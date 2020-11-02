package va.rit.teho.controller.helper;

import java.text.DecimalFormat;

public class Formatter {

    private static final DecimalFormat FORMATTER = new DecimalFormat("#.###");

    public static String formatDouble(Double d) {
        return FORMATTER.format(d);
    }
}
