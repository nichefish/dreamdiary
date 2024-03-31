package io.nicheblog.dreamdiary.global.cmm.file.repository;

import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileDtlEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * AtchFileDtlRepository
 * <pre>
 *  첨부파일 상세 Repository 인터페이스
 *  ※첨부파일 상세(atch_file_dtl) = 실제 첨부파일 정보를 담고 있는 객체. 첨부파일(atch_file)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 */
@Repository("atchFileDtlRepository")
public interface AtchFileDtlRepository
        extends BaseRepository<AtchFileDtlEntity, Integer> {

    //
}
