<#--
  -- 활동 로그 목록 > 검색 영역 Header
  -- @included_in : log_actvty_list_header
  -->
<form name="listForm" id="listForm" class="d-flex align-items-center flex-nowrap gap-2" method="get">
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
    <!-- datepicker -->
    <div>
        <!-- 인풋:: datepicker -->
        <@component.header_datepicker searchParam "searchStartDt" "" />
        <!-- 인풋:: datepicker -->
        <@component.header_datepicker searchParam "searchEndDt" DateUtils.parser.eDateParseStr(DateUtils.getCurrDate()) />
    </div>
    <div>
        <!--begin::Select-->
        <select name="rslt" id="rslt" class="form-select form-select-sm form-select-solid" data-hide-search="true">
            <option value="">작업결과</option>
            <option value="true" label="성공" ${fn.selected(searchParam.rslt!, "true")} />
            <option value="false" label="실패" ${fn.selected(searchParam.rslt!, "false")} />
        </select>
    </div>
    <div>
        <select name="searchType" id="searchType" class="form-select form-select-sm form-select-solid">
            <option value="nickNm" label="이름" ${fn.selected(searchParam.searchType!, "nickNm")} />
            <option value="userId" label="아이디" ${fn.selected(searchParam.searchType!, "userId")} />
        </select>
    </div>
    <div>
        <!-- 인풋:: searchKeyword -->
        <@component.input_header_searchKeyword searchParam />
    </div>
    <div>
        <!-- 버튼:: 목록 검색 -->
        <@component.btn_header_search "Page.search();" />
    </div>
    <div>
        <!-- 버튼:: 엑셀 다운로드하기 -->
        <@component.btn_header_xlsx_download "Page.xlsxDownload();" />
    </div>
</form>

