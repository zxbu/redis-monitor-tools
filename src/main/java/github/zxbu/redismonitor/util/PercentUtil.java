package github.zxbu.redismonitor.util;

public class PercentUtil {
    public static String format(double divisor, double dividend) {
        return String.format("%.2f", divisor / dividend * 100 ) + "%";
    }
}
