package cn.charlotte.pit.util;

import cn.charlotte.pit.ThePit;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class ConnectServer {
    @SneakyThrows
    public static void connect(Player p, String serverName) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        p.sendPluginMessage(ThePit.getInstance(), "BungeeCord", b.toByteArray());
    }
}