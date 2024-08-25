package io.nicheblog.dreamdiary.web.repository.jrnl.dream.mybatis;

import io.nicheblog.dreamdiary.web.model.jrnl.dream.JrnlDreamDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * JrnlDreamMapper
 * <pre>
 *  저널 꿈 MyBatis 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Mapper
public interface JrnlDreamMapper {

    /**
     * 삭제된 데이터 정보 조회
     */
    JrnlDreamDto getDeletedByPostNo(@Param("postNo") Integer postNo);
}
