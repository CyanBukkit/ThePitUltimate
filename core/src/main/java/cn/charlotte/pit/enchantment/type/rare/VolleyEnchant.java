package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.BowOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.Utils;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.item.ItemBuilder;
import io.irina.backports.utils.SWMRHashTable;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.ItemBow;
import net.minecraft.server.v1_8_R3.ItemStack;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Misoryan
 * @Created_In: 2021/3/8 16:55
 */

@AutoRegister
@BowOnly
public class VolleyEnchant extends AbstractEnchantment implements Listener {

    private static final Map<UUID, Cooldown> cooldown = new SWMRHashTable<>();

    private final Field playerUsingFiled;
    private Object unreflected = null;

    @SneakyThrows
    public VolleyEnchant() {
        this.playerUsingFiled = EntityHuman.class.getDeclaredField("h");
        this.playerUsingFiled.setAccessible(true);
        try {
            VarHandle varHandle = MethodHandles.lookup().unreflectVarHandle(playerUsingFiled);
            unreflected = varHandle;
        } catch (Throwable e) {
            unreflected = null;
        }
    }

    @Override
    public String getEnchantName() {
        return "连续射击";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "volley_enchant";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7射箭时同时射出 &e" + (enchantLevel + 2) + " &7支箭矢";
    }

    private Map<UUID, Boolean> isShooting = new SWMRHashTable<>();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        cooldown.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    @SneakyThrows
    public void onInteract(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (PlayerUtil.shouldIgnoreEnchant(player)) return;
        final org.bukkit.inventory.ItemStack itemInHand = player.getItemInHand();
        if (itemInHand == null) return;
        final int level = Math.min(3, this.getItemEnchantLevel(itemInHand));
        if (level == -1) {
            return;
        }
        if (isShooting.getOrDefault(player.getUniqueId(), false)) {
            return;
        }
        if (itemInHand.getType() == Material.BOW) {
            try {
                if (cooldown.getOrDefault(player.getUniqueId(), new Cooldown(0)).hasExpired()) {

                    //shoot 5 arrows need 400ms u suck why u set it to 200ms

                    final ItemStack item = Utils.toNMStackQuick(itemInHand);
                    final ItemBow bow = (ItemBow) item.getItem();

                    final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

                    final int value;
                    if (unreflected != null) {
                        value = (int) ((VarHandle) unreflected).get(entityPlayer);
                    } else {
                        value = (int) this.playerUsingFiled.get(entityPlayer);
                    }
                    ItemBuilder arrowBuilder = new ItemBuilder(Material.ARROW).internalName("default_arrow").defaultItem().canDrop(false).canSaveToEnderChest(false);

                    //let shooting multiple arrows cost 1 arrow at once
                    player.getInventory().addItem(arrowBuilder.amount(level + 1).build());

                    new BukkitRunnable() {
                        int tick = 0;

                        {
                            isShooting.put(player.getUniqueId(), true);
                        }

                        @Override
                        public void run() {
                            if (tick >= level) {
                                cooldown.put(player.getUniqueId(), new Cooldown(15L, TimeUnit.MILLISECONDS));
                                isShooting.remove(player.getUniqueId());
                                this.cancel();
                            } else {
                                bow.a(item, entityPlayer.world, entityPlayer, value);
                            }
                            tick++;
                        }
                    }.runTaskTimer(ThePit.getInstance(), 0, 2);
                }
            } catch (Exception e) {
                CC.printError(player, e);
            }
        }
    }
}
