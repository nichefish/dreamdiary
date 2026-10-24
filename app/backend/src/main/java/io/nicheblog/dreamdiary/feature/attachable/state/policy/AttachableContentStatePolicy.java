package io.nicheblog.dreamdiary.feature.attachable.state.policy;

import io.nicheblog.dreamdiary.feature.attachable._shared.type.ContentType;
import io.nicheblog.dreamdiary.feature.attachable.state.StateKey;

import java.util.*;

/**
 * AttachableContentStatePolicy
 * <pre>
 *  컨텐츠 타입별로 토글 가능한 {@link StateKey} 집합을 코드로 고정한다.
 *  (UI·클라이언트와 동일한 허용 범위를 서버에서 강제한다.)
 * </pre>
 *
 * @author nichefish
 */
public final class AttachableContentStatePolicy {

    private static final EnumMap<ContentType, EnumSet<StateKey>> ALLOWED = new EnumMap<>(ContentType.class);

    static {
        ALLOWED.put(ContentType.JOURNAL_CHAPTER, EnumSet.of(StateKey.COLLAPSED));

        final EnumSet<StateKey> diaryDreamKeys = EnumSet.of(
                StateKey.COLLAPSED,
                StateKey.IMPRTC,
                StateKey.REFRNC
        );
        ALLOWED.put(ContentType.JOURNAL_DIARY, EnumSet.copyOf(diaryDreamKeys));
        // Reflection 은 일기와 같은 표시 선호(접기·중요·참조). 꿈 전용 NHTMR/HALLUC 는 두지 않는다.
        ALLOWED.put(ContentType.JOURNAL_REFLECTION, EnumSet.copyOf(diaryDreamKeys));

        final EnumSet<StateKey> dreamKeys = EnumSet.copyOf(diaryDreamKeys);
        dreamKeys.add(StateKey.NHTMR);
        dreamKeys.add(StateKey.HALLUC);
        ALLOWED.put(ContentType.JOURNAL_DREAM, dreamKeys);

        ALLOWED.put(
                ContentType.JOURNAL_DAY,
                EnumSet.of(StateKey.COLLAPSED, StateKey.IMPRTC)
        );

        /*
         * Keep 항목은 「내가 담아둔 것」과 「요구받아 담긴 것」을 가른다. 진행 단계가 아니라
         * 성격이라 lifecycle 이 아닌 여기에 둔다 -- 요구받은 채 보류일 수도, 완료했을 수도 있다.
         * 중요(IMPRTC)도 같은 이유로 성격 축에 둔다 -- 중요한 채 보류일 수도, 완료했을 수도 있다.
         */
        ALLOWED.put(ContentType.PROSPECTIVE_KEEP, EnumSet.of(StateKey.DEMANDED, StateKey.IMPRTC));
    }

    private AttachableContentStatePolicy() {
    }

    /**
     * 해당 컨텐츠 타입에서 주어진 상태 키를 토글할 수 있는지 여부
     */
    public static boolean isAllowed(final ContentType contentType, final StateKey stateKey) {
        if (contentType == null || stateKey == null) return false;
        final EnumSet<StateKey> keys = ALLOWED.get(contentType);
        return keys != null && keys.contains(stateKey);
    }

    /**
     * 읽기 전용 허용 집합 (메타 API 등)
     */
    public static Set<StateKey> allowedKeys(final ContentType contentType) {
        if (contentType == null) return Set.of();
        final EnumSet<StateKey> keys = ALLOWED.get(contentType);
        return keys == null ? Set.of() : Collections.unmodifiableSet(EnumSet.copyOf(keys));
    }

    /**
     * 정책에 등록된 모든 컨텐츠 타입 (테스트·문서용)
     */
    public static Set<ContentType> registeredContentTypes() {
        return Collections.unmodifiableSet(ALLOWED.keySet());
    }

    /**
     * 내부 맵 스냅샷 (테스트에서 전체 스키마 검증용)
     */
    public static Map<ContentType, Set<StateKey>> snapshot() {
        final EnumMap<ContentType, Set<StateKey>> copy = new EnumMap<>(ContentType.class);
        ALLOWED.forEach((ct, set) -> copy.put(ct, Collections.unmodifiableSet(EnumSet.copyOf(set))));
        return Collections.unmodifiableMap(copy);
    }
}
