<#--
 * 물품구매/경조사비 신청(exptr_reqst) > 목록 툴바 Header
 * @included_in : exptr_reqst_list
 -->

<form name="listForm" id="listForm" class="d-flex align-items-center flex-nowrap gap-2" method="get">
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">

    <div>
        <#--헤더:: 인풋: 글분류 코드-->
        <@component.header_input_ctgrCd searchParam EXPTR_REQST_CTGR_CD![] />
    </div>
    <div>
        <#--헤더:: 인풋: searchType-->
        <select name="searchType" id="searchType" class="form-select form-select-solid fw-bolder form-select-sm py-3">
            <option value="title" label="제목" ${fn.selected(searchParam.searchType!, "title")} />
            <option value="nickNm" label="작성자" ${fn.selected(searchParam.searchType!, "nickNm")} />
        </select>
    </div>
    <div>
        <#--헤더:: 인풋: searchKeyword-->
        <@component.header_input_searchKeyword searchParam />
    </div>
    <div>
        <#--헤더:: 버튼: 목록 검색-->
        <@component.header_btn_search "ExptrReqst.search();" />
    </div>
    <div>
        <#--헤더:: 버튼: 내 글 보기-->
        <@component.header_btn_mypapr "ExptrReqst.myPaprList();" MY_PAPR!false />
    </div>
    <div>
        <#--헤더:: 버튼: 등록 화면 이동하기-->
        <@component.header_btn_reg_form "ExptrReqst.regForm();" />
    </div>
    <div>
        <#--헤더:: 버튼: 엑셀 다운로드하기-->
        <@component.header_btn_xlsx_download "ExptrReqst.xlsxDownload();" />
    </div>
</form>
