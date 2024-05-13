package cn.charlotte.pit.menu.shop.button;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.util.command.util.ClassUtil;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Creator Misoryan
 * @Date 2021/6/4 21:58
 */
@Getter
public class ShopButtonFactory {
    private final List<AbstractShopButton> buttons;

    public ShopButtonFactory() {
        this.buttons = new ArrayList<>();
    }

    @SneakyThrows
    public void init() {
        Collection<Class<?>> classes = ClassUtil.getClassesInPackage(ThePit.getInstance(), "cn.charlotte.pit.menu.shop.button.type");
        for (Class<?> clazz : classes) {
            if (AbstractShopButton.class.isAssignableFrom(clazz)) {
                Object instance = clazz.newInstance();
                buttons.add((AbstractShopButton) instance);
            }
        }
    }
}
