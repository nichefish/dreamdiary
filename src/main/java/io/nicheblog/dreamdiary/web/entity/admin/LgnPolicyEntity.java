package io.nicheblog.dreamdiary.web.entity.admin;

import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;

/**
 * LgnPolicyEntity
 * <pre>
 *  로그인 정책 정보 Entity
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "LGN_POLICY")
@DynamicInsert      // null인 값은 (null로 insert하는 대신) insert에서 제외
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class LgnPolicyEntity
        implements Serializable {

    /**
     * 로그인 정책 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LGN_POLICY_NO")
    @Comment("로그인 정책 번호 (key)")
    private Integer lgnPolicyNo;

    /**
     * 로그인 최대 시도 횟수
     */
    @Column(name = "LGN_TRY_LMT")
    @Comment("로그인 최대 시도 횟수")
    private Integer lgnTryLmt;

    /**
     * 비밀번호 변경 일자
     */
    @Column(name = "PW_CHG_DY")
    @Comment("비밀번호 변경 일자")
    private Integer pwChgDy;

    /**
     * 미접속시 계정 잠금 일자
     */
    @Column(name = "LGN_LOCK_DY")
    @Comment("미접속시 계정 잠금 일자")
    private Integer lgnLockDy;

    /**
     * 비밀번호 초기화 값
     */
    @Column(name = "PW_FOR_RESET", length = 20)
    @Comment("비밀번호 초기화 값")
    private String pwForReset;

    /**
     * 삭제여부
     */
    @Builder.Default
    @Column(name = "DEL_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("삭제여부")
    private String delYn = "N";
}