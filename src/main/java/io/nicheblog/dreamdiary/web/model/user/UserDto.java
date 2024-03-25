package io.nicheblog.dreamdiary.web.model.user;

import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * UserDto
 * <pre>
 *  사용자(계정) 정보 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAtchDto
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserDto
        extends BaseAtchDto {

    /**
     * 사용자 고유 ID (PK)
     */
    private Integer userNo;
    /**
     * 아이디
     */
    private String userId;
    /**
     * 비밀번호
     */
    private String userPw;
    /**
     * 권한코드
     */
    private String authCd;
    /**
     * 권한이름
     */
    private String authNm;
    /**
     * 이름
     */
    private String nickNm;
    /**
     * 잠금여부
     */
    @Builder.Default
    private String lockYn = "N";
    /**
     * 접속 IP 사용여부
     */
    @Builder.Default
    private String acsIpYn = "N";
    /**
     * 접속 IP 정보 (String)
     */
    private String acsIpInfoListStr;
    /**
     * 접속 IP 목록
     */
    private List<UserAcsIpDto> acsIpInfoList;

    /**
     * 퇴사여부
     */
    @Builder.Default
    private String retireYn = "N";
    /**
     * 퇴사일
     */
    private String retireDt;

    /**
     * 계정 설명 (관리자용)
     */
    private String userDc;

    /**
     * 사용자 정보 (위임)
     */
    private UserInfoDto userInfo;
    /**
     * 등록/수정시 사용자 정보 저장할지 말지 화면에서 넘겨받는 임시필드
     */
    @Builder.Default
    private String userInfoYn = "N";

    /**
     * 성공여부
     */
    @Builder.Default
    public Boolean isSuccess = false;

    /**
     * 프로필 이미지 URL
     */
    private String proflImgUrl;
    /**
     * 본인신청여부
     */
    private String reqstYn;
    /**
     * 승인여부
     */
    private String cfYn;
    /* ----- */

    /**
     * 접속IP 사용여부 Y
     */
    public Boolean getIsAcsIpY() {
        return "Y".equals(this.acsIpYn);
    }

    ;

    /**
     * 나
     */
    public Boolean getIsMe() {
        return (AuthUtils.isRegstr(this.userId));       // 인자로 넘긴 ID와 세션의 사용자 ID 비교
    }

    /**
     * 잠금여부
     */
    public Boolean getIsLocked() {
        return "Y".equals(this.lockYn);
    }

    /**
     * 승인여부
     */
    public Boolean getIsCf() {
        return "Y".equals(this.cfYn);
    }
}
