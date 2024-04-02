<#--
  -- 경비 > 물품구매/경조사비 신청 > 목록 검색 영역 Header
  -- @included in : exptr_reqst_list
  -->
<form name="listForm" id="listForm" method="get">
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
    <div class="row mb-8 p-0">
        <div class="col-lg-4 col-12 d-flex align-items-center mt-2">
            <#if EXPTR_REQST_CTGR_CD??>
                <div class="position-relative me-md-2 col-3 searchType">
                    <select name="ctgrCd" id="ctgrCd" class="form-select form-select-solid fw-bolder form-select-sm py-3">
                        <option value="">글머리</option>
                        <#list EXPTR_REQST_CTGR_CD![] as ctgr>
                            <option value="${ctgr.dtlCd!}" label="[${ctgr.dtlCdNm!}]" ${fn.selected(searchParam.ctgrCd!, ctgr.dtlCd!)} />
                        </#list>
                    </select>
                </div>
            </#if>
            <div class="position-relative me-md-2 col-3 searchType">
                <select name="searchType" id="searchType" class="form-select form-select-solid fw-bolder form-select-sm py-3">
                    <option value="title" label="제목" ${fn.selected(searchParam.searchType!, "title")} />
                    <option value="nickNm" label="작성자" ${fn.selected(searchParam.searchType!, "nickNm")} />
                </select>
            </div>
            <div class="position-relative me-md-2 col-lg-6 col-5 searchType">
                <!--begin::Svg Icon | path: icons/duotune/general/gen021.svg-->
                <span class="svg-icon svg-icon-3 svg-icon-gray-500 position-absolute top-50 translate-middle ms-6">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <rect opacity="0.5" x="17.0365" y="15.1223" width="8.15546" height="2" rx="1" transform="rotate(45 17.0365 15.1223)" fill="black"/>
                        <path d="M11 19C6.55556 19 3 15.4444 3 11C3 6.55556 6.55556 3 11 3C15.4444 3 19 6.55556 19 11C19 15.4444 15.4444 19 11 19ZM11 5C7.53333 5 5 7.53333 5 11C5 14.4667 7.53333 17 11 17C14.4667 17 17 14.4667 17 11C17 7.53333 14.4667 5 11 5Z" fill="black"/>
                    </svg>
                </span>
                <!--end::Svg Icon-->
                <input type="text" class="form-control form-control-solid ps-10 form-control-sm py-3"
                       name="searchKeyword" id="searchKeyword" placeholder="검색.." maxlength="30" value="${searchParam.searchKeyword!}"/>
                <!--end::Input group-->
            </div>
        </div>
        <div class="col-lg-4 col-12 d-flex align-items-center mt-2">
            <button type="submit" class="btn btn-sm btn-primary" onclick="Page.search();">
                <i class="bi bi-search"></i>검색
            </button>
            <#if MY_PAPR??>
                <div class="btn btn-sm btn-secondary btn-outlined ms-2 ps-2 pe-3 cursor-pointer" style="background-color:rgba(64,64,64,0.2) !important" onclick="Page.resetSearch();"
                     data-bs-toggle="tooltip" data-bs-placement="top" title="전체 글을 조회합니다.">
                    <i class="bi bi-list"></i>전체 글 보기
                </div>
            <#else>
                <div class="btn btn-sm btn-light-primary btn-outlined ms-2 ps-2 pe-3 cursor-pointer" onclick="Page.myPapr();"
                     data-bs-toggle="tooltip" data-bs-placement="top" title="내가 작성한 글만 조회합니다.">
                    <i class="bi bi-check"></i>내 글 보기
                </div>
            </#if>
        </div>
        <div class="col-lg-4 col-12 d-flex justify-content-end align-items-center mt-2">
            <a href="javascript:void(0);" class="btn btn-sm btn-primary mx-3" onclick="Page.regForm();"
               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.reg"/>">
                <i class="fas fa-plus"></i>등록
            </a>
            <button type="button" class="btn btn-sm btn-light btn-active-secondary mx-1" onclick="$('#exptr_reqst_exmpl_modal').modal('show');"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="작성 안내 팝업을 호출합니다.">
                <i class="fas fa-info-circle"></i>작성 안내
            </button>
            <#include "/view/exptr/reqst/exptr_reqst_exmpl_modal.ftlh">
            <button type="button" class="btn btn-sm btn-light btn-active-secondary mx-1" onclick="$('#exptr_reqst_rule_modal').modal('show');"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="경조사비 기준 팝업을&#10;호출합니다.">
                <i class="fas fa-info-circle"></i>경조사비 기준
            </button>
            <#include "/view/exptr/reqst/exptr_reqst_rule_modal.ftlh">
        </div>
    </div>

    <!-- #include:: component:: 태그 클릭 필터링 -->
    <#include "/view/_component/tag/_tag_list_header.ftlh">
</form>
<!--end::Header-->