package cn.charlotte.pit.util.pidgin;

import cn.charlotte.pit.util.pidgin.packet.Packet;
import cn.charlotte.pit.util.pidgin.packet.handler.IncomingPacketHandler;
import cn.charlotte.pit.util.pidgin.packet.handler.PacketExceptionHandler;
import cn.charlotte.pit.util.pidgin.packet.listener.PacketListener;
import cn.charlotte.pit.util.pidgin.packet.listener.PacketListenerData;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

public class Pidgin {

    private static final JsonParser PARSER = new JsonParser();

    private final String channel;
    private final JedisPool jedisPool;
    private final Map<Integer, Class> idToType = new HashMap<>();
    private final Map<Class, Integer> typeToId = new HashMap<>();
    private final String host;
    private final String password;
    private JedisPubSub jedisPubSub;
    private List<PacketListenerData> packetListeners = new ArrayList<>();

    public Pidgin(String channel, String host, int port, String password) {
        this.channel = channel;
        this.host = host;
        this.password = password;
        this.packetListeners = new ArrayList<>();

        this.jedisPool = new JedisPool(host, port);

        if (password != null && !"".equals(password)) {
            try (Jedis jedis = this.jedisPool.getResource()) {
                jedis.auth(password);
                System.out.println("[Pidgin] Authenticating..");
            }
        }

        this.setupPubSub();
    }

    public void sendPacket(Packet packet) {
        sendPacket(packet, null);
    }

    public void sendPacket(Packet packet, PacketExceptionHandler exceptionHandler) {
        try {

            final JsonObject object = packet.serialize();

            if (object == null) {
                throw new IllegalStateException("Packet cannot generate null serialized data");
            }

            try (Jedis jedis = this.jedisPool.getResource()) {

                //System.out.println("[Pidgin] Attempting to publish packet..");

                try {

                    if (password != null && !password.equals("")) {
                        jedis.auth(password);
                    }

                    jedis.publish(this.channel, packet.id() + ";" + object);
                    //System.out.println("[Pidgin] SuccessfullySound published packet..");

                } catch (Exception ex) {
                    System.out.println("[Pidgin] Failed to publish packet..");
                    ex.printStackTrace();
                }

            }
        } catch (Exception e) {
            if (exceptionHandler != null) {
                exceptionHandler.onException(e);
            }
        }

    }

    public Packet buildPacket(int id) {
        if (!idToType.containsKey(id)) {
            throw new IllegalStateException("A packet with that ID does not exist: " + id);
        }

        try {
            return (Packet) idToType.get(id).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new IllegalStateException("Could not create new instance of packet type");
    }

    public void registerPacket(Class clazz) {
        try {
            int id = (int) clazz.getDeclaredMethod("id").invoke(clazz.newInstance(), null);

            if (idToType.containsKey(id) || typeToId.containsKey(clazz)) {
                throw new IllegalStateException("A packet with that ID has already been registered");
            }

            idToType.put(id, clazz);
            typeToId.put(clazz, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerListener(PacketListener packetListener) {
        methodLoop:
        for (Method method : packetListener.getClass().getDeclaredMethods()) {
            if (method.getDeclaredAnnotation(IncomingPacketHandler.class) != null) {
                Class packetClass = null;

                if (method.getParameters().length > 0) {
                    if (Packet.class.isAssignableFrom(method.getParameters()[0].getType())) {
                        packetClass = method.getParameters()[0].getType();
                    }
                }

                if (packetClass != null) {
                    this.packetListeners.add(new PacketListenerData(packetListener, method, packetClass));
                }
            }
        }
    }

    private void setupPubSub() {
        System.out.println("[Pidgin] Setting up PubSup..");

        this.jedisPubSub = new JedisPubSub() {

            @Override
            public void onMessage(String channel, String message) {

                if (channel.equalsIgnoreCase(Pidgin.this.channel)) {

                    try {

                        String[] args = message.split(";", 2);

                        int id = Integer.parseInt(args[0]);

                        Packet packet = null;
                        try {
                            packet = buildPacket(id);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (packet != null) {
                            packet.deserialize(PARSER.parse(args[1]).getAsJsonObject());

                            for (PacketListenerData data : packetListeners) {
                                if (data.matches(packet)) {
                                    data.getMethod().invoke(data.getInstance(), packet);
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("[Pidgin] Failed to handle message");
                        System.out.println(message);
                        e.printStackTrace();
                    }
                }
            }

        };

        ForkJoinPool.commonPool().execute(() -> {
            try (Jedis jedis = this.jedisPool.getResource()) {
                jedis.subscribe(this.jedisPubSub, channel);
                System.out.println("[Pidgin] SuccessfullySound subscribing to channel..");
            } catch (Exception exception) {
                System.out.println("[Pidgin] Failed to subscribe to channel..");
                exception.printStackTrace();
            }
        });
    }

}
