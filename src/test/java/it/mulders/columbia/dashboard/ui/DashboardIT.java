package it.mulders.columbia.dashboard.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import it.mulders.columbia.AbstractKaribuTest;
import it.mulders.columbia.IntegrationTest;
import it.mulders.columbia.shared.TechnicalException;
import it.mulders.columbia.vaults.Vault;
import it.mulders.columbia.vaults.VaultService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;

import static java.util.stream.Collectors.joining;
import static org.mockito.Mockito.when;

@IntegrationTest
@Slf4j
class DashboardIT extends AbstractKaribuTest implements WithAssertions {
    @MockBean
    private VaultService vaultService;

    @BeforeEach
    @SneakyThrows(TechnicalException.class)
    void prepare() {
        when(vaultService.listVaults()).thenReturn(List.of(
                Vault.builder()
                        .arn("arn:aws:glacier:eu-central-1:460046808775:vaults/Example_Vault_1")
                        .archiveCount(3)
                        .name("Example_Vault_1")
                        .sizeInBytes(1024L)
                        .build(),
                Vault.builder()
                        .arn("arn:aws:glacier:eu-central-1:486007468057:vaults/Example_Vault_2")
                        .archiveCount(6)
                        .name("Example_Vault_2")
                        .sizeInBytes(2304L)
                        .build()
        ));
        prepareKaribu();
    }

    private String getText(final Component component) {
        return component.getChildren()
                .filter(Div.class::isInstance)
                .map(Div.class::cast)
                .map(Div::getText)
                .collect(joining(" "));
    }

    @Test
    void should_display_metrics() {
        var vaultCounter = _get(Counter.class, spec -> spec.withId("num-vaults"));
        assertThat(getText(vaultCounter)).contains("2");

        var archiveCounter = _get(Counter.class, spec -> spec.withId("num-archives"));
        assertThat(getText(archiveCounter)).contains("9");

        var sizeCounter = _get(Counter.class, spec -> spec.withId("archive-size"));
        assertThat(getText(sizeCounter)).contains("3.25 KB");
    }
}