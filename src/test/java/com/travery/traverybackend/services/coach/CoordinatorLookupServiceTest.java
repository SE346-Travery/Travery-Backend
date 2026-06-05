package com.travery.traverybackend.services.coach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travery.traverybackend.dtos.response.profile.GuideProfileResponse;
import com.travery.traverybackend.entities.user.Guide;
import com.travery.traverybackend.enums.user.UserStatus;
import com.travery.traverybackend.mappers.UserMapper;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.coach.impl.CoordinatorLookupServiceImpl;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CoordinatorLookupServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private UserMapper userMapper;

  @InjectMocks private CoordinatorLookupServiceImpl lookupService;

  private Guide guide;
  private GuideProfileResponse guideResponse;

  @BeforeEach
  void setUp() {
    UUID guideId = UUID.randomUUID();
    guide = Guide.builder().id(guideId).fullName("Guide Name").guideLicense("G-001").build();
    guideResponse =
        GuideProfileResponse.builder()
            .id(guideId)
            .fullName("Guide Name")
            .guideLicense("G-001")
            .build();
  }

  @Test
  void getGuides_returnsActiveGuides() {
    when(userRepository.findAllGuidesByStatus(UserStatus.ACTIVE)).thenReturn(List.of(guide));
    when(userMapper.toGuideResponseList(List.of(guide))).thenReturn(List.of(guideResponse));

    List<GuideProfileResponse> result = lookupService.getGuides();

    assertEquals(1, result.size());
    verify(userRepository).findAllGuidesByStatus(UserStatus.ACTIVE);
  }
}
