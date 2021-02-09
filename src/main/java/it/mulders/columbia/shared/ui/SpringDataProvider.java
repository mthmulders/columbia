package it.mulders.columbia.shared.ui;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Slf4j
public class SpringDataProvider<T> extends AbstractBackEndDataProvider<T, Void> {
    private final PagingAndSortingRepository<T, ?> repository;

    private Sort.Order convert(final QuerySortOrder querySortOrder) {
        var direction = switch (querySortOrder.getDirection()) {
            case ASCENDING -> Sort.Direction.ASC;
            case DESCENDING -> Sort.Direction.DESC;
        };

        return new Sort.Order(direction, querySortOrder.getSorted());
    }

    @Override
    protected int sizeInBackEnd(final Query<T, Void> query) {
        var page = query.getPage();
        var limit = query.getLimit();

        var sorting = query.getSortOrders().stream()
                .map(this::convert)
                .collect(toList());

        var request = PageRequest.of(page, limit, Sort.by(sorting));
        return (int) repository.findAll(request).getTotalElements();
    }

    @Override
    protected Stream<T> fetchFromBackEnd(final Query<T, Void> query) {
        var page = query.getPage();
        var limit = query.getLimit();

        var sorting = query.getSortOrders().stream()
                .map(this::convert)
                .collect(toList());

        var request = PageRequest.of(page, limit, Sort.by(sorting));
        return repository.findAll(request).stream();
    }
}
