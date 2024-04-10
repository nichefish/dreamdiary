package io.nicheblog.dreamdiary.web.entity.user;

import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * UserAcsIpEntity
 * <pre>
 *  사용자(계정) 정보 > 접속가능 IP Entity
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "user_acs_ip")
@DynamicInsert      // null인 값은 (null로 insert하는 대신) insert에서 제외
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE user_acs_ip SET del_yn = 'Y' WHERE user_acs_ip_no = ?")
public class UserAcsIpEntity {

    /** 사용자 정보 접속가능 IP 번호 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_acs_ip_no")
    @Comment("사용자 정보 접속가능 IP 번호 (PK)")
    private Integer userAcsIpNo;

    /** 사용자 정보 */
    @ManyToOne
    @JoinColumn(name = "user_no", referencedColumnName = "user_no")
    @Comment("사용자 정보")
    private UserEntity user;

    /** 접속가능 IP */
    @Column(name = "acs_ip", length = 20)
    @Comment("접속가능 IP")
    private String acsIp;

    /** 삭제 여부 */
    @Builder.Default
    @Column(name = "del_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("삭제 여부")
    private String delYn = "N";
}
