package cn.charlotte.pit.util.hologram.packet;

import cn.charlotte.pit.util.hologram.packet.type.*;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ReflectUtil;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 2022/2/26<br>
 * LimeCode<br>
 *
 * @author huanmeng_qwq
 */
public class PacketArmorStand {
    public static final double DISTANCE = 64;
    protected String text;
    public Location location;
    protected final ArmorStand entity;
    protected final Set<Player> users;
    protected final Set<Player> viewing;
    protected final Set<Player> hiding;

    public PacketArmorStand(String text, Location location) {
        this.text = text;
        this.location = location;
        this.entity = ArmorStandHelper.memoryEntity(location);
        this.users = new ConcurrentHashSet<>();
        this.viewing = new ConcurrentHashSet<>();
        this.hiding = new ConcurrentHashSet<>();
        init();
    }

    public void init() {
        if (!text.isEmpty()) {
            entity.setCustomName(text);
            entity.setCustomNameVisible(true);
        } else {
            entity.setCustomNameVisible(false);
        }
    }

    public void invisible() {
        entity.setVisible(false);
    }

    public void small() {
        entity.setSmall(true);
    }

    public void redisplay(Player user, boolean force) {
        hide(user, force);
        show(user, force);
    }

    public void addUser(Player user) {
        if (users.add(user)) {
            show(user, false);
        }
    }

    public void removeUser(Player user) {
        if (users.remove(user)) {
            hide(user, false);
        }
        hiding.remove(user);
        viewing.remove(user);
    }

    public void removeAll() {
        ArrayList<Player> list = new ArrayList<>(users);
        for (Player user : list) {
            removeUser(user);
        }
        viewing.clear();
        hiding.clear();
        users.clear();
    }

    public void show(Player user, boolean force) {
        if (!user.getWorld().equals(location.getWorld())) {
            hiding.add(user);
            viewing.remove(user);
            return;
        }
        if (user.getLocation().distance(location) >= DISTANCE) {
            hiding.add(user);
            viewing.remove(user);
            return;
        }
        if (viewing.add(user) || force) {
            sendEquipments(user);
            hiding.remove(user);
        }
    }

    public void sendEquipments(Player user) {
        WrapperPlayServerSpawnEntityLiving spawnEntityLiving = new WrapperPlayServerSpawnEntityLiving(entity);
        spawnEntityLiving.sendPacket(user);
        {
            WrapperPlayServerEntityEquipment entityEquipment = new WrapperPlayServerEntityEquipment();
            entityEquipment.setEntityID(entity.getEntityId());
            for (Pair<EquipmentSlot, ItemStack> pair : getItems(entity)) {
                entityEquipment.setSlot(pair.getKey().ordinal());
                entityEquipment.setItem(pair.getValue());
                entityEquipment.sendPacket(user);
            }
        }
    }

