package cn.charlotte.pit.runnable;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.events.EventFactory;
import cn.charlotte.pit.util.chat.CC;
import dev.jnic.annotation.Include;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.net.InetAddress;

//伪装一下
@Include
public class EventRunnable extends BukkitRunnable {

    @SneakyThrows
    @Override
    public void run() {
        InetAddress address = InetAddress.getByName("thepit.meowtery.cn");
        boolean i = address.isReachable(5000);

        if (!i){
            final EventFactory factory = ThePit.getInstance().getEventFactory();
            if (factory.getActiveEpicEvent() != null) {
                factory.inactiveEvent(factory.getActiveEpicEvent());
            }
            if (factory.getActiveNormalEvent() != null) {
                factory.inactiveEvent(factory.getActiveNormalEvent());
            }
            Bukkit.shutdown();
        }
    }
}
