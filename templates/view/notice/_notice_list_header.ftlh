<#--
 * 공지사항(notice) > 목록 툴바 Header
 * @included_in : notice_list.ftlh
 -->

<form name="listForm" id="listForm" class="d-flex align-items-center flex-nowrap gap-2" method="get">
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!}">
    <input type="hidden" name="tags" id="tags">
    <!-- search-param starts -->
    <!-- datepicker -->
    <div>
        <!-- 인풋:: datepicker -->
        <@component.header_datepicker searchParam "searchStartDt" "" />
        <!-- 인풋:: datepicker -->
        <@component.header_datepicker searchParam "searchEndDt" "today" />
    </div>
    <div>
        <!-- 인풋:: ctgrCd -->
        <@component.input_header_ctgrCd searchParam NOTICE_CTGR_CD![] />
    </div>
    <div>
        <select name="searchType" id="searchType" class="form-select form-select-sm form-select-solid">
            <option value="title" label="제목" ${fn.selected(searchParam.searchType!, "title")} />
            <option value="nickNm" label="작성자" ${fn.selected(searchParam.searchType!, "nickNm")} />
        </select>
    </div>
    <div>
        <!-- 인풋:: searchKeyword -->
        <@component.input_header_searchKeyword searchParam />
    </div>
    <!-- button starts -->
    <div>
        <!-- 버튼:: 목록 검색 -->
        <@component.btn_header_search "Notice.search();" />
        <!-- 버튼:: 내 글 보기 -->
        <@component.btn_header_mypapr "Notice.myPapr();" MY_PAPR!false />
    </div>
    <div>
        <!-- 버튼:: 등록 화면 이동하기 -->
        <@component.btn_header_reg_form "Notice.regForm();" />
    </div>
    <div>
        <!-- 버튼:: 엑셀 다운로드하기 -->
        <@component.btn_header_xlsx_download "Notice.xlsxDownload();" />
    </div>
</form>
