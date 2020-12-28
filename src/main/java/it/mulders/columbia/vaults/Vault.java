package it.mulders.columbia.vaults;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@EqualsAndHashCode
@Getter
public class Vault {
    private final long archiveCount;
    private final String arn;
    private final String name;
}
