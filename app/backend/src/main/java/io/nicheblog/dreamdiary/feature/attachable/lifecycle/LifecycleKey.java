package io.nicheblog.dreamdiary.feature.attachable.lifecycle;

import io.nicheblog.dreamdiary.global.type.LocalizedEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 부착 가능 컨텐츠의 라이프사이클 키.
 *
 * <p>{@code StateKey}와 달리 라이프사이클은 단일 현재값이다.
 * 컨텐츠 하나는 한 시점에 하나의 키로 해석된다.
 * {@code OPEN}은 저장 row가 없는 기본 상태이며, 나머지 값만 영속화한다.</p>
 *
 * <p>진행 상태({@code OPEN}, {@code PENDING})와 종결 상태({@code RESOLVED}, {@code FAILED},
 * {@code EXPIRED}, {@code DROPPED})로 나뉜다. 종결 넷은 「끝났다」는 같지만 끝난 이유가 다르다 --
 * 시도해서 됨 / 시도했는데 안 됨 / 시간이 끝냄 / 내가 접음.
 * 맞는 칸이 없으면 사용자는 전이를 하지 않고 항목이 열린 채 쌓인다.
 * 타입별로 어떤 키를 쓰는지는 {@code AttachableContentLifecyclePolicy}가 정한다.</p>
 *
 * <p>{@code EXPIRED}와 {@code DROPPED}는 기한이 지난 항목에서 함께 성립해 보인다.
 * 반사실로 가른다 -- 기한이 넉넉했다면 했을 항목만 {@code EXPIRED}, 기간을 늘려도 안 했을 항목은
 * {@code DROPPED}. 「기한이 없었다면」으로 물으면 웬만한 항목이 다 「언젠가는 했다」가 되어
 * {@code EXPIRED}가 전부를 먹으므로, 무기한이 아니라 넉넉한 기간으로 묻는다.</p>
 *
 * <p>결정을 선언했는지는 묻지 않는다. 관심이 없어 표류하다 기한이 지난 항목은 {@code DROPPED}이고,
 * 반대로 「기간이 짧아서 안 하기로 했다」는 명시적 결정은 기한이 원인이라 {@code EXPIRED}다.
 * 가르는 것은 결정의 유무가 아니라 기한이 구속 조건이었는가다.
 * 따라서 기한이 없는 항목은 {@code EXPIRED}가 될 수 없다 -- 없는 기한이 구속 조건일 수 없으므로
 * {@code DROPPED}다.</p>
 */
@Getter
@RequiredArgsConstructor
public enum LifecycleKey implements LocalizedEnum {

    OPEN("OPEN", "열림"),
    PENDING("PENDING", "보류"),
    RESOLVED("RESOLVED", "완료"),
    /** 시도했으나 이루지 못한 종결. 시도하지 않은 EXPIRED·DROPPED 와 구분한다. */
    FAILED("FAILED", "실패"),
    /** 기한이 구속 조건이 되어 못 한 종결. 기간이 넉넉했다면 했을 항목만 해당하며, 기한이 없으면 성립하지 않는다. */
    EXPIRED("EXPIRED", "만료"),
    /** 기간을 늘려도 하지 않았을 종결. 표류하다 기한이 지난 것도 여기 속한다. */
    DROPPED("DROPPED", "내려놓음");

    public final String key;
    public final String desc;

    /**
     * 종결 상태인지 여부.
     * 활성(진행) 투영은 종결 키를 모두 제외한다 -- RESOLVED 만 빼면 만료·내려놓음이 계속 남는다.
     *
     * @return 종결이면 true
     */
    public boolean isTerminal() {
        return this == RESOLVED || this == FAILED || this == EXPIRED || this == DROPPED;
    }

    /**
     * 저장된 라이프사이클 키를 열거형 값으로 변환한다.
     *
     * @param key 저장된 라이프사이클 키
     * @return 일치하는 {@link LifecycleKey}. 없으면 {@code null}
     */
    public static LifecycleKey getByKey(final String key) {
        if (key == null) return null;
        for (final LifecycleKey lifecycleKey : LifecycleKey.values()) {
            if (lifecycleKey.key.equals(key)) return lifecycleKey;
        }
        return null;
    }
}
