package cn.charlotte.pit.events;

import cn.charlotte.pit.ThePit;
import lombok.Setter;
import net.mizukilab.pit.util.cooldown.Cooldown;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @Author: EmptyIrony
 * @Date: 2021/1/9 13:14
 */
public class EventTimer implements Runnable {

    @Setter
    private Cooldown cooldown = new Cooldown(0);

    public Cooldown getCooldown() {
        return cooldown;
    }

    @Override
    public void run() {

        if (!cooldown.hasExpired()) {
            return;
        }

        final EventFactory factory = ThePit.getInstance().getEventFactory();
        if (factory.getActiveEpicEvent() != null || factory.getActiveNormalEvent() != null) {
            cooldown.reset();
        }
        final int min = LocalDateTime.now().getMinute();
        if (min == 55) {
            String major = EventsHandler.INSTANCE.nextEvent(true);
            cooldown = new Cooldown(3, TimeUnit.MINUTES);
            factory.getEpicEvents()
                    .stream()
                    .map(event -> (AbstractEvent) event)
                    .filter(iEvent -> iEvent.getEventInternalName().equals(major))
                    .findFirst()
                    .ifPresent(iEvent -> factory.pushEvent((IEpicEvent) iEvent));
        }

        boolean b = min != 55 && min != 50;
        if (factory.getNormalEnd().hasExpired()) { // patch
            INormalEvent activeNormalEvent = factory.getActiveNormalEvent();
            if (activeNormalEvent != null) {
                factory.inactiveEvent(activeNormalEvent);
            }

            cooldown = new Cooldown(3, TimeUnit.MINUTES);
        }
        if (b) {
            if (factory.getActiveEpicEvent() == null && factory.getNextEpicEvent() == null) {
                if (factory.getActiveNormalEvent() == null) {
                    String mini = EventsHandler.INSTANCE.nextEvent(false);
                    if (mini.equals("NULL")) {
                        return;
                    }
                    cooldown = new Cooldown(1, TimeUnit.MINUTES);

                    factory.getNormalEvents()
                            .stream()
                            .map(event -> (AbstractEvent) event)
                            .filter(iEvent -> iEvent.getEventInternalName().equals(mini))
                            .findFirst()
                            .ifPresent(iEvent -> factory.pushEvent((INormalEvent) iEvent));
                }
            }
        }

    }
}
