<#--
  -- 활동 로그 목록 > 검색 영역 Header
  -- @included in : log_actvty_list_header
  -->
<form name="listForm" id="listForm" method="get">
    <div class="d-flex align-items-center">
        <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
        <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
        <!-- datePicker -->
        <div class="position-relative">
            <!-- datepicker -->
            <div class="position-relative me-md-3">
                <@component.datepicker searchParam "searchStartDt" "" />
                <@component.datepicker searchParam "searchEndDt" DateUtils.Parser.eDateParseStr(DateUtils.getCurrDate()) />
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
                <option value="nickNm" label="이름" ${fn.selected(searchParam.searchType!, "nickNm")} />
                <option value="logUserId" label="아이디" ${fn.selected(searchParam.searchType!, "logUserId")} />
            </select>
        </div>
        <div class="position-relative w-md-200px me-md-2">
            <!--begin::Svg Icon | path: icons/duotune/general/gen021.svg-->
            <span class="svg-icon svg-icon-3 svg-icon-gray-500 position-absolute top-50 translate-middle ms-6">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                    <rect opacity="0.5" x="17.0365" y="15.1223" width="8.15546" height="2" rx="1" transform="rotate(45 17.0365 15.1223)" fill="black" />
                    <path d="M11 19C6.55556 19 3 15.4444 3 11C3 6.55556 6.55556 3 11 3C15.4444 3 19 6.55556 19 11C19 15.4444 15.4444 19 11 19ZM11 5C7.53333 5 5 7.53333 5 11C5 14.4667 7.53333 17 11 17C14.4667 17 17 14.4667 17 11C17 7.53333 14.4667 5 11 5Z" fill="black" />
                </svg>
            </span>
            <!--end::Svg Icon-->
            <input type="text" class="form-control form-control-solid fs-7 ps-10" name="searchKeyword" id="searchKeyword"
                   placeholder="검색" maxlength="16" value="${searchParam.searchKeyword!}" />
        </div>
        <div class="card-title me-md-2">
            <label class="form-label fw-bolder text-dark"> </label>
        </div>
        <div class="d-flex align-items-center me-md-2">
            <button type="submit" class="btn btn-sm btn-primary me-5" onclick="Page.search();">
                <i class="bi bi-search"></i>검색
            </button>
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
        <!--begin::Toolbar-->
<#--        <div class="d-flex justify-content-end" data-kt-user-table-toolbar="base">
            <button type="button" class="btn btn-sm btn-light-primary" style="margin-left:auto;" onclick="Page.xlsxDownload();"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="데이터를 엑셀 파일로&#10;다운로드합니다.">
                <i class="fas fa-download fs-7"></i>다운로드
            </button>
        </div>-->
    </div>
    <!--end::Card toolbar-->
</form>

