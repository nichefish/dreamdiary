package io.nicheblog.dreamdiary.web.repository.dream;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.dream.DreamDayEntity;
import io.nicheblog.dreamdiary.web.entity.dream.DreamPieceEntity;
import org.springframework.stereotype.Repository;

/**
 * DreamPieceRepository
 * <pre>
 *  꿈일자 Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("dreamPieceRepository")
public interface DreamPieceRepository
        extends BaseRepository<DreamPieceEntity, Integer> {

    //
}