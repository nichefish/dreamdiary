package io.nicheblog.dreamdiary.web.service.user;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.admin.LgnPolicyEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.mapstruct.user.UserMapstruct;
import io.nicheblog.dreamdiary.web.mapstruct.user.UserProflMapstruct;
import io.nicheblog.dreamdiary.web.model.user.UserAcsIpDto;
import io.nicheblog.dreamdiary.web.model.user.UserCttpcDto;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import io.nicheblog.dreamdiary.web.repository.user.UserProflRepository;
import io.nicheblog.dreamdiary.web.repository.user.UserRepository;
import io.nicheblog.dreamdiary.web.service.admin.LgnPolicyService;
import io.nicheblog.dreamdiary.web.spec.user.UserSpec;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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

    private final UserMapstruct userMapstruct = UserMapstruct.INSTANCE;
    private final UserProflMapstruct userProflMapstruct = UserProflMapstruct.INSTANCE;

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

    @Resource(name = "userProflRepository")
    private UserProflRepository userProflRepository;

    @Resource(name = "lgnPolicyService")
    private LgnPolicyService lgnPolicyService;

    @Resource(name = "passwordEncoder")
    private PasswordEncoder passwordEncoder;

    /**
     * 사용자 관리 > 사용자 단일 조회 (Dto Level) (Long userId / String userId)
     */
    public UserDto.DTL getDtlDto(final String userNo) throws Exception {
        // Entity 레벨 조회
        UserEntity rsUserEntity = this.getDtlEntity(userNo);
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

    /**
     * 사용자 관리 > 사용자 등록
     */
    @Override
    public UserDto.DTL regist(final UserDto.DTL userDto) throws Exception {
        // 계정 잠금여부 체크박스 값 세팅
        if (!"Y".equals(userDto.getLockedYn())) userDto.setLockedYn("N");

        // 접속 IP 사용 여부 체크박스 값 세팅
        if (!"Y".equals(userDto.getUseAcsIpYn()) || StringUtils.isEmpty(userDto.getAcsIpListStr())) {
            userDto.setUseAcsIpYn("N");
        } else {
            // 접속 IP 사용"Y"시 접속 IP 세팅
            String acsIpListStr = userDto.getAcsIpListStr();
            List<UserAcsIpDto> acsIpList = this.parseAcsIpListInfo(acsIpListStr);
            userDto.setAcsIpList(acsIpList);
        }

        // Dto -> Entity
        // 사용자 정보userInfo 먼저 처리 후 user에 키값 세팅 (필드 위임)
        UserEntity userEntity = userMapstruct.toEntity(userDto);
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userEntity.acntStus.setCfYn("Y");
        // insert
        UserEntity rsltEntity = userRepository.save(userEntity);
        UserDto.DTL rsltDto = userMapstruct.toDto(rsltEntity);
        rsltDto.setIsSuccess((rsltEntity.getUserNo() != null));
        return rsltDto;
    }

    /**
     * 사용자 관리 > 사용자 정보 등록 (메소드 분리)
     */
    public Integer userInfoReg(
            final UserEntity userEntity,
            final UserDto userDto
    ) throws Exception {
        // String userProflYn = userDto.getUserProflYn();
        // Integer userProflNo = userEntity.getUserProflNo();
        // if ("N".equals(userProflYn) && userProflNo == null) {
        //     userEntity.setUserProfl(null);
        //     return null;
        // }
        // UserProflEntity rsUserProflEntity = userEntity.getUserProfl();
        // if (rsUserProflEntity == null) {
        //     rsUserProflEntity = userProflRepository.findById(userProflNo)
        //                                          .orElse(new UserProflEntity());
        // }
        // if ("Y".equals(userProflYn)) {
        //     userInfoMapstruct.updateFromDto(userDto.getUserProfl(), rsUserProflEntity);
        //     this.sortUserInfoItemList(rsUserProflEntity);      // 빈값 걸러내기+순번매기기
        // } else {
        //     rsUserProflEntity.setDelYn("Y");
        // }
        // return userProflRepository.save(rsUserProflEntity).getUserProflNo();
        return null;
    }

    /**
     * 사용자 관리 > 접속 IP 목록 파싱 (메소드 분리)
     * Tagify (ex.) = [{"value":"123.123.123.123"},{"value":"234.234.234.234"}] 문자열 형식으로 넘어온댜.
     * 문자열을 JSON Array로 변환하여 직접 DTO에 세팅한다.
     */
    public List<UserAcsIpDto> parseAcsIpListInfo(final String acsIpListStr) {
        JSONArray jArray = new JSONArray(acsIpListStr);
        List<UserAcsIpDto> acsIpList = new ArrayList<>();
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject json = jArray.getJSONObject(i);
            UserAcsIpDto acsIp = new UserAcsIpDto();
            acsIp.setAcsIp(json.getString("value"));
            acsIpList.add(acsIp);
        }
        return acsIpList;
    }

    /**
     * 사용자 관리 > 사용자 정보 추가정보 정리 (null값 걸러내기 + sorting)
     */
    // public void sortUserInfoItemList(final UserProflEntity userProflEntity) {
    //     List<UserInfoItemEntity> itemList = userProflEntity.getItemList();
    //     if (CollectionUtils.isEmpty(itemList)) return;
    //     List<UserInfoItemEntity> sortedItemList = new ArrayList<>();
    //     int sortOrdr = 1;
    //     for (UserInfoItemEntity item : itemList) {
    //         if (StringUtils.isEmpty(item.getItemNm())) continue;
    //         item.setSortOrdr(sortOrdr++);
    //         sortedItemList.add(item);
    //     }
    //     userProflEntity.setItemList(sortedItemList);
    // }

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

    /**
     * 사용자 관리 > 사용자 수정
     */
    @Override
    public UserDto.DTL modify(
            final UserDto.DTL userDto,
            final Integer key
    ) throws Exception {
        // 계정 잠금여부 체크박스 값 세팅
        if (!"Y".equals(userDto.getLockedYn())) userDto.setLockedYn("N");

        // 접속 IP 사용 여부 체크박스 값 세팅
        if (!"Y".equals(userDto.getUseAcsIpYn()) || StringUtils.isEmpty(userDto.getAcsIpListStr())) {
            userDto.setUseAcsIpYn("N");
        } else {
            // 접속 IP 사용"Y"시 접속 IP 세팅
            String acsIpListStr = userDto.getAcsIpListStr();
            List<UserAcsIpDto> acsIpList = this.parseAcsIpListInfo(acsIpListStr);
            userDto.setAcsIpList(acsIpList);
        }

        // update entity from dto :: (null로 넘어오는 password 미처리 위해)
        // 사용자 정보userInfo 먼저 처리 후 user에 키값 세팅 (필드 위임)
        UserEntity userEntity = this.getDtlEntity(userDto.getUserNo());
        userMapstruct.updateFromDto(userDto, userEntity);
        // 잠금 -> 잠금해제시 로그인 실패 횟수 초기화
        boolean isUnlocking = "Y".equals(userEntity.acntStus.getLockedYn()) && "N".equals(userDto.getLockedYn());
        if (isUnlocking) userEntity.acntStus.setLgnFailCnt(0);

        // userEntity.setUserProflNo(this.userInfoReg(userEntity, userDto));
        // 잠금해제 상태로 업데이트시 로그인 실패 횟수 초기화 및 최종 로그인 일자=수정일자로 세팅
        if ("N".equals(userEntity.acntStus.getLockedYn())) {
            userEntity.acntStus.setLgnFailCnt(0);
            userEntity.acntStus.setLstLgnDt(DateUtils.getCurrDate());
        }
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
    public Boolean userInfoLock(final Integer userProflNo) throws Exception {
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
    public Boolean userInfoUnlock(final Integer userProflNo) throws Exception {
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
     * 사용자 정보UserInfo가 있는 계정 목록 조회
     * TODO: 어디어디 쓰지?
     */
    public Page<UserDto.LIST> getInfoUserList(final Pageable pageable) throws Exception {
        HashMap<String, Object> searchParamMap = new HashMap<>() {{
            put("hasUserInfo", true);
        }};
        return this.getPageDto(searchParamMap, pageable);
    }

    /**
     * 내부직원 목록 조회 (결재자 정보에 쓰임) (계정정보로 조회)
     *
     * @param yyStr (년도)
     */
    public Page<UserDto.LIST> getCrdtUserList(final String yyStr) throws Exception {
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
    public Page<UserDto.LIST> getCrdtUserList(
            final String startDtStr,
            final String endDtStr
    ) throws Exception {
        // 목록 검색
        Page<UserEntity> rsUserEntityList = userRepository.findAll(userSpec.searchCrdtUser(startDtStr, endDtStr), Pageable.unpaged());

        // List<Entity> -> List<ListDto>
        return this.pageEntityToDto(rsUserEntityList);
    }

    /**
     * 내부직원 연락처 목록 조회
     */
    public List<UserCttpcDto> getCrdtUserCttpcList(
            final String startDtStr,
            final String endDtStr
    ) throws Exception {
        List<UserDto.LIST> crdtUserList = this.getCrdtUserList(startDtStr, endDtStr)
                                             .getContent();

        return crdtUserList.stream()
                .map(listDto -> {
                    try {
                        return userMapstruct.toCttpcDto(listDto);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 내부직원 연락처 엑셀 다운로드 목록 조회
     */
    public List<Object> getCrdtUserCttpcListXlsx(
            final String startDtStr,
            final String endDtStr
    ) throws Exception {
        List<UserDto.LIST> crdtUserList = this.getCrdtUserList(startDtStr, endDtStr)
                                             .getContent();
        List<Object> rsCrdtUserCttpcXlsxObjList = new ArrayList<>();
        Map<String, String> header = new LinkedHashMap<>() {{
            put("이름", "이름");
            put("직급", "직급");
            put("부서", "부서");
            put("연락처", "연락처");
            put("이메일", "이메일");
        }};
        rsCrdtUserCttpcXlsxObjList.add(header);
        for (UserDto.LIST listDto : crdtUserList) {
            rsCrdtUserCttpcXlsxObjList.add(userMapstruct.toCttpcListXlsxDto(listDto));
        }
        return rsCrdtUserCttpcXlsxObjList;
    }

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
    //            String thisBrthdy = DateUtils.getCurrYearStr() + brthMnthDy;
    //            if (!todayStr.equals(thisBrthdy)) continue;
    //            brthdyUserList.add(user);
    //        }
    //    }
//
    //    return brthdyUserList;
    //}

}