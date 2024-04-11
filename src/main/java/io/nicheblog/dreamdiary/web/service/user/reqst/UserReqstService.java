package io.nicheblog.dreamdiary.web.service.user.reqst;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.web.entity.user.UserAuthRoleEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserStusEmbed;
import io.nicheblog.dreamdiary.web.mapstruct.user.reqst.UserReqstMapstruct;
import io.nicheblog.dreamdiary.web.model.user.reqst.UserReqstDto;
import io.nicheblog.dreamdiary.web.repository.user.UserRepository;
import io.nicheblog.dreamdiary.web.repository.user.reqst.UserReqstRepository;
import io.nicheblog.dreamdiary.web.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * UserReqstService
 * <pre>
 *  사용자 계정 신청 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service("userReqstService")
public class UserReqstService {

    @Resource(name = "userService")
    private UserService userService;

    private final UserReqstMapstruct userReqstMapstruct = UserReqstMapstruct.INSTANCE;

    @Resource(name = "userRepository")
    private UserRepository userRepository;
    @Resource(name = "userReqstRepository")
    private UserReqstRepository userReqstRepository;

    @Resource(name = "passwordEncoder")
    private PasswordEncoder passwordEncoder;

    /**
     * 사용자 관리 > 사용자 신규계정 신청
     * 계정 기본정보만 입력, 세부정보는 가입 승인 후 수정
     * (등록 과정과 거의 동일하지만 일단 프로세스 분리)
     */
    public UserReqstDto regist(final UserReqstDto userReqst) throws Exception {
        // 접속 IP 정보 없을시 사용으로 찍었더라도 미사용으로 변경
        if (StringUtils.isEmpty(userReqst.getAcsIpListStr())) {
            userReqst.setUseAcsIpYn("N");
            userReqst.setAcsIpListStr(null);
        }

        // Dto -> Entity
        UserEntity userReqstEntity = userReqstMapstruct.toEntity(userReqst);
        // userReqstEntity.setUserProflNo(this.userInfoReg(userReqstEntity, userReqst));
        userReqstEntity.setAuthList(List.of(new UserAuthRoleEntity(Constant.AUTH_USER)));
        userReqstEntity.setPassword(passwordEncoder.encode(userReqst.getPassword()));
        userReqstEntity.setAcntStus(UserStusEmbed.getReqstStus());
        userReqstEntity.cascade();
        // insert
        UserEntity rsltEntity = userReqstRepository.save(userReqstEntity);
        return userReqstMapstruct.toDto(rsltEntity);
    }

    /**
     * 사용자 정보 승인
     */
    public Boolean cf(final Integer userProflNo) throws Exception {
        // Entity 레벨 조회
        UserEntity rsEntity = userService.getDtlEntity(userProflNo);
        if (rsEntity == null) return false;
        // lockedYn 플래그 업데이트
        rsEntity.acntStus.setCfYn("Y");
        rsEntity.acntStus.setLockedYn("N");
        Integer rsId = userRepository.saveAndFlush(rsEntity)
                                     .getUserNo();
        return (rsId != null);
    }

    /**
     * 사용자 정보 승인취소
     */
    public Boolean uncf(final Integer userNo) throws Exception {
        // Entity 레벨 조회
        UserEntity rsEntity = userService.getDtlEntity(userNo);
        if (rsEntity == null) return false;
        // lockedYn 플래그 업데이트
        rsEntity.acntStus.setCfYn("N");
        rsEntity.acntStus.setLockedYn("Y");
        Integer rsId = userRepository.saveAndFlush(rsEntity)
                                     .getUserNo();
        return (rsId != null);
    }
}
