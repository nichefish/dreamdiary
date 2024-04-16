package io.nicheblog.dreamdiary.global;

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
public enum ContentType {

    DEFAULT("default", "기본"),
    NOTICE("notice", "공지사항"),
    COMMENT("comment", "댓글"),
    JRNL_DAY("jrnl_day", "저널 일자"),
    JRNL_DREAM("jrnl_dream", "저널 꿈"),

    VCATN_PAPR("vcatn_papr", "휴가계획서"),
    SCHDUL("schdul", "일정"),

    EXPTR_PRSNL_PAPR("exptr_prsnl_papr", "경비지출서"),
    EXPTR_REQST("exptr_reqst", "물품구매/경조사비 신청"),

    FLSYS_META("flsys_meta", "파일시스템 메타");

    public final String key;
    public final String desc;
}