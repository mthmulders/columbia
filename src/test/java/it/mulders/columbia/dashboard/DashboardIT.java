package it.mulders.columbia.dashboard;

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

import static java.util.stream.Collectors.toList;
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
                        .build(),
                Vault.builder()
                        .arn("arn:aws:glacier:eu-central-1:486007468057:vaults/Example_Vault_2")
                        .archiveCount(6)
                        .name("Example_Vault_2")
                        .build()
        ));
        prepareKaribu();
    }

    @Test
    void should_display_metrics() {
        _get(Div.class, spec -> spec.withText("Number of vaults")).getParent().ifPresent(counter -> {
            var value = (Div) counter.getChildren().collect(toList()).get(1);
            assertThat(value.getText()).isEqualTo("2");
        });

        _get(Div.class, spec -> spec.withText("Number of archives")).getParent().ifPresent(counter -> {
            var value = (Div) counter.getChildren().collect(toList()).get(1);
            assertThat(value.getText()).isEqualTo("9");
        });
    }
}