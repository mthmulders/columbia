package it.mulders.columbia.shared.ui;

import com.vaadin.flow.data.provider.Query;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SpringDataProviderTest implements WithAssertions {
    static class Example {
    }
    private final PagingAndSortingRepository<Example, Long> repository = mock(PagingAndSortingRepository.class);
    private final SpringDataProvider<Example> provider = new SpringDataProvider<>(repository);

    @BeforeEach
    void prepareRepository() {
        when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());
    }

    @Nested
    @DisplayName("fetchFromBackEnd")
    class FetchFromBackEnd {
        @Test
        void should_convert_pagination_parameters() {
            // Arrange
            var query = new Query<Example, Void>(3, 15, List.of(), null, null);

            // Act
            provider.fetchFromBackEnd(query);

            // Assert
            var captor = ArgumentCaptor.forClass(Pageable.class);
            verify(repository).findAll(captor.capture());
            var argument = captor.getValue();
            assertThat(argument.getOffset()).isEqualTo(query.getPage());
            assertThat(argument.getPageSize()).isEqualTo(query.getLimit());
        }
    }
}