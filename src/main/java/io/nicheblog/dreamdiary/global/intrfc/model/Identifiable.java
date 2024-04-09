package io.nicheblog.dreamdiary.global.intrfc.model;

/**
 * BaseAtchDto
 * <pre>
 *  (공통/상속) 첨부파일 소유 Dto (첨부파일 정보 추가)
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditDto
 */
public interface Identifiable<KeyType> {

    /**
     * 키 반환 메소드 (각 dto에서 구현)
     */
    KeyType getKey();
}
