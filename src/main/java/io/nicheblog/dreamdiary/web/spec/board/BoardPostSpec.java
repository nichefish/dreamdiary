/*
package io.nicheblog.dreamdiary.web.spec.board;

import io.nicheblog.dreamdiary.global.cmm.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.board.BoardPostEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

*/
/**
 * BoardPostSpec
 * <pre>
 *  게시판 게시물 목록 검색인자 세팅 Specification
 * </pre>
 *
 * @author nichefish
 * @implements BaseSpec:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 *//*

@Component("boardPostSpec")
@Log4j2
public class BoardPostSpec
        implements BaseSpec<BoardPostEntity> {

    */
/**
     * 인자별로 구체적인 검색 조건 세팅
     *//*

    @Override
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<BoardPostEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        List<Predicate> predicate = new ArrayList<>();
        // expressions
        Expression<Date> regDtExp = root.get("regDt");

        // 파라미터 비교
        for (String key : searchParamMap.keySet()) {
            switch (key) {
                case "searchStartDt":
                    // 기간 검색
                    predicate.add(builder.greaterThanOrEqualTo(regDtExp, DateUtils.asDate(searchParamMap.get(key))));
                    continue;
                case "searchEndDt":
                    // 기간 검색
                    predicate.add(builder.lessThanOrEqualTo(regDtExp, DateUtils.asDate(searchParamMap.get(key))));
                    continue;
                case "postSj":
                    // 제목 = LIKE 검색
                    predicate.add(builder.like(root.get(key), "%" + searchParamMap.get(key) + "%"));
                    continue;
                case "nickNm":
                    // 작성자 이름 = 조인 후 LIKE 검색
                    Join<BoardPostEntity, AuditorInfo> regstr = root.join("regstrInfo", JoinType.LEFT);      //  JOIN 타입 명시하기
                    Expression<String> nickNmExp = regstr.get(key);
                    predicate.add(builder.like(nickNmExp, "%" + searchParamMap.get(key) + "%"));
                    continue;
                // case "tag":
                //     // 태그 검색
                //     Join<NoticeEntity, BoardPostTagEntity> boardTag = root.join("tagList", JoinType.INNER);
                //     Expression<String> boardTagExp = boardTag.get("boardTag");
                //     predicate.add(builder.equal(boardTagExp, searchParamMap.get(key)));
                default:
                    // default :: 조건 파라미터에 대해 equal 검색
                    try {
                        predicate.add(builder.equal(root.get(key), searchParamMap.get(key)));
                    } catch (Exception e) {
                        log.info("unable to locate attribute " + key + " while trying root.get(key).");
                    }
            }
        }
        return predicate;
    }
}
*/
