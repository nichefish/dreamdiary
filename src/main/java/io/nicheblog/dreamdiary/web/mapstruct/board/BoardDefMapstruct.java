package io.nicheblog.dreamdiary.web.mapstruct.board;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.web.SiteTopMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.entity.board.BoardDefEntity;
import io.nicheblog.dreamdiary.web.model.board.BoardDefDto;
import io.nicheblog.dreamdiary.web.model.cmm.SiteAcsInfo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * BoardDefMapstruct
 * <pre>
 *  일반게시판 정의 MapStruct 기반 Mapper 인터페이스
 *  ※ 일반게시판 정의(board_def) = 일반게시판 분류. 일반게시판 게시물(board_post)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {SiteUrl.class, SiteTopMenu.class})
public interface BoardDefMapstruct
        extends BaseCrudMapstruct<BoardDefDto, BoardDefDto, BoardDefEntity> {

    BoardDefMapstruct INSTANCE = Mappers.getMapper(BoardDefMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    BoardDefDto toDto(final BoardDefEntity entity) throws Exception;


    /**
     * Entity -> Dto
     */
    @Override
    @Named("toListDto")
    BoardDefDto toListDto(final BoardDefEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    BoardDefEntity toEntity(final BoardDefDto dto) throws Exception;
    
    /** 
     * 메뉴 정보로 변환
     */
    @Mapping(target = "topMenu", expression = "java(SiteTopMenu.BOARD)")
    @Mapping(target = "topMenuNo", expression = "java(SiteTopMenu.BOARD.menuNo)")
    @Mapping(target = "topMenuNm", expression = "java(SiteTopMenu.BOARD.menuNm)")
    @Mapping(target = "topMenuLabel", expression = "java(SiteTopMenu.BOARD.label)")
    @Mapping(target = "menuNm", expression = "java(entity.getBoardNm())")
    @Mapping(target = "url", expression = "java(SiteUrl.BOARD_POST_LIST + \"?boardCd=\" + entity.getBoardCd())")
    SiteAcsInfo toMenu(final BoardDefEntity entity) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final BoardDefDto dto, final @MappingTarget BoardDefEntity entity) throws Exception;
}
