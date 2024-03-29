package io.nicheblog.dreamdiary.web.entity.notice;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseEnhcPostEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * NoticeSmpEntity
 * 공지사항 간소화 Entity
 * (NoticeEntity 태그 관련 정보 제거 = 연관관계 순환참조 방지 위함. 나머지는 동일)
 * (BaseClsfEntity 상속)
 *
 * @author nichefish
 */
@Entity
@Table(name = "notice")
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE notice SET del_yn = 'Y' WHERE post_no = ?")
public class NoticeSmpEntity
        extends BaseEnhcPostEntity {

    /** 필수: 컨텐츠 타입 */
    private static final String CONTENT_TYPE = ContentType.NOTICE.key;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE + "_CTGR_CD";

    /** 글 번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    @Comment("공지사항 번호")
    private Integer postNo;

    /** 컨텐츠 타입 */
    @Builder.Default
    @Column(name = "content_type")
    private String contentType = CONTENT_TYPE;

    /** 팝업 노출여부 */
    @Builder.Default
    @Column(name = "popup_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("팝업 노출여부")
    private String popupYn = "N";
}
