<#--
  -- 활동 로그 목록 > 검색 영역 Header
  -- @included_in : log_sys_list_header
  -->
<form name="listForm" id="listForm" class="d-flex align-items-center flex-nowrap gap-2" method="get">
    <div class="d-flex align-items-center">
        <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
        <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
        <!-- datePicker -->
        <div class="position-relative">
            <span class="menu-icon me-md-2 vertical-middle">
                <label for="searchStartDt"><i class="bi bi-calendar3 fs-2 cursor-pointer"></i></label>
            </span>
            <input type="text" class="form-control form-control-solid w-150px fs-7 me-md-2 fw-bolder d-inline" name="searchStartDt" id="searchStartDt" placeholder="Pick date" readonly/>
            <span class="me-md-2">~</span>
            <span class="menu-icon me-md-2 vertical-middle">
                <label for="searchEndDt"><i class="bi bi-calendar3 fs-2 cursor-pointer"></i></label>
            </span>
            <input type="text" class="form-control form-control-solid w-150px fs-7 me-md-2 fw-bolder d-inline" name="searchEndDt" id="searchEndDt" placeholder="Pick date" readonly/>
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
                <option value="userId">아이디</option>
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

