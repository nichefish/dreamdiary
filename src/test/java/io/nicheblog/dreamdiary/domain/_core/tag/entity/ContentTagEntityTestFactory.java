package io.nicheblog.dreamdiary.domain._core.tag.entity;

import io.nicheblog.dreamdiary.global._common._clsf.tag.entity.TagEntity;
import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

/**
 * ContentTagEntityTestFactory
 * <pre>
 *  컨텐츠-태그 Entity 생성 팩토리 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class ContentTagEntityTestFactory {

    /**
     * 테스트용 컨텐츠 태그 Entity 생성
     */
    public static TagEntity createTag() throws Exception {
        return TagEntity.builder()
                .tagNo(0)
                .tagNm("태그")
                .contentTagList(new ArrayList<>())
                .build();
    }
}
