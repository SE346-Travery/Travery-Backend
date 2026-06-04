package com.travery.traverybackend.services.coach.impl;

import com.travery.traverybackend.dtos.response.profile.GuideProfileResponse;
import com.travery.traverybackend.enums.user.UserStatus;
import com.travery.traverybackend.mappers.UserMapper;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.coach.CoordinatorLookupService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoordinatorLookupServiceImpl implements CoordinatorLookupService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional(readOnly = true)
  public List<GuideProfileResponse> getGuides() {
    return userMapper.toGuideResponseList(userRepository.findAllGuidesByStatus(UserStatus.ACTIVE));
  }
}
