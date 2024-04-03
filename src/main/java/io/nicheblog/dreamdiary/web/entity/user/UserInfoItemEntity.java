package io.nicheblog.dreamdiary.web.entity.user;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

/**
 * UserInfoItemEntity
 * <pre>
 *  사용자 정보 항목 Entity :: 사용자 정보(UserInfo)에 귀속됨
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "user_profl_item")
@DynamicInsert      // null인 값은 (null로 insert하는 대신) insert에서 제외
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@RequiredArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE user_profl_item SET DEL_YN = 'Y' WHERE user_profl_item_no = ?")
public class UserInfoItemEntity
        extends BaseCrudEntity {

    /**
     * 사용자 정보 항목 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_profl_item_no")
    @Comment("사용자 정보 항목 번호 (key)")
    private Integer userInfoItemNo;

    /**
     * 사용자 정보
     */
    @ManyToOne
    @JoinColumn(name = "user_profl_no", referencedColumnName = "user_profl_no")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("사용자 정보")
    private UserProflEntity userInfo;

    /** 이름 */
    @Column(name = "item_nm", length = 100)
    @Comment("이름")
    private String itemNm;

    /** 항목 */
    @Column(name = "item_cn", length = 200)
    @Comment("항목")
    private String itemCn;

    /** 비고 */
    @Column(name = "item_dc", length = 1000)
    @Comment("비고")
    private String itemDc;

    /** 정렬 순서 */
    @Column(name = "sort_ordr")
    @Comment("정렬 순서")
    private Integer sortOrdr;

}