package io.nicheblog.dreamdiary.domain.user.reqst.service;

import io.nicheblog.dreamdiary.domain.user.info.entity.UserAuthRoleEntity;
import io.nicheblog.dreamdiary.domain.user.info.entity.UserEntity;
import io.nicheblog.dreamdiary.domain.user.info.entity.UserStusEmbed;
import io.nicheblog.dreamdiary.domain.user.info.repository.jpa.UserRepository;
import io.nicheblog.dreamdiary.domain.user.info.service.UserService;
import io.nicheblog.dreamdiary.domain.user.reqst.mapstruct.UserReqstMapstruct;
import io.nicheblog.dreamdiary.domain.user.reqst.model.UserReqstDto;
import io.nicheblog.dreamdiary.global.Constant;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UserReqstService
 * <pre>
 *  사용자 계정 신청 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service("userReqstService")
@RequiredArgsConstructor
public class UserReqstService {

    private final UserService userService;
    private final UserReqstMapstruct userReqstMapstruct = UserReqstMapstruct.INSTANCE;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 신청 전처리.
     * 
     * @param userReqstDto 등록할 객체
     */
    public void preRegist(final UserReqstDto userReqstDto) throws Exception {
        // 접속 IP 정보 없을시 사용으로 찍었더라도 미사용으로 변경
        if (StringUtils.isEmpty(userReqstDto.getAcsIpListStr())) {
            userReqstDto.setUseAcsIpYn("N");
            userReqstDto.setAcsIpListStr(null);
        }
    }
    
    /**
     * 사용자 관리 > 사용자 신규계정 신청
     * 계정 기본정보만 입력, 세부정보는 가입 승인 후 수정
     * (등록 과정과 거의 동일하지만 일단 프로세스 분리)
     *
     * @param userReqstDto 등록할 객체
     * @return {@link UserReqstDto} -- 성공 결과 객체
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Transactional
    public UserReqstDto regist(final UserReqstDto userReqstDto) throws Exception {
        // 전처리 (메소드 분리)
        this.preRegist(userReqstDto);

        // Dto -> Entity 변환
        UserEntity userReqstEntity = userReqstMapstruct.toEntity(userReqstDto);
        // userReqstEntity.setUserProflNo(this.userInfoReg(userReqstEntity, userReqst));
        userReqstEntity.setAuthList(List.of(new UserAuthRoleEntity(Constant.AUTH_USER)));
        userReqstEntity.setPassword(passwordEncoder.encode(userReqstDto.getPassword()));
        userReqstEntity.setAcntStus(UserStusEmbed.getReqstStus());
        userReqstEntity.cascade();
        // insert
        UserEntity rsltEntity = userRepository.save(userReqstEntity);
        return userReqstMapstruct.toDto(rsltEntity);
    }

    /**
     * 사용자 정보 승인 처리.
     *
     * @param userNo 사용자 번호
     * @return {@link Boolean} 처리 성공 여부
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Transactional
    public Boolean cf(final Integer userNo) throws Exception {
        // Entity 레벨 조회
        UserEntity rsEntity = userService.getDtlEntity(userNo);
        if (rsEntity == null) return false;

        // lockedYn 플래그 업데이트
        rsEntity.acntStus.setCfYn("Y");
        rsEntity.acntStus.setLockedYn("N");
        Integer rsId = userRepository.saveAndFlush(rsEntity).getUserNo();
        return (rsId != null);
    }

    /**
     * 사용자 정보 승인 취소 처리.
     *
     * @param userNo 사용자 번호
     * @return {@link Boolean} 처리 성공 여부
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Transactional
    public Boolean uncf(final Integer userNo) throws Exception {
        // Entity 레벨 조회
        UserEntity rsEntity = userService.getDtlEntity(userNo);
        if (rsEntity == null) return false;

        // lockedYn 플래그 업데이트
        rsEntity.acntStus.setCfYn("N");
        rsEntity.acntStus.setLockedYn("Y");
        Integer rsId = userRepository.saveAndFlush(rsEntity).getUserNo();
        return (rsId != null);
    }
}
