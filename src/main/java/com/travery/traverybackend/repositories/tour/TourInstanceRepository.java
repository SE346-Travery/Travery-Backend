package com.travery.traverybackend.repositories.tour;

import com.travery.traverybackend.entities.tour.TourInstance;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TourInstanceRepository extends JpaRepository<TourInstance, UUID> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT ti FROM TourInstance ti JOIN FETCH ti.tour WHERE ti.id = :id")
  Optional<TourInstance> findByIdWithLock(@Param("id") UUID id);

  @Query(
      "SELECT ti FROM TourInstance ti JOIN FETCH ti.tour JOIN FETCH ti.coach JOIN FETCH ti.driver WHERE ti.id = :id")
  Optional<TourInstance> findByIdWithDetails(@Param("id") UUID id);

  List<TourInstance> findByStatus(TourInstanceStatus status);

  @Query(
      "SELECT ti FROM TourInstance ti WHERE ti.currentParticipants >= 10 AND ti.currentParticipants <= 30 AND ti.status NOT IN :excludedStatuses")
  List<TourInstance> findWaitingConfirmation(
      @Param("excludedStatuses") List<TourInstanceStatus> excludedStatuses);

  @Query(
      "SELECT ti FROM TourInstance ti WHERE ti.currentParticipants < ti.tour.minParticipants AND ti.status NOT IN :excludedStatuses")
  List<TourInstance> findLowOccupancy(
      @Param("excludedStatuses") List<TourInstanceStatus> excludedStatuses);

  List<TourInstance> findByTourIdAndStatusInAndStartDateGreaterThanEqualOrderByStartDateAsc(
      UUID tourId, List<TourInstanceStatus> statuses, LocalDate currentDate);

  List<TourInstance> findByGuideId(UUID guideId);

  List<TourInstance> findByGuideIdAndStatus(UUID guideId, TourInstanceStatus status);

  List<TourInstance> findByStartDateAndStatusIn(
      LocalDate startDate, List<TourInstanceStatus> statuses);
}
