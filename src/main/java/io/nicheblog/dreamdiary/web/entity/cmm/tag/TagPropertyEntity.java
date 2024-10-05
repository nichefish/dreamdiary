package io.nicheblog.dreamdiary.web.entity.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * TagPropertyEntity
 * <pre>
 *  태그 속성 Entity
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudEntity
 */
@Entity
@Table(name = "tag")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE tag SET del_yn = 'Y' WHERE tag_no = ?")
public class TagPropertyEntity
        extends BaseCrudEntity {

    /**
     * 태그 속성 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_property_no")
    @Comment("태그 속성 번호 (PK)")
    private Integer tagPropertyNo;

    /**
     * 참조 태그 번호
     */
    @Column(name = "ref_tag_no")
    @Comment("참조 태그 번호")
    private Integer tagNo;

    /**
     * 참조 컨텐츠 타입
     */
    @Column(name = "ref_content_type")
    @Comment("컨텐츠 타입")
    private String refContentType;

    /* ----- */

    /**
     * 프로퍼티 키
     */
    @Column(name = "property_key")
    @Comment("프로퍼티 키")
    private String propertyKey;

    /**
     * 프로퍼티 값
     */
    @Column(name = "property_value")
    @Comment("프로퍼티 값")
    private String propertyValue;

    /**
     * 프로퍼티 JSON (JSON String)
     */
    @Column(name = "property_json")
    @Comment("프로퍼티 JSON")
    private String propertyJson;
}