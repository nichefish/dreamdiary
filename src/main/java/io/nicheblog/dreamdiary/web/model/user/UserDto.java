package io.nicheblog.dreamdiary.web.model.user;

import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import io.nicheblog.dreamdiary.web.entity.user.UserAuthRoleEntity;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
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
    private String password;
    /**
     * 표시이름
     */
    private String nickNm;
    /**
     * 프로필 이미지 URL
     */
    private String proflImgUrl;

    /**
     * 잠금여부
     */
    @Builder.Default
    private String lockedYn = "N";

    /**
     * 접속 IP 사용 여부
     */
    @Builder.Default
    private String useAcsIpYn = "N";
    /**
     * 접속 IP 정보 (String)
     */
    private String acsIpListStr;
    /**
     * 접속 IP 목록
     */
    private List<UserAcsIpDto> acsIpList;

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
     * 등록/수정시 사용자 정보 저장할지 말지 화면에서 넘겨받는 임시필드
     */
    @Builder.Default
    private String userProflYn = "N";
    /**
     * 사용자 정보 (위임)
     */
    private UserProflDto userProfl;

    /**
     * 사용자 권한 정보
     */
    private List<UserAuthRoleDto> auth;

    /**
     * 본인신청여부
     */
    private String reqstYn;
    /**
     * 승인여부
     */
    private String cfYn;

    /**
     * 성공여부
     */
    @Builder.Default
    public Boolean isSuccess = false;
    
    /* ----- */

    /**
     * 접속IP 사용 여부 채크
     */
    public Boolean getIsAcsIpY() {
        return "Y".equals(this.useAcsIpYn);
    }

    /**
     * 내 정보 여부 채크
     */
    public Boolean getIsMe() {
        return (AuthUtils.isRegstr(this.userId));       // 인자로 넘긴 ID와 세션의 사용자 ID 비교
    }

    /** 잠금여부 채크 */
    public Boolean getIsLocked() {
        return "Y".equals(this.lockedYn);
    }

    /** 승인여부 채크 */
    public Boolean getIsCf() {
        return "Y".equals(this.cfYn);
    }
}