    private List<Pair<EquipmentSlot, ItemStack>> getItems(LivingEntity entity) {
        List<Pair<EquipmentSlot, ItemStack>> list = new ArrayList<>(6);
        EntityEquipment equipment = entity.getEquipment();
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            switch (equipmentSlot.name()) {
                case "HAND":
                    list.add(new Pair<>(equipmentSlot, equipment.getItemInHand()));
                    break;
                case "OFF_HAND":
                    list.add(new Pair<>(equipmentSlot, ReflectUtil.invoke(equipment, "getItemInOffHand")));
                    break;
                case "FEET":
                    list.add(new Pair<>(equipmentSlot, equipment.getBoots()));
                    break;
                case "LEGS":
                    list.add(new Pair<>(equipmentSlot, equipment.getLeggings()));
                    break;
                case "CHEST":
                    list.add(new Pair<>(equipmentSlot, equipment.getChestplate()));
                    break;
                case "HEAD":
                    list.add(new Pair<>(equipmentSlot, equipment.getHelmet()));
                    break;
            }
        }
        return list;
    }

    public void hide(Player user, boolean force) {
        if (!user.getWorld().equals(location.getWorld())) {
            hiding.add(user);
            viewing.remove(user);
            return;
        }
        if (user.getLocation().distance(location) >= DISTANCE) {
            hiding.add(user);
            viewing.remove(user);
            return;
        }
        if (hiding.add(user) || force) {
            WrapperPlayServerEntityDestroy entityDestroy = new WrapperPlayServerEntityDestroy();
            entityDestroy.setEntityIds(new int[]{entity.getEntityId()});
            entityDestroy.sendPacket(user);
            viewing.remove(user);
        }
    }

    private void clean() {
        viewing.removeIf(e -> !e.isOnline());
        hiding.removeIf(e -> !e.isOnline());
        users.removeIf(e -> !e.isOnline());
    }

    public void update() {
        clean();
        updateView();
        setText(text, false);
        WrapperPlayServerEntityMetadata entityMetadata = new WrapperPlayServerEntityMetadata();
        entityMetadata.setEntityID(entity.getEntityId());
        entityMetadata.setMetadata(WrappedDataWatcher.getEntityWatcher(entity).getWatchableObjects());
        for (Player user : viewing) {
            entityMetadata.sendPacket(user);
        }
    }

    public void updateView() {
        ArrayList<Player> viewingList = new ArrayList<>(viewing);
        ArrayList<Player> hidingList = new ArrayList<>(hiding);
        for (Player user : viewingList) {
            if (!user.getWorld().equals(location.getWorld())) {
                continue;
            }
            if (user.getLocation().distance(location) >= DISTANCE) {
                hide(user, false);
            } else {
                show(user, false);
            }
        }
        for (Player user : hidingList) {
            if (!user.getWorld().equals(location.getWorld())) {
                continue;
            }
            if (user.getLocation().distance(location) < DISTANCE) {
                show(user, false);
            }
        }
    }

    public void location(@NotNull Location location) {
        ArmorStandHelper.applyLocation(location, this);
    }

    public String text() {
        return text;
    }

    public PacketArmorStand setText(String text) {
        return this.setText(text, true);
    }

    public PacketArmorStand setText(String text, boolean update) {
        if (this.text.equals(text)) {
            return this;
        }
        this.text = text;
        if (!text.isEmpty()) {
            entity.setCustomName(text);
            entity.setCustomNameVisible(true);
        } else {
            entity.setCustomNameVisible(false);
        }
        if (update) {
            update();
        }
        return this;
    }

    public PacketArmorStand setNameVisible(boolean visible) {
        entity.setCustomNameVisible(visible);
        return this;
    }

    public Location location() {
        return location;
    }

    public Set<Player> users() {
        return users;
    }

    public ArmorStand entity() {
        return entity;
    }

    public Set<Player> viewing() {
        return viewing;
    }

    public void move(Location to, boolean ground) {
        double teleportThreshold = 4;
        double distance = Math.abs(to.getX() - location.getX()) +
                Math.abs(to.getY() - location.getY()) + Math.abs(to.getZ() - location.getZ());


        if (distance > teleportThreshold) {
            this.sendTeleportPacket(to, ground);
        } else {
            boolean look = location.getYaw() != to.getYaw() || location.getPitch() != to.getPitch();
            boolean move = location.getX() != to.getX() || location.getY() != to.getY() || location.getZ() != to.getZ();
            if (look || move) {
                if (move && !look) {
                    WrapperPlayServerRelEntityMove entityMove = new WrapperPlayServerRelEntityMove();
                    entityMove.setEntityID(entity.getEntityId());
                    entityMove.setDx(to.getX(), this.location.getX());
                    entityMove.setDy(to.getY(), this.location.getY());
                    entityMove.setDz(to.getZ(), this.location.getZ());
                    entityMove.setOnGround(ground);
                    for (Player user : viewing) {
                        entityMove.sendPacket(user);
                    }
                }
                if (move && look) {
                    WrapperPlayServerRelEntityMoveLook entityMoveLook = new WrapperPlayServerRelEntityMoveLook();
                    entityMoveLook.setEntityID(entity.getEntityId());
                    entityMoveLook.setDx(to.getX(), this.location.getX());
                    entityMoveLook.setDy(to.getY(), this.location.getY());
                    entityMoveLook.setDz(to.getZ(), this.location.getZ());
                    entityMoveLook.setOnGround(ground);
                    entityMoveLook.setYaw(to.getYaw());
                    entityMoveLook.setPitch(to.getPitch());
                    for (Player user : viewing) {
                        entityMoveLook.sendPacket(user);
                    }
                }
                if (!move && look) {
                    WrapperPlayServerEntityLook entityLook = new WrapperPlayServerEntityLook();
                    entityLook.setEntityID(entity.getEntityId());
                    entityLook.setYaw(to.getYaw());
                    entityLook.setPitch(to.getPitch());
                    entityLook.setOnGround(ground);
                    for (Player user : viewing) {
                        entityLook.sendPacket(user);
                    }
                }
                senHeadYaw(to.getYaw());
            }
        }
        ArmorStandHelper.setEntityLocation(entity, to);
        this.location = to.clone();
    }

    private void sendTeleportPacket(Location location, boolean ground) {
        WrapperPlayServerEntityTeleport entityTeleport = new WrapperPlayServerEntityTeleport();
        entityTeleport.setEntityID(entity.getEntityId());
        entityTeleport.setX(location.getX());
        entityTeleport.setY(location.getY());
        entityTeleport.setZ(location.getZ());
        entityTeleport.setYaw(location.getYaw());
        entityTeleport.setPitch(location.getPitch());
        entityTeleport.setOnGround(ground);
        for (Player user : viewing) {
            entityTeleport.sendPacket(user);
        }
    }

    private static byte getFixRotation(final float yawpitch) {
        return (byte) (yawpitch * 256.0F / 360.0F);
    }

    public void senHeadYaw(float yaw) {
        WrapperPlayServerEntityHeadRotation entityHeadRotation = new WrapperPlayServerEntityHeadRotation();
        entityHeadRotation.setEntityID(entity.getEntityId());
        entityHeadRotation.setHeadYaw(getFixRotation(yaw));
        for (Player user : viewing) {
            entityHeadRotation.sendPacket(user);
        }
    }
}
