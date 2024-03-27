/*
package io.nicheblog.dreamdiary.web.service.user;

import dreamdiary.nicheblog.io.web.entity.user.UserEntity;
import dreamdiary.nicheblog.io.web.entity.user.UserInfoEntity;
import dreamdiary.nicheblog.io.web.entity.user.UserInfoItemEntity;
import dreamdiary.nicheblog.io.web.entity.user.UserReqstEntity;
import dreamdiary.nicheblog.io.web.mapstruct.user.UserInfoMapstruct;
import dreamdiary.nicheblog.io.web.mapstruct.user.UserMapstruct;
import dreamdiary.nicheblog.io.web.model.user.UserAcsIpDto;
import dreamdiary.nicheblog.io.web.model.user.UserDto;
import dreamdiary.nicheblog.io.web.repository.user.UserInfoRepository;
import dreamdiary.nicheblog.io.web.repository.user.UserRepository;
import dreamdiary.nicheblog.io.web.repository.user.UserReqstRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

*/
/**
 * UserReqstService
 * <pre>
 *  사용자 계정 신청 서비스 모듈
 * </pre>
 *
 * @author nichefish
 *//*

@Service("userReqstService")
public class UserReqstService {

    @Resource(name = "userService")
    private UserService userService;

    private final UserMapstruct userMapstruct = UserMapstruct.INSTANCE;
    private final UserInfoMapstruct userInfoMapstruct = UserInfoMapstruct.INSTANCE;

    @Resource(name = "userRepository")
    private UserRepository userRepository;
    @Resource(name = "userProflRepository")
    private UserInfoRepository userProflRepository;
    @Resource(name = "userReqstRepository")
    private UserReqstRepository userReqstRepository;

    @Resource(name = "passwordEncoder")
    private PasswordEncoder passwordEncoder;

    */
/**
     * 사용자 관리 > 사용자 신규계정 신청
     *//*

    public UserDto userReqstReg(
            final UserDto userDto,
            final MultipartHttpServletRequest request
    ) throws Exception {
        // 파일 영역 처리
        // 계정 잠금여부 체크박스 값 세팅
        if (!"Y".equals(userDto.getLockedYn())) userDto.setLockedYn("N");

        // 접속 IP 사용 여부 체크박스 값 세팅
        if (!"Y".equals(userDto.getAcsIpYn())) {
            userDto.setAcsIpYn("N");
        } else {
            // 접속 IP 사용"Y"시 접속 IP 세팅
            String acsIpListStr = userDto.getAcsIpListStr();
            List<UserAcsIpDto> acsIpList = userService.parseAcsIpListInfo(acsIpListStr);
            userDto.setAcsIpList(acsIpList);
        }
        // Dto -> Entity
        // 사용자 정보userInfo 먼저 처리 후 user에 키값 세팅 (필드 위임)
        UserReqstEntity userReqstEntity = userMapstruct.toReqstEntity(userDto);
        userReqstEntity.setUserProflNo(this.userInfoReg(userReqstEntity, userDto));
        userReqstEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userReqstEntity.setReqstYn("Y");
        userReqstEntity.setCfYn("N");
        userReqstEntity.setLockedYn("Y");
        // insert
        UserReqstEntity rsltEntity = userReqstRepository.save(userReqstEntity);
        return userMapstruct.toDto(rsltEntity);
    }

    */
/**
     * 사용자 관리 > 사용자 정보 등록 (메소드 분리)
     *//*

    public Integer userInfoReg(
            final UserReqstEntity userEntity,
            final UserDto userDto
    ) throws Exception {
        String userProflYn = userDto.getUserProflYn();
        Integer userProflNo = userEntity.getUserProflNo();
        if ("N".equals(userProflYn) && userProflNo == null) {
            userEntity.setUserProfl(null);
            return null;
        }
        UserInfoEntity rsUserInfoEntity = userEntity.getUserProfl();
        if (rsUserInfoEntity == null) rsUserInfoEntity = userProflRepository.findById(userProflNo)
                                                                           .orElse(new UserInfoEntity());
        if ("Y".equals(userProflYn)) {
            userInfoMapstruct.updateFromDto(userDto.getUserProfl(), rsUserInfoEntity);
            this.sortUserInfoItemList(rsUserInfoEntity);      // 빈값 걸러내기+순번매기기
        } else {
            rsUserInfoEntity.setDelYn("Y");
        }
        return userProflRepository.save(rsUserInfoEntity)
                                 .getUserProflNo();
    }

    */
/**
     * 사용자 관리 > 사용자 정보 추가정보 정리 (null값 걸러내기 + sorting)
     *//*

    public void sortUserInfoItemList(final UserInfoEntity userInfoEntity) {
        List<UserInfoItemEntity> itemList = userInfoEntity.getItemList();
        if (CollectionUtils.isEmpty(itemList)) return;
        List<UserInfoItemEntity> sortedItemList = new ArrayList<>();
        int sortOrdr = 1;
        for (UserInfoItemEntity item : itemList) {
            if (StringUtils.isEmpty(item.getItemNm())) continue;
            item.setSortOrdr(sortOrdr++);
            sortedItemList.add(item);
        }
        userInfoEntity.setItemList(sortedItemList);
    }

    */
/**
     * 사용자정보 승인
     *//*

    public Boolean cfReqst(final Integer userProflNo) throws Exception {
        // Entity 레벨 조회
        UserEntity rsEntity = userService.getDtlEntity(userProflNo);
        if (rsEntity == null) return false;
        // lockedYn 플래그 업데이트
        rsEntity.setCfYn("Y");
        rsEntity.setLockedYn("N");
        Integer rsId = userRepository.saveAndFlush(rsEntity)
                                     .getUserNo();
        return (rsId != null);
    }

    */
/**
     * 사용자정보 승인취소
     *//*

    public Boolean uncfReqst(final Integer userProflNo) throws Exception {
        // Entity 레벨 조회
        UserEntity rsEntity = userService.getDtlEntity(userProflNo);
        if (rsEntity == null) return false;
        // lockedYn 플래그 업데이트
        rsEntity.setCfYn("N");
        rsEntity.setLockedYn("Y");
        Integer rsId = userRepository.saveAndFlush(rsEntity)
                                     .getUserNo();
        return (rsId != null);
    }
}*/
