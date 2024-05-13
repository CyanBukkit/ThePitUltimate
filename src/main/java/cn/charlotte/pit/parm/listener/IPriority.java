package cn.charlotte.pit.parm.listener;

import java.util.List;

/**
 * 2022/9/20<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
public interface IPriority {
    int priority();

    static void sort(List<?> objects) {
        objects.sort((o1, o2) -> {
            if (o1 instanceof IPriority && o2 instanceof IPriority) {
                return Integer.compare(((IPriority) o1).priority(), ((IPriority) o2).priority());
            } else if (o1 instanceof IPriority) {
                return Integer.compare(((IPriority) o1).priority(), 100);
            } else if (o2 instanceof IPriority) {
                return Integer.compare(((IPriority) o2).priority(), 100);
            }
            return 0;
        });
    }
}
