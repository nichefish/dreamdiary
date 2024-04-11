package io.nicheblog.dreamdiary.web.test.user;

import io.nicheblog.dreamdiary.web.model.user.emplym.UserEmplymDto;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import io.nicheblog.dreamdiary.web.model.user.reqst.UserReqstDto;
import lombok.experimental.UtilityClass;

/**
 * UserTestUtils
 * <pre>
 *  사용자 관련 테스트 공통기능 모아놓은 유틸리티 클래스
 * </pre>
 * 
 * @author nichefish 
 */
@UtilityClass
public class UserTestUtils {

    /**
     * 사용자 신청 정보 Dto 객체 생성
     */
    public static UserReqstDto createUserReqst(UserProflDto profl, UserEmplymDto emplym) {
        // 갹체 생성
        return UserReqstDto.builder()
                .userId("tester")
                .password("password!")
                .nickNm("표시이름쓰")
                .emailId("emailId")
                .emailDomain("emailDomain.com")
                .cttpc("010-0101-0101")
                .useAcsIpYn("Y")
                .acsIpListStr("")
                .cn("cncn")
                .profl(profl)
                .emplym(emplym)
                .build();
    }

    /**
     * 사용자 신청 - 프로필 정보 Dto 객체 생성 :: 메소드 분리
     */
    public static UserProflDto createUserProfl() {
        // 갹체 생성
        return UserProflDto.builder()
                .brthdy("1987-03-11")
                .proflCn("profl_cncn")
                .build();
    }

    /**
     * 사용자 신청 - 인사정보 Dto 객체 생성 :: 메소드 분리
     */
    public static UserEmplymDto createUserEmplym() {
        // 갹체 생성
        return UserEmplymDto.builder()
                .userNm("1987-03-11")
                .cmpyCd("profl cncn")
                .teamCd("profl cncn")
                .emplymCd("profl cncn")
                .rankCd("daeri")
                .apntcYn("N")
                .emplymEmailId("mailmale")
                .emplymEmailDomain("gmail.com")
                .emplymCttpc("010-0101-0101")
                .ecnyDt("2020-05-25")
                .retireYn("Y")
                .retireDt("2024-02-29")
                .acntBank("신한은행")
                .acntNo("487-13-313191")
                .emplymCn("emplym_cncn")
                .build();
    }
}
