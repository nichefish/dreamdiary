<#--
  -- 활동 로그 목록 > 검색 영역 Header
  -- @included in : log_actvty_list_header
  -->
<form name="listForm" id="listForm" method="get">
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
                <!--begin::Svg Icon | path: icons/duotune/general/gen021.svg-->
                <span class="svg-icon svg-icon-3 svg-icon-gray-500 position-absolute top-50 translate-middle ms-6">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <rect opacity="0.5" x="17.0365" y="15.1223" width="8.15546" height="2" rx="1" transform="rotate(45 17.0365 15.1223)" fill="black" />
                        <path d="M11 19C6.55556 19 3 15.4444 3 11C3 6.55556 6.55556 3 11 3C15.4444 3 19 6.55556 19 11C19 15.4444 15.4444 19 11 19ZM11 5C7.53333 5 5 7.53333 5 11C5 14.4667 7.53333 17 11 17C14.4667 17 17 14.4667 17 11C17 7.53333 14.4667 5 11 5Z" fill="black" />
                    </svg>
                </span>
                <!--end::Svg Icon-->
                <input type="text" class="form-control form-control-solid fs-7 ps-10" name="searchKeyword" id="searchKeyword" placeholder="검색" maxlength="16" />
            </div>
            <div class="card-title me-md-2">
                <label class="form-label fw-bolder text-dark"> </label>
            </div>
            <div class="d-flex align-items-center me-md-2">
                <button type="submit" class="btn btn-sm btn-primary me-5" onclick="Page.search();">
                    <i class="bi bi-search"></i>검색
                </button>
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

