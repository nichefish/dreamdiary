package io.nicheblog.dreamdiary.web.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;

/**
 * PaginationInfo
 * <pre>
 *  (공통/상속) 페이징 정보 Dto
 * </pre>
 *
 * @author nichefish
 */
@Log4j2
@Getter
@Setter
public class PaginationInfo {

    /**
     * 총 항목 개수
     */
    Long totalCnt = 0L;
    /**
     * 페이지 크기
     */
    Integer pageSize;
    /**
     * 총 페이지 개수
     */
    Integer lastPageNo = 1;
    /**
     * 현재 페이지번호
     */
    Integer currPageNo = 1;
    /**
     * 이전 페이지번호
     */
    Integer prevPageNo;
    /**
     * 이전이전 페이지번호
     */
    Integer prevPrevPageNo;
    /**
     * 이전이전이전 페이지번호
     */
    Integer prevPrevPrevPageNo;
    /**
     * 처음과 이전이전이전 사이 공백
     */
    Boolean prevEllipsis;
    /**
     * 다음 페이지번호
     */
    Integer nextPageNo;
    /**
     * 다음다음 페이지번호
     */
    Integer nextNextPageNo;
    /**
     * 다음다음다음 페이지번호
     */
    Integer nextNextNextPageNo;
    /**
     * 끝과 다음다음 사이 공백
     */
    Boolean nextEllipsis;
    /**
     * 첫페이지 여부
     */
    Boolean isFirstPage = false;
    /**
     * 마지막 페이지 여부
     */
    Boolean isLastPage = false;

    /**
     * 생성자 :: Page<> 정보 매핑
     */
    public PaginationInfo(final Page<?> pageList) {
        if (pageList == null) return;
        this.pageSize = pageList.getSize();
        this.totalCnt = pageList.getTotalElements();
        this.currPageNo = pageList.getNumber() + 1;     // idx + 1
        this.lastPageNo = pageList.getTotalPages();
        this.isFirstPage = pageList.isFirst();
        this.isLastPage = pageList.isLast();
        if (pageList.hasPrevious() && !pageList.isFirst()) {
            this.prevPageNo = pageList.previousPageable()
                                      .getPageNumber() + 1;   // idx + 1
            if (this.prevPageNo > 1) {
                this.prevPrevPageNo = this.prevPageNo - 1;
                this.prevEllipsis = (prevPrevPageNo > 2);                        // '...'
                if (this.prevPrevPageNo > 1) {
                    this.prevPrevPrevPageNo = this.prevPrevPageNo - 1;
                    this.prevEllipsis = (prevPrevPrevPageNo > 1);                        // '...'
                }
            }
        }
        if (pageList.hasNext() && !pageList.isLast()) {
            this.nextPageNo = pageList.nextPageable()
                                      .getPageNumber() + 1;
            if (this.nextPageNo < this.lastPageNo) {
                this.nextNextPageNo = this.nextPageNo + 1;
                this.nextEllipsis = (nextNextPageNo < this.lastPageNo - 1);      // '...'
                if (this.nextNextPageNo < this.lastPageNo) {
                    this.nextNextNextPageNo = this.nextNextPageNo + 1;
                    this.nextEllipsis = (nextNextNextPageNo < this.lastPageNo);      // '...'
                }
            }
        }
        log.debug("totalCnt: {}, lastPageNo: {}, currPageNo: {}, prevPageNo: {}, nextPageNo: {}, isFirstPage: {}, isLastPage: {}", totalCnt, lastPageNo, currPageNo, prevPageNo, nextPageNo, isFirstPage, isLastPage);
    }
}
