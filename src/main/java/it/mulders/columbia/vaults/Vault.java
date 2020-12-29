package it.mulders.columbia.vaults;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@Getter
@ToString(of = "arn")
public class Vault {
    private final long archiveCount;
    private final String arn;
    private final String name;
    private final long sizeInBytes;
}
