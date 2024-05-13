package cn.charlotte.pit.network;

import cn.charlotte.pit.util.pidgin.packet.Packet;
import com.google.gson.JsonObject;

/**
 * @Author: EmptyIrony
 * @Date: 2021/3/29 22:50
 */
public class MiniEvent03Packet extends Packet {
    private String internalName;
    private boolean ignoreOnline;

    public MiniEvent03Packet(String internalName, boolean ignoreOnline) {
        this.internalName = internalName;
        this.ignoreOnline = ignoreOnline;
    }

    public MiniEvent03Packet() {
    }

    @Override
    public int id() {
        return 3;
    }

    @Override
    public JsonObject serialize() {
        final JsonObject json = new JsonObject();
        json.addProperty("internalName", internalName);
        json.addProperty("ignoreOnline", ignoreOnline);

        return json;
    }

    @Override
    public void deserialize(JsonObject object) {
        internalName = object.get("internalName").getAsString();
        ignoreOnline = object.get("ignoreOnline").getAsBoolean();
    }

    public String getInternalName() {
        return this.internalName;
    }

    public boolean isIgnoreOnline() {
        return this.ignoreOnline;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public void setIgnoreOnline(boolean ignoreOnline) {
        this.ignoreOnline = ignoreOnline;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof MiniEvent03Packet)) return false;
        final MiniEvent03Packet other = (MiniEvent03Packet) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$internalName = this.getInternalName();
        final Object other$internalName = other.getInternalName();
        if (this$internalName == null ? other$internalName != null : !this$internalName.equals(other$internalName))
            return false;
        if (this.isIgnoreOnline() != other.isIgnoreOnline()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof MiniEvent03Packet;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $internalName = this.getInternalName();
        result = result * PRIME + ($internalName == null ? 43 : $internalName.hashCode());
        result = result * PRIME + (this.isIgnoreOnline() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "MiniEvent03Packet(internalName=" + this.getInternalName() + ", ignoreOnline=" + this.isIgnoreOnline() + ")";
    }
}
