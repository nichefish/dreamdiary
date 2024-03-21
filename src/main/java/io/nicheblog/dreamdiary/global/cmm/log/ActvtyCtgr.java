package io.nicheblog.dreamdiary.global.cmm.log;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ActvtyCtgr
 * <pre>
 *  공통으로 사용하는 코드성 데이터 정의
 * </pre>
 * TODO: enum으로 점진적 변환쓰
 * TODO: Freemarker 단에서 enum을 어떻게 처리할 것인지?
 *
 * @author nichefish
 */
@AllArgsConstructor
@Getter
public enum ActvtyCtgr {

    ADMIN("관리"),
    CACHE("캐시"),

    API_JANDI("API_잔디"),
    API_KASI("API_특일"),

    LGN("로그인");

    private final String desc;
}
