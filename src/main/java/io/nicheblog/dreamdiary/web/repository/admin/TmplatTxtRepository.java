package io.nicheblog.dreamdiary.web.repository.admin;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.admin.TmplatTxtEntity;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.Optional;

/**
 * TmplatTxtRepository
 * <pre>
 *  입력내용(텍스트에디터) 템플릿 관리 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("tmplatTxtRepository")
public interface TmplatTxtRepository
        extends BaseRepository<TmplatTxtEntity, Integer> {
    //

    /**
     * 사용자 ID로 검색
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Optional<TmplatTxtEntity> findByTmplatDefCdAndCtgrCdAndDefaultYn(
            final String tmplatDefCd,
            final String ctgrCd,
            final String defaultYn
    );
}
