package net.mizukilab.pit.npc.type;

import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.skin.Skin;
import net.mizukilab.pit.config.NewConfiguration;
import net.mizukilab.pit.npc.AbstractPitNPC;

public abstract class SkinNPC extends AbstractPitNPC {

    @Override
    public void initSkin(NPC npc) {
        final String skinValue = NewConfiguration.INSTANCE.getConfig().getString(this.getNpcInternalName() + "-npc-skin");
        if (skinValue != null) {
            String signature = NewConfiguration.INSTANCE
                    .getConfig()
                    .getString("not-netease-skins." + getNpcInternalName() + "-signature");
            npc.setSkin(new Skin(skinValue, signature));
        }
    }
}
