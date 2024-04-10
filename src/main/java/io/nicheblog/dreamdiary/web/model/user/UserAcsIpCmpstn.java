package io.nicheblog.dreamdiary.web.model.user;

import io.nicheblog.dreamdiary.web.entity.user.UserAcsIpEmbed;
import io.nicheblog.dreamdiary.web.mapstruct.user.UserAcsIpMapstruct;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

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
public class UserAcsIpCmpstn {

    /** 접속IP 사용 여부 체크 */
    @Builder.Default
    private Boolean useAcsIp = false;
    /** 접속 IP 사용 여부 */
    @Builder.Default
    private String useAcsIpYn = "N";
    /** 접속 IP 정보 */
    private String acsIpListStr;
    /** 접속 IP 정보 */
    private List<UserAcsIpDto> acsIpList;

    /* ----- */

    /** 생성자 */
    public UserAcsIpCmpstn(final UserAcsIpEmbed embed) {
        this();
        this.useAcsIpYn = embed.getUseAcsIpYn();
        if (!CollectionUtils.isEmpty(embed.getAcsIpList())) {
            this.useAcsIp = "Y".equals(embed.getUseAcsIpYn());
            this.acsIpList = embed.getAcsIpList().stream()
                    .map(acsIp -> {
                        try {
                            return UserAcsIpMapstruct.INSTANCE.toDto(acsIp);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
            this.acsIpListStr = embed.getAcsIpListStr();
        }
    }

    /**
     * 사용자 관리 > 화면에서 넘어온 접속IP tagify 문자열 파싱
     * Tagify (ex.) = [{"value":"123.123.123.123"},{"value":"234.234.234.234"}] 문자열 형식으로 넘어온댜.
     * 문자열을 JSON Array로 변환하여 직접 DTO에 세팅한다.
     */
    public void parseTagifyStr() {
        JSONArray jArray = new JSONArray(this.acsIpListStr);
        List<UserAcsIpDto> acsIpList = new ArrayList<>();
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject json = jArray.getJSONObject(i);
            UserAcsIpDto acsIp = new UserAcsIpDto();
            acsIp.setAcsIp(json.getString("value"));
            acsIpList.add(acsIp);
        }
        this.acsIpList = acsIpList;
    }
}