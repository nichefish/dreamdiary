package io.nicheblog.dreamdiary.web.repository.jrnl.diary.mybatis;

import io.nicheblog.dreamdiary.web.model.jrnl.diary.JrnlDiaryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * JrnlDiaryMapper
 * <pre>
 *  저널 일기 MyBatis 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Mapper
public interface JrnlDiaryMapper {

    /**
     * 삭제된 데이터 정보 조회
     */
    JrnlDiaryDto getDeletedByPostNo(@Param("postNo") Integer postNo);
}
