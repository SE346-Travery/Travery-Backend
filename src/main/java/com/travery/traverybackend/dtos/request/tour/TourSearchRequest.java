package com.travery.traverybackend.dtos.request.tour;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourSearchRequest {
    private String keyword;

    @PositiveOrZero(message = "Min price must be positive or zero")
    private BigDecimal minPrice;

    @PositiveOrZero(message = "Max price must be positive or zero")
    private BigDecimal maxPrice;

    private LocalDate startDate;

    private UUID destinationId;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer minRating;
}
