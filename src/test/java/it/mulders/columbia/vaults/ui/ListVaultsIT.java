package it.mulders.columbia.vaults.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import it.mulders.columbia.AbstractKaribuTest;
import it.mulders.columbia.IntegrationTest;
import it.mulders.columbia.shared.TechnicalException;
import it.mulders.columbia.vaults.Vault;
import it.mulders.columbia.vaults.VaultService;
import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.mockito.Mockito.when;

@IntegrationTest
public class ListVaultsIT extends AbstractKaribuTest implements WithAssertions {
    @MockBean
    private VaultService vaultService;

    @BeforeEach
    @SneakyThrows( TechnicalException.class)
    void prepare() {
        when(vaultService.listVaults()).thenReturn( List.of(
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
        UI.getCurrent().navigate(ListVaultsView.class);
    }

    @Test
    void should_display_metrics() {
        var grid = (Grid<Vault>) _get(Grid.class);

        assertThat(grid.getListDataView().getItemCount()).isEqualTo(2);

        assertThat(grid.getListDataView().getItems()).anySatisfy(item -> {
            assertThat(item.getArn()).isEqualTo("arn:aws:glacier:eu-central-1:460046808775:vaults/Example_Vault_1");
            assertThat(item.getName()).isEqualTo("Example_Vault_1");
            assertThat(item.getSizeInBytes()).isEqualTo(1024);
        });
    }
}
