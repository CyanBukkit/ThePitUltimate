package cn.charlotte.pit.perk.type.streak.highlander;

import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.event.PitPotionEffectEvent;
import cn.charlotte.pit.event.PitStreakKillChangeEvent;
import cn.charlotte.pit.item.type.UberReward;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.parm.listener.IPlayerBeKilledByEntity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.perk.AbstractPerk;
import cn.charlotte.pit.perk.PerkType;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.chat.MessageType;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 2022/10/18<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
@AutoRegister
public class UberMegaStreak extends AbstractPerk implements Listener, IAttackEntity, IPlayerDamaged, IPlayerBeKilledByEntity {
    @Override
    public String getInternalPerkName() {
        return "uber_mega_streak";
    }

    @Override
    public String getDisplayName() {
        return "登峰造极";
    }

    @Override
    public Material getIcon() {
        return Material.GOLD_SWORD;
    }

    @Override
    public double requireCoins() {
        return 50_000;
    }

    @Override
    public int requireRenown(int level) {
        return 50;
    }

    @Override
    public int requirePrestige() {
        return 20;
    }

    @Override
    public int requireLevel() {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }

    @Override
    public PerkType getPerkType() {
        return PerkType.MEGA_STREAK;
    }

    @Override
    public void onPerkActive(Player player) {
    }

    @Override
    public void onPerkInactive(Player player) {

    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> list = new ArrayList<>();
        list.add("&7激活要求连杀数: &c100 连杀");
        list.add(" ");
        list.add("&7激活后:");
        list.add("  &a▶ &7激活期间获得神话物品概率提升至原来的 &d150%");
        list.add("  &c▶ &7每击杀100名玩家,受到的伤害 &c+10%");
        list.add("  &c▶ &7100连杀后,攻击未精通的玩家造成的伤害 &c-40%");
        list.add("  &c▶ &7200连杀后,生命上限制 §c-2♥");
        list.add("  &c▶ &7300连杀后,所有药水效果持续时间 &c-50%");
        list.add("  &c▶ &7400连杀后,&c无法恢复生命值");
        list.add(" ");
        list.add("&7激活后死亡时:");
        list.add("  &a▶ &7若连杀大于400,获得一件 &d登峰造极凋落物");
        list.add(" ");
//        list.add("&c注意:若在400连杀前死亡")
        return list;
    }

    @EventHandler
    public void onKillStreakChange(PitStreakKillChangeEvent e) {
        PlayerProfile profile = e.getPlayerProfile();
        Player player = Bukkit.getPlayer(profile.getPlayerUuid());
        if (player == null || !player.isOnline()) {
            return;
        }
        if (!PlayerUtil.isPlayerChosePerk(player, getInternalPerkName())) {
            return;
        }
        if (e.getFrom() >= 200 && e.getTo() < 200) {
            profile.setMaxHealth(profile.getMaxHealth() + 2);
            player.setMaxHealth(profile.getMaxHealth());
        }
        if (e.getFrom() < 100 && e.getTo() >= 100) {
            CC.boardCast(MessageType.COMBAT, p -> "&c&l超级连杀! " + profile.getFormattedNameWithRoman(p) + " &7激活了 &f&l登峰造极 &7!");
            Bukkit.getOnlinePlayers().forEach(t -> t.playSound(t.getLocation(), Sound.WITHER_SPAWN, 0.8F, 1.5F));
        }
        if (e.getTo() == 200) {
            profile.setMaxHealth(profile.getMaxHealth() - 2);
            player.setMaxHealth(profile.getMaxHealth());
        } else if (e.getTo() == 300) {
            for (PotionEffect old : player.getActivePotionEffects()) {
                PotionEffect potionEffect = new PotionEffect(old.getType(), old.getDuration() / 2, old.getAmplifier(), old.isAmbient(), old.hasParticles());
                player.removePotionEffect(old.getType());
                if (potionEffect.getDuration() > 0) {
                    player.addPotionEffect(potionEffect);
                }
            }
        }
    }

    @Override
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(attacker.getUniqueId());
        if (profile.getStreakKills() >= 100) {
            if (target instanceof Player) {
                PlayerProfile targetProfile = PlayerProfile.getPlayerProfileByUuid(target.getUniqueId());
                if (targetProfile.getPrestige() <= 0) {
                    boostDamage.getAndAdd(-0.4);
                }
            }
        }
    }

    @Override
    public void handlePlayerBeKilledByEntity(int enchantLevel, Player myself, Entity target, AtomicDouble coins, AtomicDouble experience) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(myself.getUniqueId());
        if (profile.getStreakKills() >= 400 && profile.getTodayUberUesed() < 4) {
            int renown = Math.max(3, profile.getTodayUberUesed() * 5);
            if (profile.getRenown() < renown) {
                myself.sendMessage("§d§lUber! §c由于你没有 §e" + renown + "声望§c,无法获得凋落物!");
                return;
            }
            profile.setRenown(profile.getRenown() - renown);
            profile.setTodayUberUesed(profile.getTodayUberUesed() + 1);
            sync(() -> {
                ItemStack itemStack = UberReward.toItemStack();
                myself.sendMessage("§d§lUber! §7你获得了一个§d登峰造极掉落物");
                myself.getInventory().addItem(itemStack);
            }, 10);
        }
    }

    @Override
    public void handlePlayerDamaged(int enchantLevel, Player myself, Entity attacker, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(myself.getUniqueId());
        if (profile.getStreakKills() >= 100) {
            boostDamage.addAndGet(0.1 * (profile.getStreakKills() / 100));
        }
    }

    @EventHandler
    public void onApplyPotion(PitPotionEffectEvent e) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(e.getPlayer().getUniqueId());
        if (profile.getStreakKills() >= 300) {
            PotionEffect old = e.getPotionEffect();
            e.setPotionEffect(new PotionEffect(old.getType(), old.getDuration() / 2, old.getAmplifier(), old.isAmbient(), old.hasParticles()));
        }
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(e.getEntity().getUniqueId());
        if (profile.getStreakKills() >= 400) {
            e.setCancelled(true);
        }
    }
}
