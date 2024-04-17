package io.nicheblog.dreamdiary.web.repository.jrnl.diary;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryEntity;
import org.springframework.stereotype.Repository;

/**
 * JrnlDiaryRepository
 * <pre>
 *  저널 일기 Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("jrnlDiaryRepository")
public interface JrnlDiaryRepository
        extends BaseStreamRepository<JrnlDiaryEntity, Integer> {

    //
}