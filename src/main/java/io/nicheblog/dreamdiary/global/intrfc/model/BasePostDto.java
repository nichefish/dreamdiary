package io.nicheblog.dreamdiary.global.intrfc.model;

import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDto;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostKey;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;

/**
 * BasePostDto
 * <pre>
 *  (공통/상속) 게시판 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAtchDto
 */
@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BasePostDto
        extends BaseClsfDto {

    /**
     * 글 번호
     */
    protected Integer postNo;
    /**
     * 게시판분류코드
     */
    protected String boardCd;
    /**
     * 제목
     */
    protected String title;
    /**
     * 내용
     */
    protected String postCn;
    /**
     * 조회수
     */
    @Builder.Default
    protected Integer hitCnt = 0;
    /**
     * 첨부파일 ID
     */
    protected Integer atchFileNo;
    /**
     * 첨부파일 정보
     */
    protected AtchFileDto atchFileInfo;

    /**
     * 삭제여부
     */
    @Builder.Default
    protected char delYn = 'N';
    /**
     * 성공여부
     */
    @Builder.Default
    protected Boolean isSuccess = false;


    /* ----- */

    /**
     * 복합키 객체 반환
     */
    public BasePostKey getPostKey() {
        return new BasePostKey(this.getPostNo(), this.getBoardCd());
    }

}
