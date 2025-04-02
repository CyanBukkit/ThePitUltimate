package cn.charlotte.pit.enchantment;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.param.item.BowOnly;
import cn.charlotte.pit.enchantment.param.item.RodOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.item.IMythicItem;
import cn.charlotte.pit.util.Utils;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.time.TimeUtil;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 2 * @Author: EmptyIrony
 * 3 * @Date: 2020/12/28 23:21
 * 4
 */

@Getter
public abstract class AbstractEnchantment {

    public abstract String getEnchantName();

    public abstract int getMaxEnchantLevel();

    public abstract String getNbtName();

    //Todo: 对重制后的附魔系统的所有附魔稀有度分级
    public abstract EnchantmentRarity getRarity();

    //Todo: 附魔触发后的冷却时间
    @Nullable
    public abstract Cooldown getCooldown();

    //填写此玩家触发附魔的所需冷却时间
    public String getCooldownActionText(Cooldown cooldown) {
        return (cooldown.hasExpired() ? "&a&l✔" : "&c&l" + TimeUtil.millisToRoundedTime(cooldown.getRemaining()).replace(" ", ""));
    }

    //填写每x次攻击触发
    public String getHitActionText(Player player, int activeHit) {
        int hit = (player.getItemInHand() != null && player.getItemInHand().getType() == Material.BOW ? PlayerProfile.getPlayerProfileByUuid(player.getUniqueId()).getBowHit() : PlayerProfile.getPlayerProfileByUuid(player.getUniqueId()).getMeleeHit());
        return (hit % activeHit == 0 ? "&a&l✔" : "&e&l" + (activeHit - hit % activeHit));
    }

    //Todo: 需要一个判断玩家身上附魔是否生效中(持续时间内)的方法 (虽然也许不应该写在这里)
    public int getItemEnchantLevel(IMythicItem im){
        if(im == null)
            return -1;
        int anInt = im.getEnchantments().getInt(this);
        return anInt;
    }
    public int getItemEnchantLevel(ItemStack item) {
        IMythicItem iMythicItem = ThePit.getInstance().getItemFactory().getIMythicItem(item); //更快Or 更慢
        if(iMythicItem != null){
            return iMythicItem.getEnchantments().getInt(this);
        } //更快的解析
        if (item == null || item.getType() == Material.AIR) {
            return -1;
        }
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = Utils.toNMStackQuick(item);
        NBTTagCompound tag = nmsItem.getTag();
        if (tag == null) {
            return -1;
        }
        NBTBase extra = tag.get("extra");
        if (!(extra instanceof NBTTagCompound) || !((NBTTagCompound) extra).hasKey("ench")) {
            return -1;
        }
        NBTTagList list = ((NBTTagCompound) extra)
                .getList("ench", 8);

        if (list == null || list.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            String string = list.getString(i);
            String[] split = Utils.splitByCharAt(string,':');
            if (split.length != 2) {
                return -1;
            }
            if (split[0].equals(this.getNbtName())) {
                return Integer.parseInt(split[1]);
            }
        }
        return -1;
    }

    public boolean canApply(ItemStack item) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = Utils.toNMStackQuick(item);
        if (nmsItem.getItem() instanceof ItemSword) {
            return this.getClass().isAnnotationPresent(WeaponOnly.class);
        }
        if (nmsItem.getItem() instanceof ItemBow) {
            return this.getClass().isAnnotationPresent(BowOnly.class);
        }
        if (nmsItem.getItem() instanceof ItemArmor) {
            return this.getClass().isAnnotationPresent(ArmorOnly.class);
        }
        if (nmsItem.getItem() instanceof ItemFishingRod) {
            return this.getClass().isAnnotationPresent(RodOnly.class);
        }
        return false;
    }

    public boolean isItemHasEnchant(ItemStack itemStack) {
        return this.getItemEnchantLevel(itemStack) != -1;
    }

    public boolean isItemHasEnchant(IMythicItem itemStack) {
        return this.getItemEnchantLevel(itemStack) != -1;
    }



    public abstract String getUsefulnessLore(int enchantLevel);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractEnchantment that = (AbstractEnchantment) o;
        return that.getNbtName().equals(this.getNbtName());
    }
    //return the hashcode ewe
    int hashCode = 0,cached = -1;
    @Override
    public int hashCode() {
        if(cached == -1){
            hashCode = this.getNbtName().hashCode();
            cached = 0;
        }
        return hashCode;
    }
}
