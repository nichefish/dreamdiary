package io.nicheblog.dreamdiary.global;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ContentType
 * <pre>
 *  ClsfEntity를 상속받은 클래스들이 사용하는 컨텐츠 타입 정보
 * </pre>
 *
 * @author nichefish
 */
@RequiredArgsConstructor
@Getter
public enum ContentType {

    NOTICE("notice", "공지사항"),
    COMMENT("comment", "댓글"),
    DREAM_DAY("dream_day", "꿈 일자"),
    DREAM_PIECE("dream_piece", "꿈 조각"),;

    public final String key;
    public final String desc;
}