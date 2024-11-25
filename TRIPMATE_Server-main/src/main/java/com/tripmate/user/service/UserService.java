package com.tripmate.user.service;

import com.tripmate.user.domain.User;
import com.tripmate.user.dto.UserDTO;
import com.tripmate.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 사용자 프로필 조회
    public UserDTO getUserProfile(String userId) {
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return convertToDTO(user); // 사용자 정보를 DTO로 변환
        }
        return null;  // 실제 구현에서는 예외 처리가 필요
    }

    // 프로필 등록 로직 수정: 이미 저장된 userId에 나머지 정보만 업데이트
    @Transactional
    public void registerUserProfile(UserDTO userDTO, String userId) {
        // userId로 기존 사용자 정보 조회
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 기존 userId와 nickname은 변경하지 않고 나머지 프로필 정보만 업데이트
            user.setAge(userDTO.getAge());
            user.setGender(userDTO.getGender());
            user.setProfileImage(userDTO.getProfileImage());
            user.setMbti(userDTO.getMbti());
            user.setIntroduction(userDTO.getIntroduction());

            // Location 정보 설정
            User.Location location = new User.Location();
            location.setCountry(userDTO.getLocationCountry());
            location.setRegion(userDTO.getLocationRegion());
            user.setLocation(location);

            userRepository.save(user); // 업데이트된 사용자 정보 저장
        } else {
            throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        }
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        // nickname과 userId는 이미 저장된 사용자이므로 반환하지 않음
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());
        dto.setProfileImage(user.getProfileImage());
        dto.setMbti(user.getMbti());
        dto.setIntroduction(user.getIntroduction());

        // Location 정보가 null인지 확인
        if (user.getLocation() != null) {
            dto.setLocationCountry(user.getLocation().getCountry());
            dto.setLocationRegion(user.getLocation().getRegion());
        }

        return dto;
    }
}
