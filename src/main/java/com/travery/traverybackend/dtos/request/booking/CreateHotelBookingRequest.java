package com.travery.traverybackend.dtos.request.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.travery.traverybackend.validation.ValidDateRange;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidDateRange
public class CreateHotelBookingRequest {
    private UUID tourInstanceId; // Optional, if booked as part of a tour

    @NotEmpty
    @Valid
    private List<HotelBookingRequestDetail> rooms;

    @NotNull
    private java.time.LocalDate startDate;

    @NotNull
    private java.time.LocalDate endDate;

    @NotEmpty
    @Valid
    private List<BookingMemberRequest> members;

    @NotBlank(message = "Contact name is required")
    private String contactName;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    private String specialRequests;

    private String ipAddress;
}
