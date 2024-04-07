<#--
  -- 활동 로그 목록 > 검색 영역 Header
  -- @included_in : log_actvty_list_header
  -->

<form name="listForm" id="listForm" class="d-flex align-items-center flex-nowrap gap-2" method="get">
    <div class="d-flex align-items-center">
        <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
        <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
        <!-- datePicker -->
        <div class="position-relative">
            <!-- datepicker -->
            <div class="position-relative me-md-3">
                <!-- 인풋:: datepicker -->
                <@component.header_datepicker searchParam "searchStartDt" "" />
                <!-- 인풋:: datepicker -->
                <@component.header_datepicker searchParam "searchEndDt" DateUtils.parser.eDateParseStr(DateUtils.getCurrDate()) />
            </div>
        </div>
        <div class="position-relative w-md-1250px me-md-2">
            <!--begin::Select-->
            <select name="rslt" id="rslt" class="form-select form-select-solid fs-7 fw-bolder me-md-2 w-125px" data-hide-search="true">
                <option value="">작업결과</option>
                <option value="true" label="성공" ${fn.selected(searchParam.rslt!, "true")} />
                <option value="false" label="실패" ${fn.selected(searchParam.rslt!, "false")} />
            </select>
        </div>
        <div class="position-relative w-md-100px me-md-2">
            <select name="searchType" id="searchType" class="form-select form-select-solid fs-7 fw-bolder">
                <option value="nickNm">이름</option>
                <option value="logUserId">아이디</option>
            </select>
        </div>
        <div class="position-relative w-md-200px me-md-2">
            <!-- 인풋:: searchKeyword -->
            <@component.input_header_searchKeyword searchParam />
        </div>
        <div class="card-title me-md-2">
            <label class="form-label fw-bolder text-dark"> </label>
        </div>
        <div class="d-flex align-items-center me-md-2">
            <!-- 버튼:: 목록 검색 -->
            <@component.btn_header_search "Page.search();" />
        </div>
        <!--end::Input group-->
    </div>
    <!--end::Compact form-->
    <!--begin::Card title-->
    <div class="card-title text-start">
        <div class="d-flex align-items-center position-relative my-1">
        </div>
    </div>
    <div class="card-toolbar">
        <!-- 버튼:: 엑셀 다운로드하기 -->
        <@component.btn_header_xlsx_download "Page.xlsxDownload();" />
    </div>
    <!--end::Card toolbar-->
</form>

