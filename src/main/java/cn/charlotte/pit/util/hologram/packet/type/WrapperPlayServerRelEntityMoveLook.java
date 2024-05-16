package cn.charlotte.pit.util.hologram.packet.type;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class WrapperPlayServerRelEntityMoveLook extends AbstractPacket {

    public static final PacketType TYPE = PacketType.Play.Server.REL_ENTITY_MOVE_LOOK;

    public WrapperPlayServerRelEntityMoveLook() {
        super(new PacketContainer(TYPE), TYPE);

        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerRelEntityMoveLook(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: entity's ID
     *
     * @return The current Entity ID
     */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set Entity ID.
     *
     * @param value - new value.
     */
    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     *
     * @param world - the current world of the entity.
     * @return The spawned entity.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     *
     * @param event - the packet event.
     * @return The spawned entity.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    /**
     * Retrieve DX.
     *
     * @return The current DX
     */
    public double getDx() {
        return handle.getShorts().read(0) / 4096D;
    }

    /**
     * Set DX.
     */
    public void setDx(double toX, double x) {
        handle.getBytes().write(0, (byte) (toX * 32 - x * 32));
    }

    /**
     * Retrieve DY.
     *
     * @return The current DY
     */
    public double getDy() {
        return handle.getBytes().read(1) / 32D;
    }

    /**
     * Set DY.
     */
    public void setDy(double toY, double y) {
        handle.getBytes().write(1, (byte) (toY * 32 - y * 32));
    }

    /**
     * Retrieve DZ.
     *
     * @return The current DZ
     */
    public double getDz() {
        return handle.getBytes().read(2) / 32D;
    }

    /**
     * Set DZ.
     */
    public void setDz(double toZ, double z) {
        handle.getBytes().write(2, (byte) (toZ * 32 - z * 32));
    }

    /**
     * Retrieve the yaw of the current entity.
     *
     * @return The current Yaw
     */
    public float getYaw() {
        return (handle.getBytes().read(0) * 360.F) / 256.0F;
    }

    /**
     * Set the yaw of the current entity.
     *
     * @param value - new yaw.
     */
    public void setYaw(float value) {
        handle.getBytes().write(0, (byte) (value * 256.0F / 360.0F));
    }

    /**
     * Retrieve the pitch of the current entity.
     *
     * @return The current pitch
     */
    public float getPitch() {
        return (handle.getBytes().read(1) * 360.F) / 256.0F;
    }

    /**
     * Set the pitch of the current entity.
     *
     * @param value - new pitch.
     */
    public void setPitch(float value) {
        handle.getBytes().write(1, (byte) (value * 256.0F / 360.0F));
    }

    /**
     * Retrieve On Ground.
     *
     * @return The current On Ground
     */
    public boolean getOnGround() {
        return handle.getBooleans().read(0);
    }

    /**
     * Set On Ground.
     *
     * @param value - new value.
     */
    public void setOnGround(boolean value) {
        handle.getBooleans().write(0, value);
    }
}