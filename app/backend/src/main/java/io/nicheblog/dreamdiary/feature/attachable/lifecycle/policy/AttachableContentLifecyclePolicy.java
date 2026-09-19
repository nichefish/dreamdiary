package io.nicheblog.dreamdiary.feature.attachable.lifecycle.policy;

import io.nicheblog.dreamdiary.feature.attachable._shared.type.ContentType;
import io.nicheblog.dreamdiary.feature.attachable.lifecycle.LifecycleKey;

import java.util.*;

/**
 * 라이프사이클 사용 가능 컨텐츠 타입을 제한하는 정책 클래스.
 *
 * <p>라이프사이클 모듈은 부착 가능 컨텐츠 공통 축이지만 모든 {@link ContentType}에 바로 열지는 않는다.
 * 지원 범위를 명시해 두어 의도하지 않은 타입에서 라이프사이클이 켜지는 일을 막는다.</p>
 */
public final class AttachableContentLifecyclePolicy {

    private static final EnumMap<ContentType, EnumSet<LifecycleKey>> ALLOWED = new EnumMap<>(ContentType.class);

    static {
        final EnumSet<LifecycleKey> journalLifecycle = EnumSet.of(
                LifecycleKey.OPEN,
                LifecycleKey.PENDING,
                LifecycleKey.RESOLVED
        );

        ALLOWED.put(ContentType.JOURNAL_DIARY, EnumSet.copyOf(journalLifecycle));
        ALLOWED.put(ContentType.JOURNAL_NOTE, EnumSet.copyOf(journalLifecycle));
        ALLOWED.put(ContentType.JOURNAL_DREAM, EnumSet.copyOf(journalLifecycle));
        ALLOWED.put(ContentType.JOURNAL_REFLECTION, EnumSet.copyOf(journalLifecycle));
        ALLOWED.put(ContentType.JOURNAL_THREAD, EnumSet.copyOf(journalLifecycle));

        /*
         * Keep 항목(journal_todo)은 저널과 종결 어휘가 다르다. 저널 기록은 시간이 지나도
         * 사라지지 않지만 Keep 항목은 시도했다 지고(FAILED), 기회가 만료되고(EXPIRED),
         * 의미를 잃어 내려놓기도 한다(DROPPED).
         * 넷을 RESOLVED 하나로 뭉치면 「해냈다」와 「못 한 채 끝났다」가 구분되지 않는다.
         */
        ALLOWED.put(ContentType.JOURNAL_TODO, EnumSet.of(
                LifecycleKey.OPEN,
                LifecycleKey.PENDING,
                LifecycleKey.RESOLVED,
                LifecycleKey.FAILED,
                LifecycleKey.EXPIRED,
                LifecycleKey.DROPPED
        ));
    }

    private AttachableContentLifecyclePolicy() {
    }

    /**
     * 컨텐츠 타입과 라이프사이클 키 조합이 허용되는지 확인한다.
     *
     * @param contentType 부착 가능 컨텐츠 타입
     * @param lifecycleKey 설정하려는 라이프사이클 키
     * @return 명시적으로 등록된 조합이면 {@code true}
     */
    public static boolean isAllowed(final ContentType contentType, final LifecycleKey lifecycleKey) {
        if (contentType == null || lifecycleKey == null) return false;
        final EnumSet<LifecycleKey> keys = ALLOWED.get(contentType);
        return keys != null && keys.contains(lifecycleKey);
    }

    /**
     * 컨텐츠 타입에 노출할 라이프사이클 선택지를 반환한다.
     *
     * @param contentType 부착 가능 컨텐츠 타입
     * @return 허용된 라이프사이클 키 불변 set
     */
    public static Set<LifecycleKey> allowedKeys(final ContentType contentType) {
        if (contentType == null) return Set.of();
        final EnumSet<LifecycleKey> keys = ALLOWED.get(contentType);
        return keys == null ? Set.of() : Collections.unmodifiableSet(EnumSet.copyOf(keys));
    }

    /**
     * 라이프사이클 지원이 켜진 컨텐츠 타입 목록을 반환한다.
     *
     * @return 등록된 컨텐츠 타입 불변 set
     */
    public static Set<ContentType> registeredContentTypes() {
        return Collections.unmodifiableSet(ALLOWED.keySet());
    }
}
