package io.nicheblog.dreamdiary.global.intrfc.model;

import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDto;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import lombok.*;

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
    protected String postSj;
    /**
     * 내용
     */
    protected String postCn;
    /**
     * 조회수
     */
    protected Integer postHit = 0;
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
    protected char delYn = 'N';
    /**
     * 성공여부
     */
    protected Boolean isSuccess = false;


    /* ----- */

    /**
     * 복합키 객체 반환
     */
    public BaseClsfKey getPostKey() {
        return new BaseClsfKey(this.getPostNo(), this.getBoardCd());
    }

}
