package io.nicheblog.dreamdiary.web.spec.board;

import io.nicheblog.dreamdiary.global.intrfc.spec.embed.BaseStateSpec;
import io.nicheblog.dreamdiary.web.entity.board.BoardDefEntity;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * BoardDefSpec
 * <pre>
 *  게시판 정의 정보 목록 검색인자 세팅 Specification
 *  ※게시판 정의(board_def) = 게시판 분류. 게시판 게시물(board_post)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @implements BaseSpec:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Component("boardDefSpec")
public class BoardDefSpec
        implements BaseStateSpec <BoardDefEntity> {

    /**
     * 조회 후처리:: 정렬 순서 변경
     */
    @Override
    public void postQuery(
            Root<BoardDefEntity> root,
            CriteriaQuery<?> query,
            CriteriaBuilder builder
    ) {
        List<Order> orders = new ArrayList<>();
        orders.add(builder.asc(root.get("state").get("sortOrdr")));
        query.orderBy(orders);
    }
}
