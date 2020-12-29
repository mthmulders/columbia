package it.mulders.columbia.shared.ui;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Byte count helper")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ByteCountHelperTest implements WithAssertions {
    @ParameterizedTest
    @CsvSource(value = {"1:1 B", "1024:1 KB", "1048576:1 MB", "1073741824:1 GB", "1099511627776:1 TB"}, delimiter = ':')
    void human_readable_byte_count(final String input, final String output) {
        var result = ByteCountHelper.humanReadableByteCount(Long.parseLong(input));
        assertThat(result).isEqualTo(output);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1:3:1 B", // more decimal positions than available, should not be filled with zeroes
            "1126:1:1.1 KB",
            "1310720:3:1.25 MB",
            "1073741824:0:1 GB", // no decimal positions
            "2094796557:3:1.951 GB",
            "1699511627776:3:1.546 TB"
    }, delimiter = ':')
    void human_readable_byte_count_with_decimals(final Long input, final Integer precision, final String output) {
        var result = ByteCountHelper.humanReadableByteCount(input, precision);
        assertThat(result).isEqualTo(output);
    }
}