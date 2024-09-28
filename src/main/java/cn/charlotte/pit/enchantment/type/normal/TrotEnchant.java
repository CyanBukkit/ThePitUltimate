package cn.charlotte.pit.enchantment.type.normal;

import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.item.IMythicItem;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.command.param.defaults.PlayerParameterType;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.item.ItemUtil;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import spg.lgdev.handler.MovementHandler;
import spg.lgdev.iSpigot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: Starry_Killer
 * @Created_In: 2023/11/22 18:15
 */
@ArmorOnly
public class TrotEnchant extends AbstractEnchantment implements MovementHandler {
    private static final TrotEnchant trotEnchant = new TrotEnchant();

    @SneakyThrows
    public TrotEnchant() {
        try {
            iSpigot.INSTANCE.addMovementHandler(this);
        } catch (NoClassDefFoundError ignore) {
        }
    }
    @Override
    public String getEnchantName() {
        return "疾走";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "Trot";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.NORMAL;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7穿戴时行走速度提升 &b" + (enchantLevel == 3 ? "20%" : (enchantLevel == 2 ? "10%" : "5%"));
    }

    @Override
    public void handleUpdateLocation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {
        IMythicItem leggings = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId()).leggings;
        if (leggings != null){
            int level = trotEnchant.getItemEnchantLevel(leggings);
            if (level == 1) {
                if(player.getWalkSpeed() != 0.21F) {
                    player.setWalkSpeed(0.21F);
                }
            } else if (level == 2) {
                if(player.getWalkSpeed() != 0.22F) {
                    player.setWalkSpeed(0.22F);
                }
            } else if (level == 3) {

                if(player.getWalkSpeed() != 0.24F) {
                    player.setWalkSpeed(0.24F);
                }
            } else {
                if(player.getWalkSpeed() != 0.2F) {
                    player.setWalkSpeed(0.2F);
                }
            }

        } else {

            if(player.getWalkSpeed() != 0.2F) {
                player.setWalkSpeed(0.2F);
            }
        }
    }

    @Override
    public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {

    }
}
