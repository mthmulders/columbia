package it.mulders.columbia.shared.ui;

import lombok.AllArgsConstructor;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

import static it.mulders.columbia.shared.ui.ByteCountHelper.humanReadableByteCount;

@AllArgsConstructor
public class BytesNumberFormat extends NumberFormat {
    private final int precision;

    @Override
    public StringBuffer format(final double number, final StringBuffer result, final FieldPosition pos) {
        result.append(humanReadableByteCount((long) number, precision));
        return result;
    }

    @Override
    public StringBuffer format(final long number, final StringBuffer result, final FieldPosition pos) {
        result.append(humanReadableByteCount(number, precision));
        return result;
    }

    @Override
    public Number parse(final String source, final ParsePosition parsePosition) {
        throw new UnsupportedOperationException();
    }
}
