<#--
  -- 공지사항 > 목록 Header
  -- @included_in : notice_list
  -->
<form name="listForm" id="listForm" method="get">
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!}">
    <input type="hidden" name="tags" id="tags">
    <!-- header starts -->
    <div class="row mb-8 p-0">
        <!-- search-param starts -->
        <div class="col-lg-10 col-12 d-flex align-items-center mt-2">
            <!-- datepicker -->
            <div class="position-relative me-md-3">
                <!-- 인풋:: datepicker -->
                <@component.header_datepicker searchParam "searchStartDt" "" />
                <!-- 인풋:: datepicker -->
                <@component.header_datepicker searchParam "searchEndDt" "today" />
            </div>
            <div class="position-relative me-md-3">
                <!-- 인풋:: ctgrCd -->
                <@component.input_header_ctgrCd searchParam NOTICE_CTGR_CD!false />
            </div>
            <div class="position-relative me-md-3">
                <select name="searchType" id="searchType" class="form-select form-select-solid fw-bolder form-select-sm py-3">
                    <option value="title" label="제목" ${fn.selected(searchParam.searchType!, "title")} />
                    <option value="nickNm" label="작성자" ${fn.selected(searchParam.searchType!, "nickNm")} />
                </select>
            </div>
            <div class="position-relative me-md-3">
                <!-- 인풋:: searchKeyword -->
                <@component.input_header_searchKeyword "Page" />
            </div>
            <!-- button starts -->
            <div class="position-relative me-md-3">
                <!-- 버튼:: 목록 검색 -->
                <@component.btn_header_search "Page" />
                <!-- 내 글 보기 버튼 -->
                <@component.btn_header_mypapr "Page" MY_PAPR!false />
            </div>
        </div>
        <div class="col-lg-2 col-6 d-flex justify-content-end align-items-center mt-2">
            <a href="javascript:void(0);" class="btn btn-primary mx-3 btn-sm" onclick="Page.regForm();"
               data-bs-toggle="tooltip" data-bs-placement="top" title="<@spring.message "bs.tooltip.page.reg"/>">
                <i class="fas fa-plus"></i>등록
            </a>
        </div>
    </div>

    <hr class="my-7 text-muted" />
    <div class="mb-6 ms-4">
        <button type="button" class="btn btn-sm btn-light btn-active-secondary mx-1" onclick="$('#crdt_user_cttpc_modal').modal('show');"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="직원 연락처 팝업을 호출합니다.">
            <i class="bi bi-telephone"></i>직원 연락처
        </button>
        <#include "/view/notice/_notice_list_crdt_user_cttpc_modal.ftlh">
        <button type="button" class="btn btn-sm btn-light btn-active-secondary mx-1" onclick="$('#extn_phone_modal').modal('show');"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="내선전화 관련 안내 팝업을&#10;호출합니다.">
            <i class="bi bi-telephone"></i>내선전화 관련
        </button>
        <#include "/view/notice/_notice_list_extn_phone_modal.ftlh">
    </div>

    <!-- #include:: component:: 태그 클릭 필터링 -->
    <#include "/view/_component/tag/_tag_list_header.ftlh">
</form>
