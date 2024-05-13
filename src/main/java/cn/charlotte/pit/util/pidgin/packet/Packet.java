package cn.charlotte.pit.util.pidgin.packet;

import com.google.gson.JsonObject;

public abstract class Packet {

	public abstract int id();

	public abstract JsonObject serialize();

	public abstract void deserialize(JsonObject object);

}
