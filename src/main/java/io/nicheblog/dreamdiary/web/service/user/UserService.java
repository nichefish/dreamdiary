package io.nicheblog.dreamdiary.web.service.user;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.file.service.CmmFileService;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.global.util.ChineseCalUtils;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.admin.LgnPolicyEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserInfoEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserInfoItemEntity;
import io.nicheblog.dreamdiary.web.mapstruct.user.UserInfoMapstruct;
import io.nicheblog.dreamdiary.web.mapstruct.user.UserMapstruct;
import io.nicheblog.dreamdiary.web.model.user.UserAcsIpDto;
import io.nicheblog.dreamdiary.web.model.user.UserCttpcListDto;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import io.nicheblog.dreamdiary.web.model.user.UserListDto;
import io.nicheblog.dreamdiary.web.repository.user.UserInfoRepository;
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
import org.springframework.util.CollectionUtils;

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
        implements BaseMultiCrudService<UserDto, UserListDto, Integer, UserEntity, UserRepository, UserSpec, UserMapstruct, CmmFileService> {

    @Resource(name = "userRepository")
    private UserRepository userRepository;
    @Resource(name = "userSpec")
    private UserSpec userSpec;
    @Resource(name = "cmmFileService")
    private CmmFileService cmmFileService;

    private final UserMapstruct userMapstruct = UserMapstruct.INSTANCE;
    private final UserInfoMapstruct userInfoMapstruct = UserInfoMapstruct.INSTANCE;

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

    @Override
    public CmmFileService getFileService() {
        return this.cmmFileService;
    }

    @Resource(name = "userInfoRepository")
    private UserInfoRepository userInfoRepository;

    @Resource(name = "lgnPolicyService")
    private LgnPolicyService lgnPolicyService;

    @Resource(name = "passwordEncoder")
    private PasswordEncoder passwordEncoder;

    /**
     * 사용자 관리 > 사용자 단일 조회 (Dto Level) (Long userId / String userId)
     */
    public UserDto getDtlDto(final String userNo) throws Exception {
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
    public UserDto regist(final UserDto userDto) throws Exception {
        // 계정 잠금여부 체크박스 값 세팅
        if (!"Y".equals(userDto.getLockYn())) userDto.setLockYn("N");

        // 접속 IP 사용여부 체크박스 값 세팅
        if (!"Y".equals(userDto.getAcsIpYn()) || StringUtils.isEmpty(userDto.getAcsIpInfoListStr())) {
            userDto.setAcsIpYn("N");
        } else {
            // 접속 IP 사용"Y"시 접속 IP 세팅
            String acsIpInfoListStr = userDto.getAcsIpInfoListStr();
            List<UserAcsIpDto> acsIpInfoList = this.parseAcsIpListInfo(acsIpInfoListStr);
            userDto.setAcsIpInfoList(acsIpInfoList);
        }

        // Dto -> Entity
        // 사용자 정보userInfo 먼저 처리 후 user에 키값 세팅 (필드 위임)
        UserEntity userEntity = userMapstruct.toEntity(userDto);
        userEntity.setUserInfoNo(this.userInfoReg(userEntity, userDto));
        userEntity.setUserPw(passwordEncoder.encode(userDto.getUserPw()));
        userEntity.setCfYn("Y");
        // insert
        UserEntity rsltEntity = userRepository.save(userEntity);
        UserDto rsltDto = userMapstruct.toDto(rsltEntity);
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
        String userInfoYn = userDto.getUserInfoYn();
        Integer userInfoNo = userEntity.getUserInfoNo();
        if ("N".equals(userInfoYn) && userInfoNo == null) {
            userEntity.setUserInfo(null);
            return null;
        }
        UserInfoEntity rsUserInfoEntity = userEntity.getUserInfo();
        if (rsUserInfoEntity == null) {
            rsUserInfoEntity = userInfoRepository.findById(userInfoNo)
                                                 .orElse(new UserInfoEntity());
        }
        if ("Y".equals(userInfoYn)) {
            userInfoMapstruct.updateFromDto(userDto.getUserInfo(), rsUserInfoEntity);
            this.sortUserInfoItemList(rsUserInfoEntity);      // 빈값 걸러내기+순번매기기
        } else {
            rsUserInfoEntity.setDelYn("Y");
        }
        return userInfoRepository.save(rsUserInfoEntity).getUserInfoNo();
    }

    /**
     * 사용자 관리 > 접속 IP 목록 파싱 (메소드 분리)
     * Tagify (ex.) = [{"value":"123.123.123.123"},{"value":"234.234.234.234"}] 문자열 형식으로 넘어온댜.
     * 문자열을 JSON Array로 변환하여 직접 DTO에 세팅한다.
     */
    public List<UserAcsIpDto> parseAcsIpListInfo(final String acsIpInfoListStr) {
        JSONArray jArray = new JSONArray(acsIpInfoListStr);
        List<UserAcsIpDto> acsIpInfoList = new ArrayList<>();
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject json = jArray.getJSONObject(i);
            UserAcsIpDto acsIp = new UserAcsIpDto();
            acsIp.setAcsIp(json.getString("value"));
            acsIpInfoList.add(acsIp);
        }
        return acsIpInfoList;
    }

    /**
     * 사용자 관리 > 사용자 정보 추가정보 정리 (null값 걸러내기 + sorting)
     */
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

    /**
     * 사용자 관리 > 사용자 비밀번호 초기화
     */
    public Boolean userPwReset(final Integer userNo) throws Exception {
        // Entity 레벨 조회
        UserEntity rsUserEntity = this.getDtlEntity(userNo);
        if (rsUserEntity == null) return false;
        // 로그인 설정 조회 (cachable)
        LgnPolicyEntity rsLgnPolicyEntity = lgnPolicyService.getLgnPolicyDtlEntity();
        String pwForReset = rsLgnPolicyEntity.getPwForReset();
        // update
        rsUserEntity.setUserPw(passwordEncoder.encode(pwForReset));
        rsUserEntity.setNeedsPwReset("Y");
        rsUserEntity.setPwChgDt(DateUtils.getCurrDate());
        Integer rsId = userRepository.saveAndFlush(rsUserEntity)
                                     .getUserNo();
        return (rsId != null);
    }

    /**
     * 사용자 관리 > 사용자 수정
     */
    @Override
    public UserDto modify(
            final UserDto userDto,
            final Integer key
    ) throws Exception {
        // 계정 잠금여부 체크박스 값 세팅
        if (!"Y".equals(userDto.getLockYn())) userDto.setLockYn("N");

        // 접속 IP 사용여부 체크박스 값 세팅
        if (!"Y".equals(userDto.getAcsIpYn()) || StringUtils.isEmpty(userDto.getAcsIpInfoListStr())) {
            userDto.setAcsIpYn("N");
        } else {
            // 접속 IP 사용"Y"시 접속 IP 세팅
            String acsIpInfoListStr = userDto.getAcsIpInfoListStr();
            List<UserAcsIpDto> acsIpInfoList = this.parseAcsIpListInfo(acsIpInfoListStr);
            userDto.setAcsIpInfoList(acsIpInfoList);
        }

        // update entity from dto :: (null로 넘어오는 password 미처리 위해)
        // 사용자 정보userInfo 먼저 처리 후 user에 키값 세팅 (필드 위임)
        UserEntity userEntity = this.getDtlEntity(userDto.getUserNo());
        userMapstruct.updateFromDto(userDto, userEntity);
        // 잠금 -> 잠금해제시 로그인 실패 횟수 초기화
        if ("Y".equals(userEntity.getLockYn()) && "N".equals(userDto.getLockYn())) userEntity.setLgnFailCnt(0);

        userEntity.setUserInfoNo(this.userInfoReg(userEntity, userDto));
        // 잠금해제 상태로 업데이트시 로그인 실패 횟수 초기화 및 최종 로그인 일자=수정일자로 세팅
        if ("N".equals(userEntity.getLockYn())) {
            userEntity.setLgnFailCnt(0);
            userEntity.setLstLgnDt(DateUtils.getCurrDate());
        }
        // update
        UserEntity rsltEntity = this.updt(userEntity);
        UserDto rsltDto = userMapstruct.toDto(rsltEntity);
        rsltDto.setIsSuccess((rsltEntity.getUserNo() != null));
        return rsltDto;
    }

    /**
     * 사용자 관리 > 사용자 목록 엑셀 다운로드 목록 조회
     */
    // public List<Object> userListXlsx(final Map<String, Object> searchParamMap) throws Exception {
    //     // 목록 검색
    //     Page<UserEntity> userEntityList = this.getListEntity(searchParamMap, Pageable.unpaged());
//
    //     // List<Entity> -> List<ListXlsxDto>
    //     List<Object> rsUserListXlxsDtoList = new ArrayList<>();
    //     for (UserEntity entity : userEntityList) {
    //         UserListXlsxDto dto = userMapstruct.toListXlsxDto(entity);
    //         dto.setAcsIpInfoListStr(entity.getAcsIpInfoListStr());
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

        LgnPolicyEntity rsEntity = lgnPolicyService.getLgnPolicyDtlEntity();
        Integer lgnLockDy = rsEntity.getLgnLockDy();

        UserEntity user = this.getDtlEntity(userId);
        Date lastLgnDt = user.getLstLgnDt();
        if (lastLgnDt == null) lastLgnDt = user.getRegDt();
        Date dormantDt = DateUtils.getDateAddDay(lastLgnDt, lgnLockDy);
        return (dormantDt == null || dormantDt.compareTo(DateUtils.getCurrDate()) < 0);
    }

    /**
     * 사용자정보 잠금
     */
    public Boolean userInfoLock(final Integer userInfoNo) throws Exception {
        // Entity 레벨 조회
        UserEntity rsEntity = this.getDtlEntity(userInfoNo);
        if (rsEntity == null) return false;
        // lockYn 플래그 업데이트
        rsEntity.setLockYn("Y");
        Integer rsId = userRepository.saveAndFlush(rsEntity)
                                     .getUserInfoNo();
        return (rsId != null);
    }

    /**
     * 사용자정보 잠금 해제
     */
    public Boolean userInfoUnlock(final Integer userInfoNo) throws Exception {
        // Entity 레벨 조회
        UserEntity rsEntity = this.getDtlEntity(userInfoNo);
        if (rsEntity == null) return false;
        // lockYn 플래그 + 최종접속일 업데이트
        // TODO: 최종접속일 이거 어찌할꼬...
        rsEntity.setLockYn("N");
        // rsEntity.setDormantYn("N");
        rsEntity.setLstLgnDt(DateUtils.getCurrDate());
        Integer rsId = userRepository.saveAndFlush(rsEntity)
                                     .getUserInfoNo();
        return (rsId != null);
    }

    /**
     * 사용자정보UserInfo가 있는 계정 목록 조회
     * TODO: 어디어디 쓰지?
     */
    public Page<UserListDto> getInfoUserList(final Pageable pageable) throws Exception {
        HashMap<String, Object> searchParamMap = new HashMap<>() {{
            put("hasUserInfo", true);
        }};
        return this.getListDto(searchParamMap, pageable);
    }

    /**
     * 내부직원 목록 조회 (결재자 정보에 쓰임) (계정정보로 조회)
     *
     * @param yyStr (년도)
     */
    public Page<UserListDto> getCrdtUserList(final String yyStr) throws Exception {
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
    public Page<UserListDto> getCrdtUserList(
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
    public List<UserCttpcListDto> getCrdtUserCttpcList(
            final String startDtStr,
            final String endDtStr
    ) throws Exception {
        List<UserListDto> crdtUserList = this.getCrdtUserList(startDtStr, endDtStr)
                                             .getContent();

        return crdtUserList.stream()
                .map(listDto -> {
                    try {
                        return userMapstruct.toCttpcListDto(listDto);
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
        List<UserListDto> crdtUserList = this.getCrdtUserList(startDtStr, endDtStr)
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
        for (UserListDto listDto : crdtUserList) {
            rsCrdtUserCttpcXlsxObjList.add(userMapstruct.toCttpcListXlsxDto(listDto));
        }
        return rsCrdtUserCttpcXlsxObjList;
    }

    /**
     * 생일인 내부직원 목록 조회
     * JPA로 해당조건 조회하기에 난항이 있어서 내부직원 조회 후 for문 처리
     */
    public List<UserListDto> getBrthdyCrdtUser() throws Exception {
        String todayStr = DateUtils.getCurrDateStr(DateUtils.PTN_DATE);
        String tomorrowStr = DateUtils.getNextDateStr(DateUtils.PTN_DATE);
        Page<UserListDto> crdtUserPage = this.getCrdtUserList(todayStr, tomorrowStr);
        List<UserListDto> crdtUserList = crdtUserPage.getContent();
        if (CollectionUtils.isEmpty(crdtUserList)) return null;

        List<UserListDto> brthdyUserList = new ArrayList<>();
        for (UserListDto user : crdtUserList) {
            String brthdy = user.getBrthdy();
            if (StringUtils.isEmpty(brthdy)) continue;
            // 음력 / 양력 구분해서 적용
            if ("Y".equals(user.getLunarYn())) {
                // 음력일 경우 = 1) 오늘 날짜를 음력 날짜로 변환 후 2) 아래 로직 그대로 적용.
                String todayLunarStr = ChineseCalUtils.solarToLunarStr(todayStr, DateUtils.PTN_DATE);
                String todayLunarYear = todayLunarStr.substring(0, 4);

                String brthMnthDy = brthdy.substring(4);
                String thisLunarBrthdy = todayLunarYear + brthMnthDy;

                if (!todayLunarStr.equals(thisLunarBrthdy)) continue;
                brthdyUserList.add(user);
            } else {
                // 양력일 경우
                String brthMnthDy = brthdy.substring(4);
                String thisBrthdy = DateUtils.getCurrYearStr() + brthMnthDy;
                if (!todayStr.equals(thisBrthdy)) continue;
                brthdyUserList.add(user);
            }
        }

        return brthdyUserList;
    }

}