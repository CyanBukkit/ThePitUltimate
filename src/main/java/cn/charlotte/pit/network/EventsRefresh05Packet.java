package cn.charlotte.pit.network;

import cn.charlotte.pit.util.pidgin.packet.Packet;
import com.google.gson.JsonObject;

public class EventsRefresh05Packet extends Packet {
    private long timestamp;

    public EventsRefresh05Packet(long timestamp) {
        this.timestamp = timestamp;
    }

    public EventsRefresh05Packet() {
    }

    @Override
    public int id() {
        return 5;
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
