package it.mulders.columbia.about;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SystemStatus {
    private final String applicationVersion;
    private final String availableMem;
    private final String databaseInfo;
    private final String freeMem;
    private final String javaVersion;
    private final String os;
}
