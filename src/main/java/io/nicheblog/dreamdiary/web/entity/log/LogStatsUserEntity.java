package io.nicheblog.dreamdiary.web.entity.log;

import io.nicheblog.dreamdiary.global.cmm.log.entity.LogActvtyEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.List;

/**
 * UserEntity
 * 계정 정보 Entity :: 사용자 정보 Entity를 위임 필드로 가짐
 * BaseAuditEntity 상속 *
 */
@Entity
@Table(name = "user")
@DynamicInsert      // null인 값은 (null로 insert하는 대신) insert에서 제외
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
public class LogStatsUserEntity
        extends BaseCrudEntity {

    /**
     * 사용자 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    @Comment("사용자 번호 (key)")
    private Integer userNo;

    /**
     * 사용자 아이디
     */
    @Column(name = "user_id", length = 20, unique = true)
    @Comment("사용자 아이디")
    private String userId;

    /* ----- */

    /**
     * 활동(접속) 횟수
     */
    @Column(name = "actvty_cnt")
    @Comment("활동(접속) 횟수")
    private Long actvtyCnt;

    /**
     * 활동(접속) 목록
     */
    @OneToMany
    @JoinColumn(name = "log_user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("활동(접속) 목록")
    private List<LogActvtyEntity> actvtyList;

    /* ----- */

    /**
     * 생성자
     */
    public LogStatsUserEntity(
            final String userId,
            final Long actvtyCnt
    ) {
        this.userId = userId;
        this.actvtyCnt = actvtyCnt;
    }
}