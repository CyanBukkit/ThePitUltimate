package cn.charlotte.pit.events;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.util.Utils;
import cn.charlotte.pit.util.cooldown.Cooldown;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * @Author: EmptyIrony
 * @Date: 2021/1/9 13:14
 */
public class EventTimer implements Runnable {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy:MM:dd:HH:mm");
    @Setter

    private static Cooldown cooldown = new Cooldown(0);
    public static Cooldown getCooldown(){
        return cooldown;
    }
    @Override
    public void run() {

        if (!cooldown.hasExpired()) {
            return;
        }

        final String format = DATE_FORMAT.format(System.currentTimeMillis());
        final String[] split = Utils.splitByCharAt(format,':');
        final String minString = split[4];
        final EventFactory factory = ThePit.getInstance().getEventFactory();

        final int min = Integer.parseInt(minString);

        if (min == 55) {
            String major = EventsHandler.INSTANCE.nextEvent(true);
            cooldown = new Cooldown(1, TimeUnit.MINUTES);

            factory.getEpicEvents()
                    .stream()
                    .map(event -> (IEvent) event)
                    .filter(iEvent -> iEvent.getEventInternalName().equals(major))
                    .findFirst()
                    .ifPresent(iEvent -> factory.pushEvent((IEpicEvent) iEvent));
        }

        boolean b = min != 55 && min != 50;
        if(factory.getNormalEnd().hasExpired() && cooldown.hasExpired()) { // patch
            INormalEvent activeNormalEvent = factory.getActiveNormalEvent();
            if (activeNormalEvent != null) {
                factory.inactiveEvent(activeNormalEvent);
            }
        }
        if (b) {
            if (factory.getActiveEpicEvent() == null && factory.getNextEpicEvent() == null) {
                if (factory.getActiveNormalEvent() == null) {
                    String mini = EventsHandler.INSTANCE.nextEvent(false);
                    if(mini.equals("NULL")) {
                        return;
                    }
                    cooldown = new Cooldown(1, TimeUnit.MINUTES);

                    factory.getNormalEvents()
                            .stream()
                            .map(event -> (IEvent) event)
                            .filter(iEvent -> iEvent.getEventInternalName().equals(mini))
                            .findFirst()
                            .ifPresent(iEvent -> factory.pushEvent((INormalEvent) iEvent));
                }
            }
        }

    }
}
