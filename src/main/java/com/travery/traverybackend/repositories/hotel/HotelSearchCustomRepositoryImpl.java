package com.travery.traverybackend.repositories.hotel;

import com.travery.traverybackend.dtos.request.hotel.HotelSearchRequest;
import com.travery.traverybackend.entities.hotel.Hotel;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Set;
import org.hibernate.search.engine.search.predicate.SearchPredicate;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.sort.SearchSort;
import org.hibernate.search.engine.search.sort.dsl.SearchSortFactory;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class HotelSearchCustomRepositoryImpl implements HotelSearchCustomRepository {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public Page<Hotel> searchHotels(HotelSearchRequest request, Pageable pageable) {
    SearchSession searchSession = Search.session(entityManager);
    var scope = searchSession.scope(Hotel.class);

    int offset = (int) pageable.getOffset();
    int size = pageable.getPageSize();
    var result =
        searchSession
            .search(scope)
            .where(buildPredicate(scope.predicate(), request))
            .sort(buildSort(scope.sort(), pageable.getSort()))
            .fetch(offset, size);
    return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
  }

  private SearchPredicate buildPredicate(SearchPredicateFactory f, HotelSearchRequest request) {
    var bool = f.bool();

    if (!bool.hasClause()) {
      return f.matchAll().toPredicate();
    }

    // 1. Text Search (Keyword)
    if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
      bool.must(
          f.match()
              .field("name")
              .boost(2.0f)
              .field("description")
              .field("address")
              .matching(request.getKeyword())
              .fuzzy(1));
    }

    // 2. City/Province filter
    if (request.getCityProvince() != null && !request.getCityProvince().isBlank()) {
      bool.must(f.match().field("cityProvince").matching(request.getCityProvince()));
    }

    // 3. Average Rating filter
    if (request.getMinRating() != null) {
      bool.must(f.range().field("averageRating").atLeast(request.getMinRating()));
    }

    // 4. Capacity filter (Nested RoomTypes)
    if (request.getAdults() != null || request.getChildren() != null) {
      var roomBool = f.bool();
      if (request.getAdults() != null) {
        roomBool.must(f.range().field("roomTypes.capacityAdults").atLeast(request.getAdults()));
      }
      if (request.getChildren() != null) {
        roomBool.must(f.range().field("roomTypes.capacityChildren").atLeast(request.getChildren()));
      }
      bool.must(roomBool.toPredicate());
    }

    // 5. Price filter (RoomTypes basePrice)
    if (request.getMinPrice() != null && request.getMaxPrice() != null) {
      bool.must(
          f.range()
              .field("roomTypes.basePrice")
              .between(request.getMinPrice(), request.getMaxPrice()));
    } else if (request.getMinPrice() != null) {
      bool.must(f.range().field("roomTypes.basePrice").atLeast(request.getMinPrice()));
    } else if (request.getMaxPrice() != null) {
      bool.must(f.range().field("roomTypes.basePrice").atMost(request.getMaxPrice()));
    }

    return bool.toPredicate();
  }

  private SearchSort buildSort(SearchSortFactory f, Sort sort) {
    if (sort.isUnsorted()) {
      return f.composite().add(f.score()).add(f.field("id").desc()).toSort();
    }

    var sortBuilder = f.composite();
    Set<String> allowedSortFields = java.util.Set.of("id", "averageRating", "name");

    for (Sort.Order order : sort) {
      String property = order.getProperty();
      if (!allowedSortFields.contains(property)) {
        throw new BaseAppException(WebErrorCode.BAD_REQUEST, "Invalid sort field: " + property);
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
