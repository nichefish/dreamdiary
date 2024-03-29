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

    DEFAULT("default", "기본"),
    NOTICE("notice", "공지사항"),
    COMMENT("comment", "댓글"),
    DREAM_DAY("dream_day", "꿈 일자"),
    DREAM_PIECE("dream_piece", "꿈 조각"),

    VCATN_PAPR("vcatn_papr", "휴가계획서"),

    EXPTR_PRSNL_PAPR("exptr_prsnl_papr", "경비지출서"),
    EXPTR_REQST("exptr_reqst", "물품구매/경조사비 신청");

    public final String key;
    public final String desc;
}