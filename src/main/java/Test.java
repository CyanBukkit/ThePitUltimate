import java.util.Calendar;
import java.util.Locale;

/**
 * 2022/10/18<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
public class Test {
    static Calendar GUOQING_END = Calendar.getInstance();
    static Calendar GUOQING_START = Calendar.getInstance();

    static {
        GUOQING_END.set(Calendar.MONTH, 10);
        GUOQING_END.set(Calendar.DAY_OF_MONTH, 7);
        GUOQING_END.set(Calendar.HOUR_OF_DAY, 23);
        GUOQING_END.set(Calendar.MINUTE, 59);
        GUOQING_END.set(Calendar.SECOND, 59);

        GUOQING_END.set(Calendar.MONTH, 10);
        GUOQING_END.set(Calendar.DAY_OF_MONTH, 1);
        GUOQING_END.set(Calendar.HOUR_OF_DAY, 0);
        GUOQING_END.set(Calendar.MINUTE, 0);
        GUOQING_END.set(Calendar.SECOND, 0);
    }

    public static void main(String[] args) {
        System.out.println(isGuoQing());
    }

    public static boolean isGuoQing() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        return calendar.before(GUOQING_END) && calendar.after(GUOQING_START);
    }

}
