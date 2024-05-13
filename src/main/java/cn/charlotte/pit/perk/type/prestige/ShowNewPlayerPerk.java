//package cn.charlotte.pit.perk.type.prestige;
//
//import cn.charlotte.pit.data.PlayerProfile;
//import cn.charlotte.pit.event.PitProfileLoadedEvent;
//import cn.charlotte.pit.parm.AutoRegister;
//import cn.charlotte.pit.perk.AbstractPerk;
//import cn.charlotte.pit.perk.PerkType;
//import cn.charlotte.pit.util.PlayerUtil;
//import org.bukkit.Material;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.material.MaterialData;
//
//import java.util.Collections;
//import java.util.List;
//
///**
// * 2022/8/17<br>
// * ThePit-B<br>
// *
// * @author huanmeng_qwq
// */
//@AutoRegister
//public class ShowNewPlayerPerk extends AbstractPerk implements Listener {
//    @Override
//    public String getInternalPerkName() {
//        return "ShowNewPlayer";
//    }
//
//    @Override
//    public String getDisplayName() {
//        return "保护终结者";
//    }
//
//    @Override
//    public Material getIcon() {
//        return Material.SKULL_ITEM;
//    }
//
//    @Override
//    public MaterialData getIconData() {
//        return new MaterialData(getIcon(), (byte) 3);
//    }
//
//    @Override
//    protected boolean isDisableDurability() {
//        return true;
//    }
//
//    @Override
//    public double requireCoins() {
//        return PlayerProfile.PROTECT_LEVEL * 100;
//    }
//
//    @Override
//    public int requireRenown(int level) {
//        return 1;
//    }
//
//    @Override
//    public int requirePrestige() {
//        return PlayerProfile.PROTECT_LEVEL / 10;
//    }
//
//    @Override
//    public int requireLevel() {
//        return 0;
//    }
//
//    @Override
//    public List<String> getDescription(Player player) {
//        return Collections.singletonList("&7装备天赋将显示新手保护玩家的真实ID");
//    }
//
//    @Override
//    public int getMaxLevel() {
//        return 1;
//    }
//
//    @Override
//    public PerkType getPerkType() {
//        return PerkType.PERK;
//    }
//
//    @Override
//    public void onPerkActive(Player player) {
//        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
//        if (!profile.isLoaded()) {
//            return;
//        }
//        profile.setBypassTag(true);
//        profile.updateShow();
//    }
//
//    @Override
//    public void onPerkInactive(Player player) {
//        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
//        if (!profile.isLoaded()) {
//            return;
//        }
//        profile.setBypassTag(false);
//        profile.updateShow();
//    }
//
//    @Override
//    public void onUnlock(Player player) {
//        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
//        if (!profile.isLoaded()) {
//            return;
//        }
//        profile.setBypassTag(true);
//        profile.updateShow();
//    }
//
//    @EventHandler
//    public void onProfileLoaded(PitProfileLoadedEvent e) {
//        PlayerProfile profile = e.getPlayerProfile();
//        if (PlayerUtil.isPlayerUnlockedPerk(e.getPlayer(), getInternalPerkName())) {
//            profile.setBypassTag(true);
//        }
//    }
//}
