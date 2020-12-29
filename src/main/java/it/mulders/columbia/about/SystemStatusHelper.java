package it.mulders.columbia.about;

import it.mulders.columbia.shared.ui.ByteCountHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

@AllArgsConstructor(onConstructor = @__({ @Autowired }))
@Component
@Slf4j
public class SystemStatusHelper {
    private final BuildProperties buildProperties;
    private final DataSource datasource;
    private final Runtime runtime = Runtime.getRuntime();

    public SystemStatus systemStatus() {
        var javaVersion = String.format("%s %s",
                System.getProperty("java.vm.vendor"), System.getProperty("java.version"));
        var os = String.format("%s %s (%s bits)",
                System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("sun.arch.data.model"));

        return SystemStatus.builder()
                .applicationVersion(String.format("%s (%s)", buildProperties.getVersion(), buildProperties.get("build.sha1")))
                .availableMem(ByteCountHelper.humanReadableByteCount(runtime.totalMemory()))
                .databaseInfo(determineDatabaseInfo())
                .freeMem(ByteCountHelper.humanReadableByteCount(runtime.freeMemory()))
                .javaVersion(javaVersion)
                .os(os)
                .build();
    }

    private String determineDatabaseInfo() {
        try (var connection = datasource.getConnection()) {
            var metadata = connection.getMetaData();
            return String.format("%s %s", metadata.getDatabaseProductName(), metadata.getDatabaseMajorVersion());
        } catch (final SQLException e) {
            log.error("Could not connect to database", e);
            return "Error connecting to database: " + e.getMessage();
        }
    }
}
