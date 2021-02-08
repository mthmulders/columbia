package it.mulders.columbia.shared.ui;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Helper class for displaying human-friendly numbers of bytes.
 */
@UtilityClass
public class ByteCountHelper {
    private static final long UNIT = 1024;
    private static final String UNITS = "KMGTPE";

    private static String format(final double number, final int precision) {
        if (precision == 0) {
            return Integer.toString((int) number);
        } else {
            var zeroes = StringUtils.repeat('#', precision);
            var symbols = DecimalFormatSymbols.getInstance(Locale.UK);
            var format = new DecimalFormat("#,###." + zeroes, symbols);
            return format.format(number);
        }
    }

    public static String humanReadableByteCount(final long bytes, final int precision) {
        if (bytes < UNIT) {
            return bytes + " B";
        } else {
            var exp = (int) (Math.log(bytes) / Math.log(UNIT));
            var prefix = UNITS.charAt(exp - 1);
            var amount = (bytes / Math.pow(UNIT, exp));

            return format(amount, precision) + " " + prefix + "B";
        }
    }

    public static String humanReadableByteCount(final long bytes) {
        return humanReadableByteCount(bytes, 0);
    }
}
