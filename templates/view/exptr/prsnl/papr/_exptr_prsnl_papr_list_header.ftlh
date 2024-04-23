<#--
  -- 경비지출서(exptr_prsnl_papr) > 목록 툴바 Header
  -- @included_in : exptr_prsnl_papr_list
  -->

<form name="listForm" id="listForm" class="d-flex align-items-center flex-nowrap gap-2" method="get">
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
    <div>
        <select name="yy" id="yy" class="form-select form-select-sm form-select-solid">
            <option value="">년도</option>
            <#list YY_CD![] as yy>
                <option value="${yy.dtlCd!}" label="${yy.dtlCdNm!}" ${fn.selected(searchParam.yy!, yy.dtlCd!)} />
            </#list>
        </select>
    </div>
    <div>
        <select name="mnth" id="mnth" class="form-select form-select-sm form-select-solid">
            <option value="">월</option>
            <#list MNTH_CD![] as mnth>
                <option value="${mnth.dtlCd!}" label="${mnth.dtlCdNm!}" ${fn.selected(searchParam.mnth!, mnth.dtlCd!)} />
            </#list>
        </select>
    </div>
    <div>
        <select name="searchType" id="searchType" class="form-select form-select-sm form-select-solid">
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
        <@component.header_btn_search "ExptrPrsnlPapr.search();" />
    </div>
    <div>
        <#--헤더:: 버튼: 내 글 보기-->
        <@component.header_btn_mypapr "ExptrPrsnlPapr.myPapr();" MY_PAPR!false />
    </div>
    <div>
        <#--헤더:: 버튼: 등록 화면 이동하기-->
        <@component.header_btn_reg_form "ExptrPrsnlPapr.regForm();" />
    </div>
    <div>
        <#--헤더:: 버튼: 엑셀 다운로드하기-->
        <@component.header_btn_xlsx_download "ExptrPrsnlPapr.xlsxDownload();" />
    </div>
</form>
