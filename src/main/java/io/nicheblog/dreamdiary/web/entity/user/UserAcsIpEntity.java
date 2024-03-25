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
@Table(name = "USER_ACS_IP")
@DynamicInsert      // null인 값은 (null로 insert하는 대신) insert에서 제외
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "DEL_YN='N'")
@SQLDelete(sql = "UPDATE USER_ACS_IP SET DEL_YN = 'Y' WHERE USER_ACS_IP_NO = ?")
public class UserAcsIpEntity {

    /**
     * 사용자 정보 접속가능 IP 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ACS_IP_NO")
    @Comment("사용자 정보 접속가능 IP 번호")
    private Integer userAcsIpNo;

    /**
     * 사용자 정보
     */
    @ManyToOne
    @JoinColumn(name = "USER_NO", referencedColumnName = "USER_NO")
    @Comment("사용자 정보")
    private UserEntity user;

    /**
     * 접속가능 IP
     */
    @Column(name = "ACS_IP", length = 20)
    @Comment("접속가능 IP")
    private String acsIp;

    /**
     * 삭제여부
     */
    @Builder.Default
    @Column(name = "DEL_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("삭제여부")
    private String delYn = "N";
}
