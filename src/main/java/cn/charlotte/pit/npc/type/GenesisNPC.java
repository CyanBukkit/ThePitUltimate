package cn.charlotte.pit.npc.type;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.config.PitConfig;
import cn.charlotte.pit.events.genesis.team.GenesisTeam;
import cn.charlotte.pit.menu.genesis.GenesisMenu;
import cn.charlotte.pit.npc.AbstractPitNPC;
import com.google.common.collect.Lists;
import net.jitse.npclib.api.skin.Skin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * 2022/7/28<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
public class GenesisNPC extends AbstractPitNPC {
    private boolean angel;

    public GenesisNPC(boolean angel) {
        this.angel = angel;
    }

    @Override
    public String getNpcInternalName() {
        return "genesis";
    }

    @Override
    public List<String> getNpcDisplayName(Player player) {
        List<String> list = Arrays.asList("&e限时活动: 光暗派系", (angel ? "&f&l天使" : "&c&l恶魔"), "&e右键查看");
        return list;
    }

    @Override
    public Location getNpcSpawnLocation() {
        return angel ? ThePit.getInstance().getPitConfig().getGenesisAngelNpcLocation() : ThePit.getInstance().getPitConfig().getGenesisDemonNpcLocation();
    }

    @Override
    public Skin getNpcSkin() {
        return new Skin("ewogICJ0aW1lc3RhbXAiIDogMTY5MjQzMzYyOTY1OSwKICAicHJvZmlsZUlkIiA6ICIxZGQ4YmU5YTljMDc0OGU4OTZjZjgwNDNjNTMxMWEzNCIsCiAgInByb2ZpbGVOYW1lIiA6ICJHaWdhQ2F0VG95IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg4MzA2MTM5OWY0YzA2MDc4OGQ2MThlZjA1ZjIxOWI5M2IzN2MwYjY0ZTc2ODZkYzBhNTg4YzAxMWJmODY1ZTgiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==","qt2xvunxlAyVVsD6VAIDTw/W0E3ICwBjezRxRlL8D7kpV3JLmuaSVyOsrndIhP/81Cq8Ea+Wj1G0fJF+ZdyBCkyAAzrzgv2q7mdnwymFf/5tFDzHoDv9WxiA7LRSHxww+Jnw9EB74xq6c+JclYz5IkGxa9uBQaS+ijP+GMDqsEFfN1hULb/AvbqJpq0bjySmCHWWXdO8XZDs5/nO/OhzHTmr7dCDMSgdL+Qr8ROASoJ3YxewIjKLdkgT/Z7kl8pHFAwp7cMEOCbqtHFvgKvur4JEPLegw9sbrCi56Iu/cOICRDbBNtUOfy9QFEByed8PQRjRdESVq/SUNX9q329GrIYK+6Qtb6LIJ50ICetcvgXZfD8EYrzyQsldbvTNGJBd2xFdzrBev07q53LlUdV4Gshds7VrgajXgQthD8syD89bM/VjOhx2brERxDxeKnP2rLEgoq0Ma9aULlOWmUhEEbQ56fRMFjwYka57W/KOMMy9UJBUbNxVDhT55Zy7RpsOAlwtURdUnJctwj+jL7Xn0QHxFYm2a+fWBMfz3ubHOScy89M60/+UcejF47zmfxW0gxSSpEghrdEMXG2y3XzGRJxYVz4qUmfrZSGy8RvovXUbRROvwAqPmMSFZRtNtw0OYpf2xc/5swA0zUVOsGNG17Tb2fBwqalMPkNRHe3wL9M=");
    }

    @Override
    public void handlePlayerInteract(Player player) {
        new GenesisMenu(angel ? GenesisTeam.ANGEL : GenesisTeam.DEMON).openMenu(player);
    }

    @Override
    public ItemStack getNpcHeldItem() {
        return null;
    }
}
