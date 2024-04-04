package io.nicheblog.dreamdiary.global.auth.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.PostLoad;
import java.io.Serializable;

/**
 * AuditorDto
 * <pre>
 *  사용자 정보 조회용 Dto
 *  (등록자/수정자 등 조회시 다른 dto 안에 포함된 형태로 사용)
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
public class AuditorDto
        implements Serializable {

    @PostLoad
    private void onLoad() {
        if (StringUtils.isEmpty(this.proflImgUrl)) this.proflImgUrl = "/metronic/assets/media/avatar_blank.png";
    }

    /** 사용자 ID */
    private String userId;
    /** 사용자 이름 */
    private String nickNm;

    /** TODO: 권한코드 */
    private String authCd;

    /** 권한명  */
    private String authNm;

    /** 프로필 이미지 URL */
    private String proflImgUrl;
}