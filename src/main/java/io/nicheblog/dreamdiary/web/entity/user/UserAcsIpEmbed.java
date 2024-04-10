package io.nicheblog.dreamdiary.web.entity.user;

import io.nicheblog.dreamdiary.web.mapstruct.user.UserAcsIpMapstruct;
import io.nicheblog.dreamdiary.web.model.user.UserAcsIpCmpstn;
import io.nicheblog.dreamdiary.web.model.user.UserAcsIpDto;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.CascadeType;
import javax.persistence.OrderBy;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserAcsIpInfo
 * <pre>
 *  사용자user에서 계정 상태 관련 정보 위임
 * </pre>
 *
 * @author nichefish
 */
@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAcsIpEmbed {

    /** 접속 IP 사용 여부 */
    @Builder.Default
    @Column(name = "use_acs_ip_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("접속 IP 사용 여부")
    private String useAcsIpYn = "N";

    /** 접속 IP 정보 */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "user_no")
    @Fetch(FetchMode.SELECT)
    @OrderBy("acsIp ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("접속 IP 정보")
    private List<UserAcsIpEntity> acsIpList;

    /* ----- */

    /** 생성자 */
    public UserAcsIpEmbed(UserAcsIpCmpstn dto) {
        this();
        this.useAcsIpYn = dto.getUseAcsIpYn();
        boolean useAcsIp = "Y".equals(this.useAcsIpYn);
        // acsIp 사용하지 않으면? 기존 acsIp 목록 초기화
        if (!useAcsIp) this.acsIpList = new ArrayList<>();
        // acsIp 사용하면? 목록 변환
        if (useAcsIp && !CollectionUtils.isEmpty(dto.getAcsIpList())) {
            this.acsIpList = dto.getAcsIpList().stream()
                    .map(acsIp -> {
                        try {
                            return UserAcsIpMapstruct.INSTANCE.toEntity(acsIp);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
        }
    }

    /**
     * 서브엔티티 List 처리를 위한 Setter (override)
     * 한 번 Entity가 생성된 이후부터는 new List를 할당하면 안 되고 계속 JPA 이력이 추적되어야 한다.
     */
    public void setAcsIpList(final List<UserAcsIpEntity> acsIpList) {
        if (CollectionUtils.isEmpty(acsIpList)) return;
        if (this.acsIpList == null) {
            this.acsIpList = acsIpList;
        } else {
            this.acsIpList.clear();
            this.acsIpList.addAll(acsIpList);
        }
    }

    /**
     * 접속가능IP 목록을 문자열로 변환하여 반환
     */
    public String getAcsIpListStr() {
        if (CollectionUtils.isEmpty(this.acsIpList)) return null;
        List<String> acsIpList = new ArrayList<>();
        for (UserAcsIpEntity ip : this.getAcsIpList()) {
            acsIpList.add(ip.getAcsIp());
        }
        return StringUtils.join(acsIpList, ", ");
    }

    /** 접속가능 IP 문자열 목록 반환*/
    public List<String> getAcsIpStrList() {
        if (CollectionUtils.isEmpty(this.acsIpList)) return null;
        return this.acsIpList.stream()
                .map(entity -> {
                    try {
                        return entity.getAcsIp();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}