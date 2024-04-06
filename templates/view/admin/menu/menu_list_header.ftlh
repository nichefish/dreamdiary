<#--
  -- 활동 로그 목록 > 검색 영역 Header
  -- @included_in : log_actvty_list_header
  -->
<form name="listForm" id="listForm" class="d-flex align-items-center flex-nowrap gap-2" method="get">
    <div class="row mb-8 p-0">
        <div class="col-lg-9 col-12 d-flex align-items-center mt-2">
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
            <div class="position-relative w-md-100px me-md-2">
                <select name="searchType" id="searchType" class="form-select form-select-solid fs-7 fw-bolder">
                    <option value="nickNm">이름</option>
                    <option value="logUserId">아이디</option>
                </select>
            </div>
            <div class="position-relative w-md-200px me-md-2">
                <!-- 인풋:: searchKeyword -->
                <@component.input_header_searchKeyword "Page" />
            </div>
            <div class="card-title me-md-2">
                <label class="form-label fw-bolder text-dark"> </label>
            </div>
            <div class="d-flex align-items-center me-md-2">
                <!-- 버튼:: 목록 검색 -->
                <@component.btn_header_search "Page" />
            </div>
        </div>
        <!--end::Input group-->
        <div class="col-lg-3 col-6 d-flex justify-content-end align-items-center mt-2">
            <a href="javascript:void(0);" class="btn btn-sm btn-primary mx-3" onclick="Menu.regForm('main');"
               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="메뉴 등록 팝업을 호출합니다.">
                <i class="fas fa-plus"></i>등록
            </a>
        </div>
    </div>
    <!--end::Compact form-->
</form>

