<#--
  -- 공지사항 > 목록 Header
  -- @included_in : notice_list
  -->
<form name="listForm" id="listForm" method="get">
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
    <div class="row mb-8 p-0">
        <div class="col-lg-6 col-12 align-items-center d-flex my-2">
            <div class="position-relative me-md-2 col-lg-2 col-4 searchType">
                <select name="ctgrCd" id="ctgrCd" class="form-select form-select-solid fw-bolder form-select-sm py-3">
                    <option value="">글머리</option>
                    <#list NOTICE_CTGR_CD![] as ctgr>
                        <option value="${ctgr.dtlCd!}" id="${ctgr.dtlCd!}">[${ctgr.dtlCdNm!}]</option>
                    </#list>
                </select>
            </div>
            <div class="position-relative me-md-2 col-lg-2 col-4 searchType">
                <select name="searchType" id="searchType" class="form-select form-select-solid fw-bolder form-select-sm py-3">
                    <option value="title">제목</option>
                    <option value="nickNm">작성자</option>
                </select>
            </div>
            <div class="position-relative me-md-2 col-lg-4 col-4 searchType">
                <!--begin::Svg Icon | path: icons/duotune/general/gen021.svg-->
                <span class="svg-icon svg-icon-3 svg-icon-gray-500 position-absolute top-50 translate-middle ms-6">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <rect opacity="0.5" x="17.0365" y="15.1223" width="8.15546" height="2" rx="1" transform="rotate(45 17.0365 15.1223)" fill="black"/>
                        <path d="M11 19C6.55556 19 3 15.4444 3 11C3 6.55556 6.55556 3 11 3C15.4444 3 19 6.55556 19 11C19 15.4444 15.4444 19 11 19ZM11 5C7.53333 5 5 7.53333 5 11C5 14.4667 7.53333 17 11 17C14.4667 17 17 14.4667 17 11C17 7.53333 14.4667 5 11 5Z" fill="black"></path>
                    </svg>
                </span>
                <!--end::Svg Icon-->
                <input type="text" class="form-control form-control-solid ps-10 form-control-sm py-3" name="searchKeyword" id="searchKeyword" placeholder="검색.." maxlength="30">
                <!--end::Input group-->
            </div>
            <div class="col-lg-4 col-4">
                <!-- 버튼:: 목록 검색 -->
                <@component.btn_list_search "Page" />
                <!-- 내 글 보기 버튼 -->
                <@component.btn_list_mypapr "Page" MY_PAPR!false />
            </div>
        </div>
        <div class="col-lg-6 col-12 d-flex justify-content-end align-items-center buttons">
            <a href="javascript:void(0);" class="btn btn-primary mx-3 btn-sm" onclick="Page.regForm();"
               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="등록 팝업을 호출합니다.">
                <i class="fas fa-plus"></i>등록
            </a>
        </div>
    </div>
</form>
