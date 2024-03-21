package io.nicheblog.dreamdiary.global.cmm.auth.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

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

    /**
     * 처리자 ID
     */
    private String userId;
    /**
     * 처리자 이름
     */
    private String userNm;
    /**
     * 권한코드
     */
    private String authCd;
    /**
     * 권한명
     */
    private String authNm;
    /**
     * 프로필 이미지 URL
     */
    private String proflImgUrl;

    /* ----- */

    public String getProflImgUrl() {
        if (StringUtils.isEmpty(this.proflImgUrl)) {
            return ("/metronic/assets/media/avatar_blank.png");
        }
        return this.proflImgUrl;
    }
}