package va.rit.teho.controller.helper;

import java.text.DecimalFormat;

public class Formatter {

    private Formatter() {
    }

    private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#.###");

    public static String formatDoubleAsString(Double d) {
        return DECIMAL_FORMATTER.format(d);
    }

    public static Double formatDouble(Double d) {
        return Double.valueOf(formatDoubleAsString(d));
    }
}
