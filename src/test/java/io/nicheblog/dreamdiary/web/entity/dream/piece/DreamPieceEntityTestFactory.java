package io.nicheblog.dreamdiary.web.entity.dream.piece;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.web.entity.dream.DreamPieceEntity;
import lombok.experimental.UtilityClass;

/**
 * DreamPieceEntityTestFactory
 * <pre>
 *  꿈 조각 테스트 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
public class DreamPieceEntityTestFactory {

    /**
     * 꿈 일자 Entity 생성
     */
    public static DreamPieceEntity createDreamPiece() throws Exception {
        return DreamPieceEntity.builder()
                .postNo(0)
                .contentType(ContentType.NOTICE.key)
                .title("test_title")
                .cn("test_cn")
                .ctgrCd("test_ctgr_cd")
                .build();
    }
}
