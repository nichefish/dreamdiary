package io.nicheblog.dreamdiary.web.model.user;

import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
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
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserDto
        extends BaseAtchDto
        implements Identifiable<Integer> {

    /** 사용자 고유 번호 (PK) */
    private Integer userNo;
    /** 아이디 */
    private String userId;
    /** 비밀번호 */
    private String password;
    /** 표시이름 */
    private String nickNm;
    /** 프로필 이미지 URL */
    private String proflImgUrl;

    /** 연락처 */
    private String cttpc;
    /** E-mail ID */
    private String emailId;
    /** E-mail 뒷부분 */
    private String emailDomain;

    /** 잠금여부 */
    @Builder.Default
    private String lockedYn = "N";

    /** 퇴사여부 */
    @Builder.Default
    private String retireYn = "N";
    /** 퇴사일 */
    private String retireDt;

    /**
     * 등록/수정시 사용자 정보 저장할지 말지 화면에서 넘겨받는 임시필드
     */
    @Builder.Default
    private String userProflYn = "N";
    /** 사용자 정보 (위임) */
    private UserProflDto userProfl;

    /** 사용자 권한 정보 */
    private List<UserAuthRoleDto> authList;
    /** 사용자 권한 정보(문자열) */
    private List<String> authStrList;
    /** 사용자 권한 정보(문자열) */
    private String authListStr;

    /** 본인신청여부 */
    private String isReqst;
    /** 승인여부 */
    private Boolean isCf;
    
    /* ----- */

    /** 잠금여부 채크 */
    public Boolean getIsLocked() {
        return "Y".equals(this.lockedYn);
    }

    @Override
    public Integer getKey() {
        return this.userNo;
    }

    /* ----- */

    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class DTL extends UserDto {
        /** 계정 설명 (관리자용) */
        private String cn;

        /* ----- */

        /** 접속IP 사용 여부 체크 */
        @Builder.Default
        private Boolean useAcsIp = false;
        /** 접속 IP 사용 여부 */
        @Builder.Default
        private String useAcsIpYn = "N";
        /** 접속 IP 정보 */
        private String acsIpListStr;
        /** 접속 IP 정보 */
        private List<UserAcsIpDto> acsIpList;
    }

    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    public static class LIST extends UserDto {
        /** 입사일 */
        private String ecnyDt;
        /** 입사일 */
        private String userNm;

        /* ----- */

        /** 내 정보 여부 채크 */
        public Boolean getIsMe() {
            return (AuthUtils.isRegstr(this.getUserId()));       // 인자로 넘긴 ID와 세션의 사용자 ID 비교
        }
    }
}
