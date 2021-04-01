package va.rit.teho.controller.helper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Formatter {

    private static final DecimalFormat DECIMAL_FORMATTER =
            new DecimalFormat("#" + DecimalFormatSymbols.getInstance().getDecimalSeparator() + "###");

    private Formatter() {
    }

    public static String formatDoubleAsString(Double d) {
        return DECIMAL_FORMATTER.format(d);
    }

    public static Double formatDouble(Double d) {
        return Double.valueOf(formatDoubleAsString(d));
    }
}
