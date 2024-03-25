package io.nicheblog.dreamdiary.web.model.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * UserInfoItemDto
 * <pre>
 *  사용자(직원) 추가정보 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserInfoItemDto {

    /**
     * 사용자 정보 추가정보 고유 ID (PK)
     */
    private Integer userInfoItemNo;
    /**
     * 항목 이름
     */
    private String itemNm;
    /**
     * 항목 내용
     */
    private String itemCn;
    /**
     * 항목 설명
     */
    private String itemDc;
    /**
     * 정렬 순서
     */
    private Integer sortOrdr;
}
