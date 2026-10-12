package io.nicheblog.dreamdiary.feature.attachable.prefix.repository.jpa;

import io.nicheblog.dreamdiary.feature.attachable.prefix.entity.PrefixContentEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 말머리-컨텐츠 연결 저장소.
 * <p>
 * 콘텐츠당 0..1 연결이므로 {@code (ref_id, ref_content_type)}로 단건 조회한다.
 * (cascade하지 않고 수동 관리 — meta_content와 동일.)
 * </p>
 *
 * @author nichefish
 */
@Repository
public interface PrefixContentRepository
        extends BaseStreamRepository<PrefixContentEntity, Integer> {

    /**
     * attachable 키로 콘텐츠의 말머리 연결을 단건 조회한다.
     *
     * @param refId 참조 글 번호
     * @param refContentType 참조 컨텐츠 타입
     * @return 말머리 연결(0..1)
     */
    Optional<PrefixContentEntity> findByRefIdAndRefContentType(Integer refId, String refContentType);

    /**
     * 같은 컨텐츠 타입의 여러 콘텐츠 말머리 연결을 한 번에 조회한다.
     * 별도 content_type 컬럼이 없어 PrefixEmbed 조인을 쓸 수 없는 도메인(예: Project) 목록 부착용.
     *
     * @param refContentType 참조 컨텐츠 타입
     * @param refIds 참조 글 번호 목록
     * @return 말머리 연결 목록 (콘텐츠당 0..1)
     */
    List<PrefixContentEntity> findAllByRefContentTypeAndRefIdIn(String refContentType, Collection<Integer> refIds);
}
