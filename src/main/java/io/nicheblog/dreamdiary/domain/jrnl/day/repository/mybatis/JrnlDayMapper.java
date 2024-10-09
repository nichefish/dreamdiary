package io.nicheblog.dreamdiary.domain.jrnl.day.repository.mybatis;

import io.nicheblog.dreamdiary.domain.jrnl.day.model.JrnlDayDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * JrnlDayMapper
 * <pre>
 *  저널 일자 MyBatis 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Mapper
public interface JrnlDayMapper {

    /**
     * 삭제된 데이터 정보 조회
     * @param postNo - 조회할 게시글 번호 (삭제된 데이터)
     * @return JrnlDayDto - 삭제된 데이터에 대한 JrnlDayDto 객체
     */
    JrnlDayDto getDeletedByPostNo(final @Param("postNo") Integer postNo);
}
