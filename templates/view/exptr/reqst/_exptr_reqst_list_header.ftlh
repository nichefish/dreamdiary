<#--
 * 물품구매/경조사비 신청(exptr_reqst) > 목록 툴바 Header
 * @included_in : exptr_reqst_list
 -->

<form name="listForm" id="listForm" class="d-flex align-items-center flex-nowrap gap-2" method="get">
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
    <div>
        <!-- 인풋:: ctgrCd -->
        <@component.input_header_ctgrCd searchParam EXPTR_REQST_CTGR_CD![] />
    </div>
    <div>
        <select name="searchType" id="searchType" class="form-select form-select-solid fw-bolder form-select-sm py-3">
            <option value="title" label="제목" ${fn.selected(searchParam.searchType!, "title")} />
            <option value="nickNm" label="작성자" ${fn.selected(searchParam.searchType!, "nickNm")} />
        </select>
    </div>
    <div>
        <!-- 인풋:: searchKeyword -->
        <@component.input_header_searchKeyword searchParam />
    </div>
    <div>
        <!-- 버튼:: 목록 검색 -->
        <@component.btn_header_search "ExptrReqst.search();" />
    </div>
    <div>
        <!-- 버튼:: 내 글 보기 -->
        <@component.btn_header_mypapr "ExptrReqst.myPapr();" MY_PAPR!false />
    </div>
    <div>
        <!-- 버튼:: 등록 화면 이동하기 -->
        <@component.btn_header_reg_form "ExptrReqst.regForm();" />
    </div>
    <div>
        <!-- 버튼:: 엑셀 다운로드하기 -->
        <@component.btn_header_xlsx_download "ExptrReqst.xlsxDownload();" />
    </div>
</form>
