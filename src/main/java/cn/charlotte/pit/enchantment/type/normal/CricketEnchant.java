package cn.charlotte.pit.enchantment.type.normal;

import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.parm.listener.ITickTask;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.klee.backports.utils.SWMRHashTable;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.AtomicDouble;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
@AutoRegister
public class CricketEnchant extends AbstractEnchantment implements ITickTask, IPlayerDamaged, IActionDisplayEnchant {
    Set<UUID> crickets = new ObjectOpenHashSet<>();
    @Override
    public String getEnchantName() {
        return "蟋蟀";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "cricket";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.NORMAL;
    }

    @Nullable
    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return switch (enchantLevel) {
            case 1 -> "&7站在 &a草方块 &7上时 , 受到的伤害 &9-5%/s" +
                    "&7并获得永久 &c生命恢复 I";
            case 2 -> "&7站在 &a草方块 &7上时 , 受到的伤害 &9-10%/s" +
                    "&7并获得永久 &c生命恢复 I";
            case 3 -> "&7站在 &a草方块 &7上时 , 受到的伤害 &9-15%/s" +
                    "&7并获得永久 &c生命恢复 I";
            default -> "ERROR";
        };
    }
    PotionEffect L1 = PotionEffectType.REGENERATION.createEffect(100,0);
    @Override
    public void handle(int enchantLevel, Player player) {
        UUID uniqueId = player.getUniqueId();

        if(player.getLocation().clone().add(0,-1,0).getBlock().getType() == Material.GRASS) {
            player.addPotionEffect(L1,true);
            crickets.add(uniqueId);
        } else {
            crickets.remove(uniqueId);
        }
    }

    @Override
    public int loopTick(int enchantLevel) {
        return 20;
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        this.crickets.remove(e.getPlayer().getUniqueId());
    }
    @Override
    public void handlePlayerDamaged(int enchantLevel, Player myself, Entity attacker, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        if(crickets.contains(myself.getUniqueId())){
            boostDamage.set(boostDamage.get() - switch (enchantLevel) {
                case 1 -> 0.05;
                case 2 -> 0.1;
                case 3 -> 0.15;
                default -> 0;
            });
        }
    }

    @Override
    public String getText(int level, Player player) {
        return crickets.contains(player.getUniqueId()) ? "&a&l✔" : "&c&l✘";
    }
}
