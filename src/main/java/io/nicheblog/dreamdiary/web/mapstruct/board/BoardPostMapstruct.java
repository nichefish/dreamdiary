package io.nicheblog.dreamdiary.web.mapstruct.board;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.board.BoardPostEntity;
import io.nicheblog.dreamdiary.web.model.board.BoardPostDto;
import io.nicheblog.dreamdiary.web.model.board.BoardPostListDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * BoardPostMapstruct
 * <pre>
 *  게시판 게시물 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface BoardPostMapstruct
        extends BaseListMapstruct<BoardPostDto, BoardPostListDto, BoardPostEntity> {

    BoardPostMapstruct INSTANCE = Mappers.getMapper(BoardPostMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "ctgrClCd", expression = "java((entity.getBoardDefInfo() != null) ? entity.getBoardDefInfo().getCtgrClCd() : null)")
    @Mapping(target = "ctgrNm", expression = "java((entity.getCtgrCdInfo() != null) ? entity.getCtgrCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "commentList", expression = "java(entity.getCommentDtoList())")
    // @Mapping(target = "viewerList", expression = "java(entity.getViewerDtoList())")
    // @Mapping(target = "managtrList", expression = "java(entity.getManagtrDtoList())")
    @Mapping(target = "regstrNm", expression = "java((entity.getRegstrInfo() != null) ? entity.getRegstrInfo().getNickNm() : null)")
    @Mapping(target = "mdfusrNm", expression = "java((entity.getMdfusrInfo() != null) ? entity.getMdfusrInfo().getNickNm() : null)")
    @Mapping(target = "regDt", expression = "java(DateUtils.asStr(entity.getRegDt(), DateUtils.PTN_DATETIME))")
    @Mapping(target = "mdfDt", expression = "java(DateUtils.asStr(entity.getMdfDt(), DateUtils.PTN_DATETIME))")
    @Mapping(target = "managtrNm", expression = "java((entity.getManagtrInfo() != null) ? entity.getManagtrInfo().getNickNm() : null)")
    @Mapping(target = "managtDt", expression = "java(DateUtils.asStr(entity.getManagtDt(), DateUtils.PTN_DATETIME))")
    BoardPostDto toDto(final BoardPostEntity entity) throws Exception;

    /**
     * Entity -> Dto
     */
    // @Mapping(target = "ctgrClCd", expression = "java((entity.getBoardDefInfo() != null) ? entity.getBoardDefInfo().getCtgrClCd() : null)")
    // @Mapping(target = "ctgrNm", expression = "java((entity.getCtgrCdInfo() != null) ? entity.getCtgrCdInfo().getDtlCdNm() : null)")
    // @Mapping(target = "commentList", expression = "java(entity.getCommentDtoList())")
    // @Mapping(target = "viewerList", expression = "java(entity.getViewerDtoList())")
    // @Mapping(target = "managtrList", expression = "java(entity.getManagtrDtoList())")
    // @Mapping(target = "regstrNm", expression = "java((entity.getRegstrInfo() != null) ? entity.getRegstrInfo().getNickNm() : null)")
    // @Mapping(target = "mdfusrNm", expression = "java((entity.getMdfusrInfo() != null) ? entity.getMdfusrInfo().getNickNm() : null)")
    // @Mapping(target = "regDt", expression = "java(DateUtils.asStr(entity.getRegDt(), DateUtils.PTN_DATETIME))")
    // @Mapping(target = "mdfDt", expression = "java(DateUtils.asStr(entity.getMdfDt(), DateUtils.PTN_DATETIME))")
    // @Mapping(target = "managtrNm", expression = "java((entity.getManagtrInfo() != null) ? entity.getManagtrInfo().getNickNm() : null)")
    // @Mapping(target = "managtDt", expression = "java(DateUtils.asStr(entity.getManagtDt(), DateUtils.PTN_DATETIME))")
    // BoardPostDto toDto(final BoardPostSmpEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Mapping(target = "ctgrClCd", expression = "java((entity.getBoardDefInfo() != null) ? entity.getBoardDefInfo().getCtgrClCd() : null)")
    @Mapping(target = "ctgrNm", expression = "java((entity.getCtgrCdInfo() != null) ? entity.getCtgrCdInfo().getDtlCdNm() : null)")
    @Mapping(target = "regstrNm", expression = "java((entity.getRegstrInfo() != null) ? entity.getRegstrInfo().getNickNm() : null)")
    @Mapping(target = "mdfusrNm", expression = "java((entity.getMdfusrInfo() != null) ? entity.getMdfusrInfo().getNickNm() : null)")
    @Mapping(target = "regDt", expression = "java(DateUtils.asStr(entity.getRegDt(), DateUtils.PTN_DATETIME))")
    @Mapping(target = "mdfDt", expression = "java(DateUtils.asStr(entity.getMdfDt(), DateUtils.PTN_DATETIME))")
    @Mapping(target = "managtrNm", expression = "java((entity.getManagtrInfo() != null) ? entity.getManagtrInfo().getNickNm() : null)")
    @Mapping(target = "managtDt", expression = "java(DateUtils.asStr(entity.getManagtDt(), DateUtils.PTN_DATETIME))")
    BoardPostListDto toListDto(final BoardPostEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    // @Mapping(target = "tagList", expression = "java(dto.getTagEntityList())")
    BoardPostEntity toEntity(final BoardPostDto dto) throws Exception;

    /**
     * Dto -> Entity
     *//*

    BoardPostSmpEntity toSmplEntity(final BoardPostDto dto) throws Exception;

    */
/**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // @Mapping(target = "tagList", expression = "java(dto.getTagEntityList())")
    void updateFromDto(
            final BoardPostDto dto,
            final @MappingTarget BoardPostEntity entity
    ) throws Exception;
}
