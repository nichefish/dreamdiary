package io.nicheblog.dreamdiary.web.service.user;

import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.reqst.UserReqstEntity;
import io.nicheblog.dreamdiary.web.mapstruct.user.UserReqstMapstruct;
import io.nicheblog.dreamdiary.web.model.user.UserAcsIpDto;
import io.nicheblog.dreamdiary.web.model.user.UserReqstDto;
import io.nicheblog.dreamdiary.web.repository.user.UserRepository;
import io.nicheblog.dreamdiary.web.repository.user.UserReqstRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
    // @Resource(name = "userProflRepository")
    // private UserInfoRepository userProflRepository;
    @Resource(name = "userReqstRepository")
    private UserReqstRepository userReqstRepository;

    @Resource(name = "passwordEncoder")
    private PasswordEncoder passwordEncoder;

    /**
     * 사용자 관리 > 사용자 신규계정 신청
     * 계정 기본정보만 입력, 세부정보는 가입 승인 후 수정
     */
    public UserReqstDto regist(
            final UserReqstDto userReqst,
            final MultipartHttpServletRequest request
    ) throws Exception {
        // 파일 영역 처리
        // 접속 IP 사용 여부 체크박스 값 세팅
        if (StringUtils.isEmpty(userReqst.getAcsIpListStr())) {
            userReqst.setUseAcsIpYn("N");
        } else {
            // 접속 IP 사용"Y"시 접속 IP 세팅
            String acsIpListStr = userReqst.getAcsIpListStr();
            List<UserAcsIpDto> acsIpList = userService.parseAcsIpListInfo(acsIpListStr);
            userReqst.setAcsIpList(acsIpList);
        }
        // Dto -> Entity
        // 사용자 정보userInfo 먼저 처리 후 user에 키값 세팅 (필드 위임)
        UserReqstEntity userReqstEntity = userReqstMapstruct.toEntity(userReqst);
        // userReqstEntity.setUserProflNo(this.userInfoReg(userReqstEntity, userReqst));
        userReqstEntity.setPassword(passwordEncoder.encode(userReqst.getPassword()));
        userReqstEntity.acntStus.setReqstYn("Y");
        userReqstEntity.acntStus.setCfYn("N");
        // insert
        UserReqstEntity rsltEntity = userReqstRepository.save(userReqstEntity);
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
