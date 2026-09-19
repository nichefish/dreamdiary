package io.nicheblog.dreamdiary.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * TextClass
 * <pre>
 *  CSS class를 직접 저장하지 않고, 시각적 의미(색 어휘)만 저장하는 enum.
 *  태그 프로필과 말머리가 이 어휘를 공유하며, 실제 Bootstrap text utility 는
 *  렌더링 시점에 {@link #toCssClass()} 가 코드값에서 조립한다.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@RequiredArgsConstructor
public enum TextClass {

    DEFAULT("DEFAULT"),
    SUCCESS("SUCCESS"),
    INFO("INFO"),
    WARNING("WARNING"),
    DANGER("DANGER"),
    PRIMARY("PRIMARY"),
    SECONDARY("SECONDARY"),
    DARK("DARK"),
    MUTED("MUTED"),
    DIALOG("DIALOG"),
    NOTI("NOTI"),
    EMOTION("EMOTION"),
    BURNT("BURNT");

    private final String key;

    public static TextClass getOrDefault(final TextClass semantic) {
        return semantic == null ? DEFAULT : semantic;
    }

    /**
     * 렌더링에 쓸 Bootstrap text utility 클래스를 반환한다.
     * <pre>
     *  코드값을 소문자로 낮춰 {@code text-<code>} 로 조립한다. 태그·말머리가 같은 색 어휘를
     *  공유하므로 해석도 이 한 곳만 쓴다. {@code DEFAULT} 는 클래스를 붙이지 않는다.
     * </pre>
     *
     * @return CSS 클래스명. DEFAULT 면 빈 문자열.
     */
    public String toCssClass() {
        if (this == DEFAULT) return "";
        return "text-" + this.key.toLowerCase(java.util.Locale.ROOT);
    }

    public static TextClass fromCode(final String code) {
        if (StringUtils.isBlank(code)) return DEFAULT;

        if (StringUtils.equalsIgnoreCase("ALERT", code)) return DANGER;

        for (final TextClass semantic : values()) {
            if (StringUtils.equalsIgnoreCase(semantic.getKey(), code)) return semantic;
            if (StringUtils.equalsIgnoreCase(semantic.name(), code)) return semantic;
        }
        return DEFAULT;
    }
}
