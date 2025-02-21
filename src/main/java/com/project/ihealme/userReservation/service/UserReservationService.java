package com.project.ihealme.userReservation.service;

import com.project.ihealme.userReservation.domain.UserReservation;
import com.project.ihealme.userReservation.dto.UserResPageRequestDTO;
import com.project.ihealme.userReservation.dto.UserResPageResponseDTO;
import com.project.ihealme.userReservation.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

//@Transactional
@Transactional
@Service
public class UserReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public List<UserReservation> getUserReservationList() {
        return reservationRepository.findAll(Sort.by(Sort.Direction.DESC, "resNo"));
    }

//    public Long updateStatus(UserReservation userReservation) {
//
//        userReservation.setEmail("longlee@daum.net");
//        userReservation.setCurrentStatus("진료 전");
//        // test를 위해 값을 임의로 넣음. 원래대로라면 이메일은 로그인 정보를 가져옴
//
//        UserReservation userRes = reservationRepository.findByEmailAndCurrentStatus(
//                userReservation.getEmail(), userReservation.getCurrentStatus());
//
//        reservationRepository.save(userRes.toEntity(userRes));
//
//        return userRes.getResNo();
//    }

    public void updateCurrentStatus(int resNo, String newStatus) {
        UserReservation userReservation = reservationRepository.findByResNo((long) resNo);

        userReservation.setCurrentStatus(newStatus);

        reservationRepository.save(userReservation);
    }

    public UserResPageResponseDTO getUserResList(UserResPageRequestDTO userResPageRequestDTO) {
        Page<UserReservation> result = null;

        String type = userResPageRequestDTO.getType();
        Pageable pageable = userResPageRequestDTO.getPageable(Sort.by("resNo").descending());

        if (type == null || type.equals("")) {
            result = reservationRepository.findAllByPage(pageable);
        } else {
            String keyword = userResPageRequestDTO.getKeyword();

            switch (type) {
                case "hpt":
                    result = reservationRepository.findByNameContaining(keyword, pageable);
                    break;
                case "tx":
                    result = reservationRepository.findByListContaining(keyword, pageable);
                    break;
                case "st":
                    result = reservationRepository.findByCurrentStatusContaining(keyword, pageable);
                    break;
            }
        }

        return new UserResPageResponseDTO(result);
    }
}
