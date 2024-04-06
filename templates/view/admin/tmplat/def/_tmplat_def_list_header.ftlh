<#--
  -- 공지사항 > 목록 툴바 Header
  -- @included_in : notice_list
  -->

<form name="listForm" id="listForm" class="d-flex align-items-center flex-nowrap gap-2" method="get">
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
        <div>
            <!-- 인풋:: ctgrCd -->
            <@component.input_header_ctgrCd searchParam TMPLAT_CTGR_CD![] />
        </div>
        <div>
            <select name="searchType" id="searchType" class="form-select form-select-solid fw-bolder form-select-sm py-3">
                <option value="title">제목</option>
                <option value="nickNm">작성자</option>
            </select>
        </div>
        <div>
            <!-- 인풋:: searchKeyword -->
            <@component.input_header_searchKeyword "Page" />
        </div>
        <div>
            <!-- 버튼:: 목록 검색 -->
            <@component.btn_header_search "Page" />
        </div>
        <div>
            <!-- 버튼:: 내 글 보기 -->
            <@component.btn_header_mypapr "Page" MY_PAPR!false />
        </div>
        <div>
            <!-- 버튼:: 등록 화면 이동하기 -->
            <@component.btn_header_reg_form "Page" />
        </div>
    </div>
</form>
