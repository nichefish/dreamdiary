package io.nicheblog.dreamdiary.web.service.user;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.admin.LgnPolicyEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserStusEmbed;
import io.nicheblog.dreamdiary.web.mapstruct.user.UserMapstruct;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import io.nicheblog.dreamdiary.web.repository.user.UserRepository;
import io.nicheblog.dreamdiary.web.service.admin.LgnPolicyService;
import io.nicheblog.dreamdiary.web.spec.user.UserSpec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * UserService
 * <pre>
 *  사용자 관리 > 계정 및 권한 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseMultiCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("userService")
public class UserService
        implements BaseMultiCrudService<UserDto.DTL, UserDto.LIST, Integer, UserEntity, UserRepository, UserSpec, UserMapstruct> {

    @Resource(name = "userRepository")
    private UserRepository userRepository;
    @Resource(name = "userSpec")
    private UserSpec userSpec;

    @Resource(name = "lgnPolicyService")
    private LgnPolicyService lgnPolicyService;
    @Resource(name = "passwordEncoder")
    private PasswordEncoder passwordEncoder;

    private final UserMapstruct userMapstruct = UserMapstruct.INSTANCE;

    @Override
    public UserRepository getRepository() {
        return this.userRepository;
    }
    @Override
    public UserSpec getSpec() {
        return this.userSpec;
    }
    @Override
    public UserMapstruct getMapstruct() {
        return this.userMapstruct;
    }

    /**
     * 사용자 관리 > 사용자 단일 조회 (Dto Level) (Long userId / String userId)
     */
    public UserDto.DTL getDtlDto(final String userId) throws Exception {
        // Entity 레벨 조회
        UserEntity rsUserEntity = this.getDtlEntity(userId);
        return userMapstruct.toDto(rsUserEntity);
    }

    /**
     * 사용자 관리 > 사용자 단일 조회 (Entity Level) (Long userNo / String userId)
     */
    public UserEntity getDtlEntity(final String userId) throws Exception {
        Optional<UserEntity> rsUserEntityWrapper = userRepository.findByUserId(userId);
        return Objects.requireNonNull(rsUserEntityWrapper.orElseThrow(() -> new NullPointerException("사용자 정보가 존재하지 않습니다.")));
    }

    /* ----- */

    /**
     * 사용자 관리 > 사용자 ID 중복 체크
     */
    public Boolean userIdDupChck(final String userId) {
        return userRepository.findByUserId(userId).isPresent();
    }

    @Override
    public void preRegist(final UserDto.DTL userDto) throws Exception {
        // 접속 IP 정보 없을시 사용으로 찍었더라도 미사용으로 변경
        if (StringUtils.isEmpty(userDto.getAcsIpListStr())) {
            userDto.setUseAcsIpYn("N");
            userDto.setAcsIpListStr(null);
        }
    }

    /**
     * 수정 후처리 :: override
     */
    @Override
    public void postModify(final UserEntity rslt) throws Exception {
        // 관련 캐시 처리
        EhCacheUtils.clearL2Cache(AuditorInfo.class);
    }

    /**
     * 삭제 후처리 :: override
     */
    @Override
    public void postDelete(final UserEntity rslt) throws Exception {
        // 관련 캐시 처리
        EhCacheUtils.clearL2Cache(AuditorInfo.class);
    }

    /**
     * 사용자 관리 > 사용자 등록
     */
    @Override
    public UserDto.DTL regist(final UserDto.DTL userDto) throws Exception {
        // Dto -> Entity
        // 사용자 정보userInfo 먼저 처리 후 user에 키값 세팅 (필드 위임)
        UserEntity userEntity = userMapstruct.toEntity(userDto);
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userEntity.setAcntStus(UserStusEmbed.getRegistStus());
        userEntity.cascade();
        // insert
        UserEntity rsltEntity = userRepository.save(userEntity);
        UserDto.DTL rsltDto = userMapstruct.toDto(rsltEntity);
        rsltDto.setIsSuccess((rsltEntity.getUserNo() != null));
        return rsltDto;
    }

    /**
     * 사용자 관리 > 사용자 비밀번호 초기화
     */
    public Boolean passwordReset(final Integer userNo) throws Exception {
        // Entity 레벨 조회
        UserEntity rsUserEntity = this.getDtlEntity(userNo);
        if (rsUserEntity == null) return false;
        // 로그인 설정 조회 (cachable)
        LgnPolicyEntity rsLgnPolicyEntity = lgnPolicyService.getDtlEntity();
        String pwForReset = rsLgnPolicyEntity.getPwForReset();
        // update
        rsUserEntity.setPassword(pwForReset);
        rsUserEntity.acntStus.setNeedsPwReset("Y");
        rsUserEntity.acntStus.setPwChgDt(DateUtils.getCurrDate());
        Integer rsId = userRepository.saveAndFlush(rsUserEntity)
                                     .getUserNo();
        return (rsId != null);
    }

    @Override
    public void preModify(final UserDto.DTL userDto) throws Exception {
        // 접속 IP 정보 없을시 사용으로 찍었더라도 미사용으로 변경
        if (StringUtils.isEmpty(userDto.getAcsIpListStr())) {
            userDto.setUseAcsIpYn("N");
            userDto.setAcsIpListStr(null);
        }
    }

    /**
     * 사용자 관리 > 사용자 수정
     */
    @Override
    public UserDto.DTL modify(final UserDto.DTL userDto) throws Exception {
        UserEntity userEntity = this.getDtlEntity(userDto);
        userMapstruct.updateFromDto(userDto, userEntity);

        // update
        UserEntity rsltEntity = this.updt(userEntity);
        UserDto.DTL rsltDto = userMapstruct.toDto(rsltEntity);
        rsltDto.setIsSuccess((rsltEntity.getUserNo() != null));
        return rsltDto;
    }

    /**
     * 사용자 관리 > 사용자 목록 엑셀 다운로드 목록 조회
     */
    // public List<Object> userListXlsx(final Map<String, Object> searchParamMap) throws Exception {
    //     // 목록 검색
    //     List<UserEntity> userEntityList = this.getListEntity(searchParamMap);
//
    //     // List<Entity> -> List<ListXlsxDto>
    //     List<Object> rsUserListXlxsDtoList = new ArrayList<>();
    //     for (UserEntity entity : userEntityList) {
    //         UserListXlsxDto dto = userMapstruct.toListXlsxDto(entity);
    //         dto.setAcsIpListStr(entity.getAcsIpListStr());
    //         rsUserListXlxsDtoList.add(dto);
    //     }
    //     return rsUserListXlxsDtoList;
    // }

    /**
     * 장기간 미접속여부 조회
     */
    public Boolean isDormant(final String userId) throws Exception {
        if (StringUtils.isEmpty(userId)) return false;
        if (Constant.SYSTEM_ACNT.equals(userId) || Constant.DEV_ACNT.equals(userId)) return false;

        LgnPolicyEntity rsEntity = lgnPolicyService.getDtlEntity();
        Integer lgnLockDy = rsEntity.getLgnLockDy();

        UserEntity user = this.getDtlEntity(userId);
        Date lastLgnDt = user.acntStus.getLstLgnDt();
        if (lastLgnDt == null) lastLgnDt = user.getRegDt();
        Date dormantDt = DateUtils.getDateAddDay(lastLgnDt, lgnLockDy);
        return (dormantDt == null || dormantDt.compareTo(DateUtils.getCurrDate()) < 0);
    }

    /**
     * 사용자 정보 잠금
     */
    public Boolean userLock(final Integer userProflNo) throws Exception {
        // Entity 레벨 조회
        UserEntity rsEntity = this.getDtlEntity(userProflNo);
        if (rsEntity == null) return false;
        // lockedYn 플래그 업데이트
        rsEntity.acntStus.setLockedYn("Y");
        Integer rsId = userRepository.saveAndFlush(rsEntity)
                                     .getUserNo();
        return (rsId != null);
    }

    /**
     * 사용자 정보 잠금 해제
     */
    public Boolean userUnlock(final Integer userProflNo) throws Exception {
        // Entity 레벨 조회
        UserEntity rsEntity = this.getDtlEntity(userProflNo);
        if (rsEntity == null) return false;
        // lockedYn 플래그 + 최종접속일 업데이트
        // TODO: 최종접속일 이거 어찌할꼬...
        rsEntity.acntStus.setLockedYn("N");
        // rsEntity.setDormantYn("N");
        rsEntity.acntStus.setLstLgnDt(DateUtils.getCurrDate());
        Integer rsId = userRepository.saveAndFlush(rsEntity)
                                     .getUserNo();
        return (rsId != null);
    }

    /**
     * 내부직원 목록 조회 (결재자 정보에 쓰임) (계정정보로 조회)
     *
     * @param yyStr (년도)
     */
    public List<UserDto.LIST> getCrdtUserList(final String yyStr) throws Exception {
        if (StringUtils.isEmpty(yyStr)) return null;
        // 목록 검색
        String startDtStr = yyStr + "-01-01";
        String endDtStr = yyStr + "-12-31";
        return this.getCrdtUserList(startDtStr, endDtStr);
    }

    /**
     * 내부직원 목록 조회 (결재자 정보에 쓰임) (계정정보로 조회)
     *
     * @param startDtStr : 시작일자yyyy-MM-dd
     * @param endDtStr : 종료일자yyyy-MM-dd
     */
    public List<UserDto.LIST> getCrdtUserList(
            final String startDtStr,
            final String endDtStr
    ) throws Exception {
        // 목록 검색
        List<UserEntity> userEntityList = userRepository.findAll(userSpec.searchCrdtUser(startDtStr, endDtStr));

        // List<Entity> -> List<ListDto>
        return this.listEntityToDto(userEntityList);
    }

    /**
     * 내부직원 연락처 목록 조회
     */
    // public List<UserCttpcDto> getCrdtUserCttpcList(
    //         final String startDtStr,
    //         final String endDtStr
    // ) throws Exception {
    //     List<UserDto.LIST> crdtUserList = this.getCrdtUserList(startDtStr, endDtStr);
//
    //     return crdtUserList.stream()
    //             .map(listDto -> {
    //                 try {
    //                     return userMapstruct.toCttpcDto(listDto);
    //                 } catch (Exception e) {
    //                     throw new RuntimeException(e);
    //                 }
    //             })
    //             .collect(Collectors.toList());
    // }
//
    // /**
    //  * 내부직원 연락처 엑셀 다운로드 목록 조회
    //  */
    // public List<Object> getCrdtUserCttpcListXlsx(
    //         final String startDtStr,
    //         final String endDtStr
    // ) throws Exception {
    //     List<UserDto.LIST> crdtUserList = this.getCrdtUserList(startDtStr, endDtStr);
    //     List<Object> rsCrdtUserCttpcXlsxObjList = new ArrayList<>();
    //     Map<String, String> header = new LinkedHashMap<>() {{
    //         put("이름", "이름");
    //         put("직급", "직급");
    //         put("부서", "부서");
    //         put("연락처", "연락처");
    //         put("이메일", "이메일");
    //     }};
    //     rsCrdtUserCttpcXlsxObjList.add(header);
    //     for (UserDto.LIST listDto : crdtUserList) {
    //         rsCrdtUserCttpcXlsxObjList.add(userMapstruct.toCttpcListXlsxDto(listDto));
    //     }
    //     return rsCrdtUserCttpcXlsxObjList;
    // }

    /**
     * 생일인 내부직원 목록 조회
     * JPA로 해당조건 조회하기에 난항이 있어서 내부직원 조회 후 for문 처리
     */
    //public List<UserDto.LIST> getBrthdyCrdtUser() throws Exception {
    //    String todayStr = DateUtils.getCurrDateStr(DatePtn.DATE);
    //    String tomorrowStr = DateUtils.getNextDateStr(DatePtn.DATE);
    //    Page<UserDto.LIST> crdtUserPage = this.getCrdtUserList(todayStr, tomorrowStr);
    //    List<UserDto.LIST> crdtUserList = crdtUserPage.getContent();
    //    if (CollectionUtils.isEmpty(crdtUserList)) return null;
//
    //    List<UserDto.LIST> brthdyUserList = new ArrayList<>();
    //    for (UserDto.LIST user : crdtUserList) {
    //        String brthdy = user.getBrthdy();
    //        if (StringUtils.isEmpty(brthdy)) continue;
    //        // 음력 / 양력 구분해서 적용
    //        if ("Y".equals(user.getLunarYn())) {
    //            // 음력일 경우 = 1) 오늘 날짜를 음력 날짜로 변환 후 2) 아래 로직 그대로 적용.
    //            String todayLunarStr = DateUtils.ChineseCal.solToLunStr(todayStr, DatePtn.DATE);
    //            String todayLunarYear = todayLunarStr.substring(0, 4);
//
    //            String brthMnthDy = brthdy.substring(4);
    //            String thisLunarBrthdy = todayLunarYear + brthMnthDy;
//
    //            if (!todayLunarStr.equals(thisLunarBrthdy)) continue;
    //            brthdyUserList.add(user);
    //        } else {
    //            // 양력일 경우
    //            String brthMnthDy = brthdy.substring(4);
    //            String thisBrthdy = DateUtils.getCurrYyStr() + brthMnthDy;
    //            if (!todayStr.equals(thisBrthdy)) continue;
    //            brthdyUserList.add(user);
    //        }
    //    }
//
    //    return brthdyUserList;
    //}

}