package io.nicheblog.dreamdiary.web.entity.user;

import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Date;

/**
 * UserStusInfo
 * <pre>
 *  사용자user에서 계정 상태 관련 정보 분리
 * </pre>
 *
 * @author nichefish
 */
@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStusEmbed {

    /**
     * 잠금여부
     */
    @Builder.Default
    @Column(name = "locked_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    protected String lockedYn = "N";

    /**
     * 마지막 로그인 일시
     */
    @DateTimeFormat(pattern = DateUtils.PTN_DATETIME)
    @Column(name = "lst_lgn_dt")
    @Comment("마지막 로그인 일시")
    private Date lstLgnDt;

    /**
     * 로그인 실패 횟수
     */
    @Builder.Default
    @Column(name = "lgn_fail_cnt", columnDefinition = "INT DEFAULT 0")
    @Comment("로그인 실패 횟수")
    private Integer lgnFailCnt = 0;

    /**
     * 패스워드 변경일시
     */
    @DateTimeFormat(pattern = DateUtils.PTN_DATETIME)
    @Column(name = "pw_chg_dt")
    @Comment("패스워드 변경일시")
    private Date pwChgDt;

    /**
     * 패스워드 리셋 필요여부
     */
    @Builder.Default
    @Column(name = "needs_pw_reset", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("패스워드 리셋 필요여부")
    private String needsPwReset = "N";

    /**
     * 장기 미로그인 패스 체크 여부
     */
    @Builder.Default
    @Column(name = "dormant_bypass_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("장기 미로그인 패스 체크 여부")
    private String dormantBypassYn = "N";

    /**
     * 본인신청여부
     */
    @Builder.Default
    @Column(name = "reqst_yn", length = 1, columnDefinition = "CHAR DEFAULT 'Y'")
    @Comment("본인신청여부")
    private String reqstYn = "Y";

    /**
     * 승인여부
     */
    @Builder.Default
    @Column(name = "cf_yn", length = 1, columnDefinition = "CHAR DEFAULT 'Y'")
    @Comment("승인여부")
    private String cfYn = "Y";

    /* ----- */

    /** 생성자 */
    public UserStusEmbed(UserDto dto) {
        this();
        this.reqstYn = dto.getReqstYn();
        this.cfYn = dto.getCfYn();
    }
}