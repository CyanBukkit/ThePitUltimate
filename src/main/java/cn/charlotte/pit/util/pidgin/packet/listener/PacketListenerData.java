package cn.charlotte.pit.util.pidgin.packet.listener;

import cn.charlotte.pit.util.pidgin.packet.Packet;

import java.lang.reflect.Method;

/**
 * A wrapper class that holds all the information needed to
 * identify and execute a message function.
 */
public class PacketListenerData {

    private final Object instance;
    private final Method method;
    private final Class packetClass;

    public PacketListenerData(Object instance, Method method, Class packetClass) {
        this.instance = instance;
        this.method = method;
        this.packetClass = packetClass;
    }

    public boolean matches(Packet packet) {
        return this.packetClass == packet.getClass();
    }

    public Object getInstance() {
        return this.instance;
    }

    public Method getMethod() {
        return this.method;
    }

    public Class getPacketClass() {
        return this.packetClass;
    }
}
