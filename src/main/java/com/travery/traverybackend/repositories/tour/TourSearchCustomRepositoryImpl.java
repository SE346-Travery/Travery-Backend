package com.travery.traverybackend.repositories.tour;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import java.util.Set;
import org.hibernate.search.engine.search.predicate.SearchPredicate;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.sort.SearchSort;
import org.hibernate.search.engine.search.sort.dsl.SearchSortFactory;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.travery.traverybackend.dtos.request.tour.TourSearchRequest;
import com.travery.traverybackend.entities.tour.Tour;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class TourSearchCustomRepositoryImpl implements TourSearchCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Tour> searchTours(TourSearchRequest request, Pageable pageable) {
        SearchSession searchSession = Search.session(entityManager);
        var scope = searchSession.scope(Tour.class);

        int offset = (int) pageable.getOffset();
        int size = pageable.getPageSize();
        var result = searchSession.search(scope)
                .where(buildPredicate(scope.predicate(), request))
                .sort(buildSort(scope.sort(), pageable.getSort()))
                .fetch(offset, size);
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    private SearchPredicate buildPredicate(SearchPredicateFactory f, TourSearchRequest request) {
        var bool = f.bool();
        bool.must(f.match().field("isCustom").matching(false));

        // 1. Tìm kiếm text (Full-text search) với Fuzzy Search và Boosting
        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            bool.must(f.match()
                    .field("name").boost(2.0f) // Ưu tiên kết quả trùng tên tour hơn
                    .field("description")
                    .field("destination.name")
                    .matching(request.getKeyword())
                    .fuzzy(1)); // Cho phép sai lệch 1 ký tự (typo)
        }
        // 2. Lọc khoảng giá
        if (request.getMinPrice() != null && request.getMaxPrice() != null) {
            bool.must(f.range().field("pricePerAdult").between(request.getMinPrice(), request.getMaxPrice()));
        } else if (request.getMinPrice() != null) {
            bool.must(f.range().field("pricePerAdult").atLeast(request.getMinPrice()));
        } else if (request.getMaxPrice() != null) {
            bool.must(f.range().field("pricePerAdult").atMost(request.getMaxPrice()));
        }
        // 3. Lọc theo destination (Nếu ID là chuỗi String thì pass thẳng, UUID thì
        // parse ra)
        if (request.getDestinationId() != null) {
            bool.must(f.match().field("destination.id").matching(request.getDestinationId()));
        }
        // 4. Lọc theo minRating (sau khi bạn thêm field averageRating)
        if (request.getMinRating() != null) {
            bool.must(f.range().field("averageRating").atLeast(request.getMinRating().doubleValue()));
        }
        // 5. Nested: Tìm ngày khả dụng
        if (request.getStartDate() != null) {
            bool.must(f.nested("tourInstances")
                    .add(f.match().field("tourInstances.startDate").matching(request.getStartDate()))
                    .add(f.match().field("tourInstances.status").matching(TourInstanceStatus.OPEN)));
        }
        return bool.toPredicate();
    }

    // Hàm tạo tiêu chí Sort từ Pageable
    private SearchSort buildSort(SearchSortFactory f, Sort sort) {
        if (sort.isUnsorted()) {
            // Mặc định: Sort theo điểm số phù hợp của từ khóa (Relevance Score),
            // nếu không có từ khóa thì sắp xếp theo ID giảm dần.
            return f.composite()
                    .add(f.score())
                    .add(f.field("id").desc())
                    .toSort();
        }

        var sortBuilder = f.composite();
        Set<String> allowedSortFields = java.util.Set.of("id", "pricePerAdult", "averageRating", "name");

        for (Sort.Order order : sort) {
            String property = order.getProperty();
            if (!allowedSortFields.contains(property)) {
                throw new BaseAppException(
                        WebErrorCode.BAD_REQUEST,
                        "Invalid sort field: " + property);
            }
            var fieldSort = f.field(property);
            if (order.isAscending()) {
                sortBuilder.add(fieldSort.asc());
            } else {
                sortBuilder.add(fieldSort.desc());
            }
        }
        return sortBuilder.toSort();
    }
}
