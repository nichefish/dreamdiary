package io.nicheblog.dreamdiary.cmm.log;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * LogTy
 * <pre>
 *  공통으로 사용하는 코드성 데이터 정의
 * </pre>
 * TODO: enum으로 점진적 변환쓰
 * TODO: Freemarker 단에서 enum을 어떻게 처리할 것인지?
 *
 * @author nichefish
 */
/* 로그 유형 코드 */
@AllArgsConstructor
@Getter
public enum LogTy {
    ACTVTY("활동 로그"),
    SYS("시스템 로그");

    private final String desc;
}