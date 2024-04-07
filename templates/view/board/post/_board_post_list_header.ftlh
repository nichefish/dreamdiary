<#--
  -- 게시판 > 목록 툴바 Header
  -- @included_in : board_post_list
  -->

<form name="listForm" id="listForm" class="d-flex align-items-center flex-nowrap gap-2" method="get">
    <input type="hidden" name="boardCd" id="boardCd" value="${boardCd!}"/>      <!-- 게시판 검색에선 꼭 있어야 한다. -->
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
    <input type="hidden" name="tags" id="tags">
    <!-- search-param starts -->
    <div>
        <!-- 인풋:: datepicker -->
        <@component.header_datepicker searchParam "searchStartDt" "" />
        <!-- 인풋:: datepicker -->
        <@component.header_datepicker searchParam "searchEndDt" "today" />
    </div>
    <#if POST_CTGR_CD??>
        <div>
            <!-- 인풋:: ctgrCd -->
            <@component.input_header_ctgrCd searchParam POST_CTGR_CD![] />
        </div>
    </#if>
    <div>
        <select name="searchType" id="searchType" class="form-select form-select-sm form-select-solid">
            <option value="title" label="제목" ${fn.selected(searchParam.searchType!, "제목")} />
            <option value="nickNm" label="작성자" ${fn.selected(searchParam.searchType!, "작성자")} />
        </select>
    </div>
    <div>
    <!-- 인풋:: searchKeyword -->
        <@component.input_header_searchKeyword searchParam />
    </div>
    <!-- 버튼:: 목록 검색 -->
    <div>
        <@component.btn_header_search "Page.search();" />
    </div>
    <!-- 버튼:: 내 글 보기 -->
    <div>
        <@component.btn_header_mypapr "Page" MY_PAPR!false />
    </div>
    <!-- 버튼:: 등록하기 -->
    <div>
        <!-- 버튼:: 등록 화면 이동하기 -->
        <@component.btn_header_reg_form "Page.regForm();" />
    </div>
    <div>
        <!-- 버튼:: 엑셀 다운로드하기 -->
        <@component.btn_header_xlsx_download "Page.xlsxDownload();" />
    </div>
</form>
