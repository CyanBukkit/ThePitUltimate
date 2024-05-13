package cn.charlotte.pit.network;

import cn.charlotte.pit.util.pidgin.packet.Packet;
import com.google.gson.JsonObject;

/**
 * @Author: EmptyIrony
 * @Date: 2021/3/25 18:57
 */
public class CDKActive02Packet extends Packet {
    private long timestamp;

    public CDKActive02Packet(long timestamp) {
        this.timestamp = timestamp;
    }

    public CDKActive02Packet() {
    }

    @Override
    public int id() {
        return 2;
    }

    @Override
    public JsonObject serialize() {
        final JsonObject json = new JsonObject();
        json.addProperty("timestamp", timestamp);

        return json;
    }

    @Override
    public void deserialize(JsonObject object) {

        this.timestamp = object.get("timestamp").getAsLong();

    }
}
