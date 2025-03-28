package io.nicheblog.dreamdiary.extension.clsf.tag.service;

import io.nicheblog.dreamdiary.extension.clsf.ContentType;
import io.nicheblog.dreamdiary.extension.clsf.tag.entity.ContentTagEntity;
import io.nicheblog.dreamdiary.extension.clsf.tag.entity.TagEntity;
import io.nicheblog.dreamdiary.extension.clsf.tag.entity.TagSmpEntity;
import io.nicheblog.dreamdiary.extension.clsf.tag.mapstruct.ContentTagMapstruct;
import io.nicheblog.dreamdiary.extension.clsf.tag.model.ContentTagDto;
import io.nicheblog.dreamdiary.extension.clsf.tag.model.ContentTagParam;
import io.nicheblog.dreamdiary.extension.clsf.tag.model.TagDto;
import io.nicheblog.dreamdiary.extension.clsf.tag.repository.jpa.ContentTagRepository;
import io.nicheblog.dreamdiary.extension.clsf.tag.spec.ContentTagSpec;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;

import java.util.List;

/**
 * ContentTagService
 * <pre>
 *  컨텐츠-태그 서비스 인터페이스
 * </pre>
 *
 * @author nichefish
 */
public interface ContentTagService
        extends BaseCrudService<ContentTagDto, ContentTagDto, Integer, ContentTagEntity, ContentTagRepository, ContentTagSpec, ContentTagMapstruct> {

    /**
     * 특정 게시물에 대한 콘텐츠 태그 목록을 조회합니다.
     *
     * @param postNo 글 번호
     * @param contentType 컨텐츠 타입
     * @return {@link List} -- 태그 목록
     */
    List<ContentTagEntity> getListEntityByRefWithCache(final Integer postNo, final ContentType contentType) throws Exception;

    /**
     * 특정 게시물에 대한 콘텐츠 태그 목록을 조회합니다.
     *
     * @param param 파라미터
     * @return {@link List} -- 태그 목록
     */
    List<ContentTagEntity> getListEntityTag(final ContentTagParam param) throws Exception;

    /**
     * 태그 조회
     *
     * @param tagNo 태그 번호
     * @return {@link List} -- 태그 목록
     */
    TagSmpEntity getTagSmpDtlEntity(final Integer tagNo);

    /**
     * 특정 게시물에 대한 콘텐츠 태그 목록을 조회합니다.
     *
     * @param clsfKey 참조 복합키 정보 (BaseClsfKey)
     * @return {@link List} -- 태그 목록
     */
    List<TagDto> getTagStrListByClsfKey(final BaseClsfKey clsfKey) throws Exception;

    /**
     * 특정 게시물에 대해 태그 정보와 연결되지 않는 컨텐츠 태그 삭제.
     *
     * @param clsfKey 참조 복합키 정보 (BaseClsfKey)
     * @param obsoleteTagList 삭제할 태그 목록
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    void delObsoleteContentTags(final BaseClsfKey clsfKey, final List<TagDto> obsoleteTagList) throws Exception;

    /**
     * 특정 게시물에 대해 컨텐츠 태그 목록 추가.
     *
     * @param clsfKey 참조 복합키 정보 (BaseClsfKey)
     * @param rsList  처리할 태그 엔티티 목록 (List<TagEntity>)
     * @return {@link List} -- 등록된 컨텐츠 태그 엔티티 목록
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    List<ContentTagEntity> addContentTags(final BaseClsfKey clsfKey, final List<TagEntity> rsList) throws Exception;
}
