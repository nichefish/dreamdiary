package io.nicheblog.dreamdiary.global.cmm.log.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * LogActvtyUrlNmEntity
 * 로그 작업URL 한글 매칭 Entity
 */
@Entity
@Table(name = "LOG_ACTVTY_URL_NM")
@DynamicInsert      // null인 값은 (null로 insert하는 대신) insert에서 제외
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "del_yn='N'")
public class LogActvtyUrlNmEntity
        implements Serializable {

    /**
     * URL (PK)
     */
    @Id
    @Column(name = "URL", length = 200, updatable = false)
    @Comment("URL (key)")
    protected String url;

    /**
     * URL 이름
     */
    @Column(name = "URL_NM", length = 2000)
    @Comment("URL 이름")
    private String urlNm;

    /**
     * 삭제 여부
     */
    @Builder.Default
    @Column(name = "DEL_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("삭제 여부")
    private String delYn = "N";
}
