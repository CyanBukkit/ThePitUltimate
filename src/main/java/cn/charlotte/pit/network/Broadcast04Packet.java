package cn.charlotte.pit.network;

import cn.charlotte.pit.util.pidgin.packet.Packet;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @Author: EmptyIrony
 * @Date: 2021/4/25 11:02
 */
public class Broadcast04Packet extends Packet {
    private static final Gson gson = new Gson();
    private String message;

    public Broadcast04Packet(String message) {
        this.message = message;
    }

    public Broadcast04Packet() {
    }

    @Override
    public int id() {
        return 4;
    }

    @Override
    @SneakyThrows
    public JsonObject serialize() {
        final JsonObject json = new JsonObject();
        json.addProperty("body", URLEncoder.encode(message,"UTF-8"));

        return json;
    }

    @SneakyThrows
    @Override
    public void deserialize(JsonObject object) {
        final JsonElement element = object.get("body");
        this.message = URLDecoder.decode(element.getAsString(),"UTF-8");
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Broadcast04Packet)) return false;
        final Broadcast04Packet other = (Broadcast04Packet) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$message = this.getMessage();
        final Object other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Broadcast04Packet;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $message = this.getMessage();
        result = result * PRIME + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    public String toString() {
        return "Broadcast04Packet(message=" + this.getMessage() + ")";
    }
}
