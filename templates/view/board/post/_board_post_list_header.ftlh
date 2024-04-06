<#--
  -- 게시판 > 목록 툴바 Header
  -- @included_in : board_post_list
  -->
<form name="listForm" id="listForm" class="d-flex align-items-center flex-nowrap" method="get">
    <input type="hidden" name="boardCd" id="boardCd" value="${boardCd!}"/>      <!-- 게시판 검색에선 꼭 있어야 한다. -->
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
    <input type="hidden" name="tags" id="tags">
    <!-- search-param starts -->
    <!-- datepicker -->
    <div class="me-md-3">
        <!-- 인풋:: datepicker -->
        <@component.header_datepicker searchParam "searchStartDt" "" />
        <!-- 인풋:: datepicker -->
        <@component.header_datepicker searchParam "searchEndDt" "today" />
    </div>
    <#if POST_CTGR_CD??>
        <div class="me-md-2">
            <!-- 인풋:: ctgrCd -->
            <@component.input_header_ctgrCd searchParam POST_CTGR_CD!false />
        </div>
    </#if>
    <div class="me-md-2">
        <select name="searchType" id="searchType" class="form-select form-select-solid fw-bolder form-select-sm py-3">
            <option value="title" label="제목" ${fn.selected(searchParam.searchType!, "제목")} />
            <option value="nickNm" label="작성자" ${fn.selected(searchParam.searchType!, "작성자")} />
        </select>
    </div>
    <div class="me-md-2">
        <!-- 인풋:: searchKeyword -->
        <@component.input_header_searchKeyword "Page" />
    </div>
    <!-- 버튼:: 목록 검색 -->
    <@component.btn_header_search "Page" />
    <!-- 버튼:: 내 글 보기 -->
    <@component.btn_header_mypapr "Page" MY_PAPR!false />
    <!-- 버튼:: 등록하기 -->
    <button type="button" class="btn btn-sm btn-primary" onclick="Page.regForm();"
       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.reg"/>">
        <i class="fas fa-plus"></i>등록
    </button>
</form>
