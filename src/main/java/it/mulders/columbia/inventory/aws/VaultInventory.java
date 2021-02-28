package it.mulders.columbia.inventory.aws;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
public class VaultInventory {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ObjectReader reader = mapper.readerFor(Description.class);

    @AllArgsConstructor
    @Getter
    public static class Description {
        private final String path;
        private final String type;

        @JsonCreator
        public static Description ofString(final String input) throws IOException {
            return reader.readValue(input, Description.class);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class Archive {
        @JsonProperty("ArchiveId")
        private final String archiveId;
        @JsonProperty("ArchiveDescription")
        private final Description description;
        @JsonProperty("CreationDate")
        private final LocalDateTime creationDate;
        @JsonProperty("Size")
        private final long size;
        @JsonProperty("SHA256TreeHash")
        private final String sha256TreeHash;
    }

    @JsonProperty("VaultARN")
    private final String vaultARN;
    @JsonProperty("InventoryDate")
    private final LocalDateTime inventoryDate;
    @JsonProperty("ArchiveList")
    private final List<Archive> archives;
}
