package va.rit.teho.controller.helper;

import java.text.DecimalFormat;

public class Formatter {

    private Formatter() {}

    private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#.###");

    public static String formatDouble(Double d) {
        return DECIMAL_FORMATTER.format(d);
    }
}
