package net.mizukilab.pit.data.operator;

import cn.charlotte.pit.ThePit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public interface ExecutionPolicy {
    ExecutionPolicy EXECUTION_POLICY_DEFAULT = new DefaultPolicy();
    void success(Player player);

    void fail(Player player,Throwable throwable);
    public class DefaultPolicy implements ExecutionPolicy {

        @Override
        public void success(Player player) {
            //NO-OP
        }

        @Override
        public void fail(Player player,Throwable throwable) {
            throwable.printStackTrace();
            Bukkit.getScheduler().runTask(ThePit.getInstance(), () -> player.kickPlayer("An error was occurred on your profile, please contact the administrator to get details. "));
        }
    }
}
