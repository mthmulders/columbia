package it.mulders.columbia.about;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.info.BuildProperties;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("System Status Helper")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SystemStatusHelperTest implements WithAssertions {
    private final Properties properties = new Properties();
    private final DataSource datasource = mock(DataSource.class);

    @BeforeEach
    void setup() throws SQLException {
        var connection = mock( Connection.class);
        var metadata = mock( DatabaseMetaData.class);
        when(connection.getMetaData()).thenReturn(metadata);
        when(datasource.getConnection()).thenReturn(connection);
    }

    @Test
    void should_report_application_version() {
        // Arrange
        properties.setProperty("version", "1.0"); // Seems Spring strips off the 'build.' prefix
        properties.setProperty("build.sha1", "revision 12345abc");
        var helper = new SystemStatusHelper(new BuildProperties(properties), datasource);

        // Act
        var result = helper.systemStatus();

        // Assert
        assertThat(result.getApplicationVersion()).isEqualTo("1.0 (revision 12345abc)");
    }

    @Test
    void should_report_available_memory() {
        // Arrange
        var helper = new SystemStatusHelper(new BuildProperties(properties), datasource);

        // Act
        var result = helper.systemStatus();

        // Assert
        assertThat(result.getAvailableMem()).isNotNull();
    }

    @Test
    void should_report_database_info() throws SQLException {
        // Arrange
        when(datasource.getConnection().getMetaData().getDatabaseProductName()).thenReturn("PostgreSQL");
        when(datasource.getConnection().getMetaData().getDatabaseMajorVersion()).thenReturn(10);
        var helper = new SystemStatusHelper(new BuildProperties(properties), datasource);

        // Act
        var result = helper.systemStatus();

        // Assert
        assertThat(result.getDatabaseInfo()).isEqualTo("PostgreSQL 10");
    }

    @Test
    void should_report_database_info_without_database_connection() throws SQLException {
        // Arrange
        when(datasource.getConnection()).thenThrow(SQLException.class);
        var helper = new SystemStatusHelper(new BuildProperties(properties), datasource);

        // Act
        var result = helper.systemStatus();

        // Assert
        assertThat(result.getDatabaseInfo()).contains("Error connecting to database");
    }

    @Test
    void should_report_free_memory() {
        // Arrange
        var helper = new SystemStatusHelper(new BuildProperties(properties), datasource);

        // Act
        var result = helper.systemStatus();

        // Assert
        assertThat(result.getFreeMem()).isNotNull();
    }

    @Test
    void should_report_java_version() {
        // Arrange
        var helper = new SystemStatusHelper(new BuildProperties(properties), datasource);

        // Act
        var result = helper.systemStatus();

        // Assert
        assertThat(result.getJavaVersion()).isNotNull();
    }

    @Test
    void should_report_operating_system() {
        // Arrange
        var helper = new SystemStatusHelper(new BuildProperties(properties), datasource);

        // Act
        var result = helper.systemStatus();

        // Assert
        assertThat(result.getOs()).isNotNull();
    }

    @ParameterizedTest
    @CsvSource(value = {"1:1 B", "1024:1 KB", "1048576:1 MB", "1073741824:1 GB"}, delimiter = ':')
    void human_readable_byte_count(final String input, final String output) {
        var helper = new SystemStatusHelper(new BuildProperties(properties), datasource);
        var result = helper.humanReadableByteCount(Long.parseLong(input));
        assertThat(result).isEqualTo(output);
    }
}