package io.nicheblog.dreamdiary.web.spec.tag;

import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import io.nicheblog.dreamdiary.web.entity.tag.TagEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * BoardTagSpec
 * <pre>
 *  게시판 태그 목록 검색인자 세팅 Specification
 *  ※게시판 태그(board_tag) = 게시판별 태그 정보. 게시판 게시물 태그(board_post_tag)와 1:N으로 연관된다.
 * </pre>
 *
 * @author nichefish
 * @implements BaseSpec:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Component("tagSpec")
@Log4j2
public class TagSpec
        implements BaseSpec<TagEntity> {

    /**
     * 조회 후처리:: 정렬 순서 변경
     */
    @Override
    public void postQuery(
            Root<TagEntity> root,
            CriteriaQuery<?> query,
            CriteriaBuilder builder
    ) {
        query.distinct(true);
    }

    /**
     * 인자별로 구체적인 검색 조건 세팅
     */
    @Override
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<TagEntity> root,
            final CriteriaBuilder builder
    ) {

        List<Predicate> predicate = new ArrayList<>();

        // 파라미터 비교
        for (String key : searchParamMap.keySet()) {
            switch (key) {
                // case "boardCd":
                //     Join<BoardTagEntity, BoardPostTagEntity> postTag = root.join("postTagList", JoinType.LEFT);      //  JOIN 타입 명시하기
                //     predicate.add(builder.equal(postTag.get(key), searchParamMap.get(key)));
                //     continue;
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
