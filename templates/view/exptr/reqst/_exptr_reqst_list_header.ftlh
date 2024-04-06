<#--
  -- 경비 > 물품구매/경조사비 신청 > 목록 검색 영역 Header
  -- @included_in : exptr_reqst_list
  -->
<form name="listForm" id="listForm" method="get">
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
    <div class="row mb-8 p-0">
        <div class="col-lg-4 col-12 d-flex align-items-center mt-2">
            <#if EXPTR_REQST_CTGR_CD??>
                <div class="position-relative me-md-2 col-3 searchType">
                    <!-- 인풋:: ctgrCd -->
                    <@component.input_header_ctgrCd searchParam EXPTR_REQST_CTGR_CD!false />
                </div>
            </#if>
            <div class="position-relative me-md-2 col-3 searchType">
                <select name="searchType" id="searchType" class="form-select form-select-solid fw-bolder form-select-sm py-3">
                    <option value="title" label="제목" ${fn.selected(searchParam.searchType!, "title")} />
                    <option value="nickNm" label="작성자" ${fn.selected(searchParam.searchType!, "nickNm")} />
                </select>
            </div>
            <div class="position-relative me-md-2 col-lg-6 col-5 searchType">
                <!-- 인풋:: searchKeyword -->
                <@component.input_header_searchKeyword "Page" />
            </div>
        </div>
        <div class="col-lg-4 col-12 d-flex align-items-center mt-2">
            <!-- 버튼:: 목록 검색 -->
            <@component.btn_header_search "Page" />
            <!-- 내 글 보기 버튼 -->
            <@component.btn_header_mypapr "Page" MY_PAPR!false />
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
            <#include "/view/exptr/reqst/_exptr_reqst_exmpl_modal.ftlh">
            <button type="button" class="btn btn-sm btn-light btn-active-secondary mx-1" onclick="$('#exptr_reqst_rule_modal').modal('show');"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="경조사비 기준 팝업을&#10;호출합니다.">
                <i class="fas fa-info-circle"></i>경조사비 기준
            </button>
            <#include "/view/exptr/reqst/_exptr_reqst_rule_modal.ftlh">
        </div>
    </div>

    <!-- #include:: component:: 태그 클릭 필터링 -->
    <#include "/view/_component/tag/_tag_list_header.ftlh">
</form>
<!--end::Header-->