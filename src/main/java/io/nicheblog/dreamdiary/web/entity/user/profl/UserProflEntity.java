package io.nicheblog.dreamdiary.web.entity.user.profl;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.Date;

/**
 * UserInfoEntity
 * <pre>
 *  사용자 정보 Entity :: 계정(User) 정보에 귀속됨
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudEntity
 */
@Entity
@Table(name = "user_profl")
@DynamicInsert      // null인 값은 (null로 insert하는 대신) insert에서 제외
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user"}, callSuper = true)
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE user_profl SET DEL_YN = 'Y' WHERE user_profl_no = ?")
public class UserProflEntity
        extends BaseCrudEntity {

    /**
     * 사용자 정보 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_profl_no")
    @Comment("사용자 정보 번호")
    private Integer userProflNo;

    /**
     * 계정 정보
     */
    @OneToOne
    @JoinColumn(name = "user_no", referencedColumnName = "user_no", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("계정 정보")
    private UserEntity user;

    /**
     * 이름
     */
    @Column(name = "user_nm", length = 20)
    @Comment("이름")
    private String userNm;

    /**
     * 연락처
     */
    @Column(name = "cttpc", length = 20)
    @Comment("연락처")
    private String cttpc;

    /**
     * Email 주소 (사내메일)
     */
    @Column(name = "email", length = 40)
    @Comment("Email 주소")
    private String email;

    /**
     * 생년월일
     */
    @DateTimeFormat(pattern = DateUtils.PTN_DATE)
    @Column(name = "brthdy")
    @Comment("생년월일")
    private Date brthdy;

    /**
     * 음력여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "lunar_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("음력여부")
    private String lunarYn = "N";

    /**
     * 사용자 설명 (관리자용)
     */
    @Column(name = "profl_dc", length = 1000)
    @Comment("사용자 설명")
    private String proflDc;

    /**
     * 사용자 정보 추가추가 목록
     * TODO
     */
    // @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    // @JoinColumn(name = "user_profl_no")
    // @Fetch(FetchMode.SELECT)
    // @OrderBy("state.sortOrdr ASC")
    // @NotFound(action = NotFoundAction.IGNORE)
    // @Comment("사용자 정보 추가추가 목록")
    // private List<UserInfoItemEntity> itemList;

    /* ----- */

    /**
     * 서브엔티티 List 처리를 위한 Setter (override)
     * 한 번 Entity가 생성된 이후부터는 new List를 할당하면 안 되고 계속 JPA 이력이 추적되어야 한다.
     */
    // public void setItemList(final List<UserInfoItemEntity> itemList) {
    //     if (CollectionUtils.isEmpty(itemList)) return;
    //     if (this.itemList == null) {
    //         this.itemList = itemList;
    //     } else {
    //         this.itemList.clear();
    //         this.itemList.addAll(itemList);
    //     }
    // }
}