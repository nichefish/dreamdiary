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

    DEFAULT("DEFAULT", "기본"),
    // 공지사항
    NOTICE("NOTICE", "공지사항"),
    // 저널
    JRNL_DAY("JRNL_DAY", "저널 일자"),
    JRNL_DREAM("JRNL_DREAM", "저널 꿈"),
    JRNL_DIARY("JRNL_DREAM", "저널 일기"),
    JRNL_SUMRY("JRNL_SUMRY", "저널 결산"),
    // 일정
    VCATN_PAPR("VCATN_PAPR", "휴가계획서"),
    SCHDUL("SCHDUL", "일정"),
    // 경비
    EXPTR_PRSNL_PAPR("EXPTR_PRSNL_PAPR", "경비지출서"),
    EXPTR_REQST("EXPTR_REQST", "물품구매/경조사비 신청"),

    //
    COMMENT("COMMENT", "댓글"),
    FLSYS_META("FLSYS_META", "파일시스템 메타");

    public final String key;
    public final String desc;
}