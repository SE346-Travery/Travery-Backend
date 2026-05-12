package com.travery.traverybackend.dtos.response.tour;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourItineraryResponse {
    private Integer dayNumber;
    private String title;
    private String description;
    private List<ImageResponse> images;
}
