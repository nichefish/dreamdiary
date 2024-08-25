package io.nicheblog.dreamdiary.web.repository.jrnl.day.mybatis;

import io.nicheblog.dreamdiary.web.model.jrnl.day.JrnlDayDto;
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
     */
    JrnlDayDto getDeletedByPostNo(@Param("postNo") Integer postNo);
}
