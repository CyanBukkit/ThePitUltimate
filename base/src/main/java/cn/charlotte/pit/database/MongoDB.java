package cn.charlotte.pit.database;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.*;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.conversions.Bson;
import org.mongojack.JacksonMongoCollection;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

/**
 * 2 * @Author: EmptyIrony
 * 3 * @Date: 2020/12/28 23:03
 * 4
 */

public final class MongoDB {

    private static final Logger log = ThePit.getInstance().getLogger();

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private JacksonMongoCollection<PlayerProfile> profileCollection;
    private JacksonMongoCollection<TradeData> tradeCollection;
    private JacksonMongoCollection<PlayerMailData> mailCollection;
    private JacksonMongoCollection<PlayerInvBackup> invCollection;
    private JacksonMongoCollection<CDKData> cdkCollection;
    private JacksonMongoCollection<FixedRewardData> rewardCollection;

    private JacksonMongoCollection<EventQueue> eventQueueCollection;

    public void connect() {
        log.info("Connecting to database... (正在连接数据库<<<<)");
        Instant connects = Instant.now();

        String address = ThePit.getInstance().getPitConfig().getMongoDBAddress();
        int port = ThePit.getInstance().getPitConfig().getMongoDBPort();

        final String mongoUser = ThePit.getInstance().getPitConfig().getMongoUser();
        final String mongoPassword = ThePit.getInstance().getPitConfig().getMongoPassword();

        final String databaseName;
        if (ThePit.getInstance().getPitConfig().getDatabaseName() == null) {
            databaseName = "thePit";
        } else {
            databaseName = ThePit.getInstance().getPitConfig().getDatabaseName();
        }

        //hook PowerOFTwo
        ConnectionString connectionString = new ConnectionString("mongodb://" + address + ":" + port);
        MongoClientSettings.Builder builder1 = MongoClientSettings.builder().serverApi(ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build()).applyConnectionString(connectionString);
        if (mongoUser != null && mongoPassword != null && !mongoUser.isEmpty() && !mongoPassword.isEmpty()) {
            final MongoCredential credential = MongoCredential.createCredential(mongoUser, databaseName, mongoPassword.toCharArray());
            MongoClientSettings thePit = builder1
                    .credential(credential).applicationName("ThePitRequiredPass")
                    .build();
            this.mongoClient = MongoClients.create(thePit);
        } else {
            MongoClientSettings thePit = builder1.applicationName("ThePitUnsafe").build();

            this.mongoClient = MongoClients.create(thePit);
        }

        this.database = mongoClient.getDatabase(databaseName);
        this.collection = database.getCollection("players");

        createIndex(collection, "uuidIndex", "uuid");

        createIndex(collection, "lowerNameIndex", "lowerName");

        final MongoCollection<Document> tradeCollection = database.getCollection("trade");
        createIndex(tradeCollection, "playerAIndex", "playerA");
        createIndex(tradeCollection, "playerBIndex", "playerB");
        createIndex(tradeCollection, "tradeUuidIndex", "tradeUuid");


        final MongoCollection<Document> invCollection = database.getCollection("inv");
        createIndex(invCollection, "uuidIndex", "uuid");
        createIndex(invCollection, "backupUuidIndex", "backupUuid");

        //create trade index
        MongoCollection<Document> trade = database.getCollection("trade");
        boolean indexFound = false;
        for (Document listIndex : trade.listIndexes()) {
            if (listIndex.get("completeTime") != null) {
                indexFound = true;
                if (listIndex.getInteger("completeTime") == -1) {
                    trade.createIndex(Filters.eq("completeTime", 1));
                }
            }
        }

        if (!indexFound) {
            trade.createIndex(Filters.eq("timeStamp", 1));
        }


        MongoCollection<Document> inv = database.getCollection("inv");
        indexFound = false;
        for (Document listIndex : inv.listIndexes()) {
            if (listIndex.get("timeStamp") != null) {
                indexFound = true;
                if (listIndex.getInteger("timeStamp") == -1) {
                    trade.createIndex(Filters.eq("timeStamp", 1));
                }
            }
        }
        if (!indexFound) {
            trade.createIndex(Filters.eq("timeStamp", 1));
        }


        JacksonMongoCollection.JacksonMongoCollectionBuilder builder = JacksonMongoCollection.builder();
        this.profileCollection = builder.build(this.database.getCollection("players", PlayerProfile.class), PlayerProfile.class, UuidRepresentation.JAVA_LEGACY);

        this.tradeCollection = builder.build(this.database.getCollection("trade", TradeData.class), TradeData.class, UuidRepresentation.JAVA_LEGACY);

        this.mailCollection = builder.build(this.database.getCollection("mail", PlayerMailData.class), PlayerMailData.class, UuidRepresentation.JAVA_LEGACY);

        this.invCollection = builder.build(this.database.getCollection("inv", PlayerInvBackup.class), PlayerInvBackup.class, UuidRepresentation.JAVA_LEGACY);

        this.cdkCollection = builder.build(this.database.getCollection("cdk", CDKData.class), CDKData.class, UuidRepresentation.JAVA_LEGACY);

        this.rewardCollection = builder.build(this.database.getCollection("reward", FixedRewardData.class), FixedRewardData.class, UuidRepresentation.JAVA_LEGACY);

        this.eventQueueCollection = builder.build(this.database.getCollection("event_queue", EventQueue.class), EventQueue.class, UuidRepresentation.JAVA_LEGACY);

        createIndex(mailCollection, "uuidIndex", "uuid");
        log.info("Connected! (连接成功>>>>)");
        log.info("Costs " + ChronoUnit.MILLIS.between(connects, Instant.now()));

//        log.info("loading cdk...");
//        CDKData.loadAllCDKFromData();
//        log.info("loaded!");
    }

    public MongoClient getMongoClient() {
        return this.mongoClient;
    }

    public MongoDatabase getDatabase() {
        return this.database;
    }

    public MongoCollection<Document> getCollection() {
        return this.collection;
    }

    public JacksonMongoCollection<PlayerProfile> getProfileCollection() {
        return this.profileCollection;
    }

    public JacksonMongoCollection<TradeData> getTradeCollection() {
        return this.tradeCollection;
    }

    public JacksonMongoCollection<PlayerMailData> getMailCollection() {
        return this.mailCollection;
    }

    public JacksonMongoCollection<PlayerInvBackup> getInvCollection() {
        return this.invCollection;
    }

    public JacksonMongoCollection<CDKData> getCdkCollection() {
        return this.cdkCollection;
    }

    public JacksonMongoCollection<FixedRewardData> getRewardCollection() {
        return this.rewardCollection;
    }

    public JacksonMongoCollection<EventQueue> getEventQueueCollection() {
        return eventQueueCollection;
    }

    private void createIndex(MongoCollection<?> collection, String indexName, String fieldName) {
        try {
            IndexOptions indexOptions = new IndexOptions().name(indexName);
            Bson index = Indexes.ascending(fieldName);
            collection.createIndex(index, indexOptions);
        } catch (Exception ignore) {

        }
    }
}
