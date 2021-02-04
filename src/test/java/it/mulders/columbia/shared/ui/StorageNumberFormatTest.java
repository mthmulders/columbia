package it.mulders.columbia.shared.ui;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StorageNumberFormatTest implements WithAssertions {
    private String formatLong(final int precision, final long input) {
        var output = new StringBuffer();
        new StorageNumberFormat(precision).format(input, output, null);
        return output.toString();
    }

    private String formatDouble(final int precision, final double input) {
        var output = new StringBuffer();
        new StorageNumberFormat(precision).format(input, output, null);
        return output.toString();
    }

    @Test
    void should_format_double_with_zero_precision() {
        assertThat(formatDouble(0, 3.14)).isEqualTo("3 B");
    }

    @Test
    void should_format_double_with_one_precision() {
        assertThat(formatDouble(1, 3692.14)).isEqualTo("3.6 KB");
    }

    @Test
    void should_format_long_with_zero_precision() {
        assertThat(formatLong(0, 314)).isEqualTo("314 B");
    }

    @Test
    void should_format_long_with_one_precision() {
        assertThat(formatLong(1, 36921496)).isEqualTo("35.2 MB");
    }
}