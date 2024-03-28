package io.nicheblog.dreamdiary.web.entity.admin;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAtchEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * TmplatDefEntity
 * <pre>
 *  템플릿(사전입력폼) 정의 정보 Entity
 * </pre>
 *
 * @author nichefish
 * @extends BaseAtchEntity
 */
@Entity
@Table(name = "tmplat_def")
@DynamicInsert      // null인 값은 (null로 insert하는 대신) insert에서 제외
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE tmplat_def SET del_yn = 'Y' WHERE tmplat_def_no = ?")
public class TmplatDefEntity
        extends BaseAtchEntity
        implements Serializable {

    /**
     * 템플릿 정의 번호 (PK)
     */
    @Id
    @Column(name = "tmplat_def_no", length = 1000)
    @Comment("템플릿 정의 번호")
    private String tmplatDefNo;

    /**
     * 템플릿 정의 코드
     */
    @Column(name = "tmplat_def_cd", length = 1000)
    @Comment("템플릿 정의 코드")
    private String tmplatDefCd;

    /**
     * 템플릿 정의 이름
     */
    @Column(name = "tmplat_def_nm", length = 1000)
    @Comment("템플릿 정의 이름")
    private String tmplatDefNm;

    /**
     * 템플릿 구분 코드
     * (템플릿들이 해당 CLCD 아래 DTLCD를 구별값으로 갖게 됨)
     */
    @Column(name = "ctgr_cl_cd")
    @Comment("템플릿 구분 코드")
    private String ctgrClCd;

    /**
     * 정렬 순서
     */
    @Column(name = "sort_ordr")
    @Comment("정렬 순서")
    private Integer sortOrdr;

    /**
     * 사용여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "use_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("사용여부")
    private String useYn = "N";
}
