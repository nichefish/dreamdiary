package io.nicheblog.dreamdiary.feature.attachable.prefix.model;

import io.nicheblog.dreamdiary.global.type.TextClass;
import lombok.*;

import javax.validation.constraints.*;

/**
 * 말머리 관리·표시 DTO.
 *
 * @author nichefish
 */
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PrefixDto {

    @Positive
    private Integer id;

    @NotBlank
    @Size(max = 100)
    private String name;

    /**
     * 표시 색 의미 코드.
     * 태그 프로필과 같은 {@code TEXT_CLASS_CD} 어휘를 쓴다. 미지정이면 null.
     */
    @Size(max = 20)
    private String textClassCd;

    /**
     * 렌더용 CSS 클래스 (서버 해석값, 읽기 전용).
     * 미지정·DEFAULT 면 빈 문자열이다. 클라이언트는 이 값을 그대로 class 에 붙인다.
     */
    private String textClass;

    @NotNull
    @PositiveOrZero
    private Integer sortOrder;

    @Pattern(regexp = "[YN]")
    private String activeYn;

    /**
     * 표시용 말머리 DTO 를 조립한다.
     * <pre>
     *  각 도메인(엔트리·챕터·스레드·할일·게시글)이 선택된 말머리를 표시용으로 실어 보낼 때 쓴다.
     *  의미 코드에서 CSS 클래스를 파생하는 규칙을 이 한 곳에 두어, 도메인마다 복제되지 않게 한다.
     * </pre>
     *
     * @param id 말머리 ID
     * @param name 말머리명
     * @param textClass 색 의미. null 이면 DEFAULT 로 본다.
     * @param sortOrder 정렬 순서
     * @param activeYn 활성 여부
     * @return 표시용 말머리 DTO
     */
    public static PrefixDto displayOf(
            final Integer id,
            final String name,
            final TextClass textClass,
            final Integer sortOrder,
            final String activeYn
    ) {
        final TextClass resolved = TextClass.getOrDefault(textClass);
        return PrefixDto.builder()
                .id(id)
                .name(name)
                .textClassCd(resolved.getKey())
                .textClass(resolved.toCssClass())
                .sortOrder(sortOrder)
                .activeYn(activeYn)
                .build();
    }
}
