package com.travery.traverybackend.services.coach;

import com.travery.traverybackend.dtos.response.profile.GuideProfileResponse;
import java.util.List;

public interface CoordinatorLookupService {
  List<GuideProfileResponse> getGuides();
}
