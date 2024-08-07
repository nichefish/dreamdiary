package io.nicheblog.dreamdiary.web.service.board;

import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.service.BasePostService;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.web.entity.board.BoardPostEntity;
import io.nicheblog.dreamdiary.web.mapstruct.board.BoardPostMapstruct;
import io.nicheblog.dreamdiary.web.model.board.BoardPostDto;
import io.nicheblog.dreamdiary.web.repository.board.BoardPostRepository;
import io.nicheblog.dreamdiary.web.spec.board.BoardPostSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BoardPostService
 * <pre>
 *  일반게시판 게시물 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BasePostService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("boardPostService")
@Log4j2
public class BoardPostService
        implements BasePostService<BoardPostDto.DTL, BoardPostDto.LIST, BaseClsfKey, BoardPostEntity, BoardPostRepository, BoardPostSpec, BoardPostMapstruct> {

    @Resource(name = "boardPostRepository")
    private BoardPostRepository boardPostRepository;
    @Resource(name = "boardPostSpec")
    private BoardPostSpec boardPostSpec;

    private final BoardPostMapstruct postMapstruct = BoardPostMapstruct.INSTANCE;

    @Resource(name = "cdService")
    public CdService cdService;

    @Override
    public BoardPostRepository getRepository() {
        return this.boardPostRepository;
    }

    @Override
    public BoardPostSpec getSpec() {
        return this.boardPostSpec;
    }

    @Override
    public BoardPostMapstruct getMapstruct() {
        return this.postMapstruct;
    }

    /**
     * 게시판 > 게시판 목록 Page<Entity>->Page<Dto> 변환
     */
    @Override
    public Page<BoardPostDto.LIST> pageEntityToDto(final Page<BoardPostEntity> entityPage) throws Exception {
        List<BoardPostDto.LIST> dtoList = new ArrayList<>();
        int i = 0;
        for (BoardPostEntity entity : entityPage.getContent()) {
            BoardPostDto.LIST listDto = postMapstruct.toListDto(entity);
            listDto.setRnum(CmmUtils.getPageRnum(entityPage, i));
            String ctgrNm = cdService.getDtlCdNm(listDto.getCtgrClCd(), listDto.getCtgrCd());
            listDto.setCtgrNm(ctgrNm);
            dtoList.add(listDto);
            i++;
        }
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    /**
     * 게시판 > 게시판 상단 고정 목록 조회
     */
    public List<BoardPostDto.LIST> getFxdList(final String contentType) throws Exception {
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("contentType", contentType);
            put("fxdYn", "Y");
        }};

        List<BoardPostEntity> entityList = this.getListEntity(searchParamMap);
        List<BoardPostDto.LIST> dtoList = new ArrayList<>();
        for (BoardPostEntity entity : entityList) {
            BoardPostDto.LIST listDto = postMapstruct.toListDto(entity);
            String ctgrNm = cdService.getDtlCdNm(listDto.getCtgrClCd(), listDto.getCtgrCd());
            listDto.setCtgrNm(ctgrNm);
            dtoList.add(listDto);
        }
        return dtoList;
    }

    /**
     * 게시판 > 게시판 조회 (dto level)
     */
    @Override
    public BoardPostDto.DTL getDtlDto(final BaseClsfKey key) throws Exception {
        BoardPostEntity rsEntity = this.getDtlEntity(key);       // Entity 레벨 조회
        BoardPostDto.DTL rsDto = postMapstruct.toDto(rsEntity);
        String ctgrNm = cdService.getDtlCdNm(rsDto.getCtgrClCd(), rsDto.getCtgrCd());
        rsDto.setCtgrNm(ctgrNm);
        return rsDto;
    }
}
