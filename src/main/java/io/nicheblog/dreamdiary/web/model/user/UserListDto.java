package io.nicheblog.dreamdiary.web.model.user;

import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * UserListDto
 * <pre>
 *  사용자(계정) 정보 목록 조회 Dto
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
public class UserListDto
        extends BaseAtchDto {

    /**
     * 목록 순번
     */
    private Long rnum;

    /**
     * 사용자 고유 ID
     */
    private Integer userNo;
    /**
     * 권한코드
     */
    private String authCd;
    /**
     * 권한이름
     */
    private String authNm;
    /**
     * 아이디
     */
    private String userId;
    /**
     * 이름
     */
    private String userNm;
    /**
     * 직급
     */
    private String jobTitleNm;
    /**
     * 수습여부
     */
    @Builder.Default
    private String apntcYn = "N";
    /**
     * 소속(팀)
     */
    private String cmpyNm;
    /**
     * 소속(팀)
     */
    private String teamNm;
    /**
     * 재직구분이름
     */
    private String emplymNm;
    /**
     * 입사일
     */
    private String ecnyDt;
    /**
     * 연락처
     */
    private String cttpc;
    /**
     * 이메일
     */
    private String email;
    /**
     * 생년월일
     */
    private String brthdy;
    /**
     * 음력여부
     */
    @Builder.Default
    private String lunarYn = "N";
    /**
     * 직원정보 존재여부
     */
    @Builder.Default
    private String userInfoYn = "N";
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
     * 잠금여부
     */
    @Builder.Default
    private String lockYn = "N";

    /**
     * 계정 설명 (관리자용)
     */
    private String userDc;

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
