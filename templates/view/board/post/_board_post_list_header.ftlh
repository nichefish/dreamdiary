<#--
  -- 게시판 > 목록 검색 영역 Header
  -- @included_in : board_post_list
  -->
<form name="listForm" id="listForm" method="get">
    <input type="hidden" name="boardCd" id="boardCd" value="${boardCd!}"/>      <!-- 게시판 검색에선 꼭 있어야 한다. -->
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
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
            <#if POST_CTGR_CD??>
                <div class="position-relative me-md-2">
                    <!-- 인풋:: ctgrCd -->
                    <@component.input_header_ctgrCd searchParam POST_CTGR_CD!false />
                </div>
            </#if>
            <div class="position-relative me-md-2">
                <select name="searchType" id="searchType" class="form-select form-select-solid fw-bolder form-select-sm py-3">
                    <option value="title" label="제목" ${fn.selected(searchParam.searchType!, "제목")} />
                    <option value="nickNm" label="작성자" ${fn.selected(searchParam.searchType!, "작성자")} />
                </select>
            </div>
            <div class="position-relative me-md-2">
                <!-- 인풋:: searchKeyword -->
                <@component.input_header_searchKeyword "Page" />
            </div>
            <!-- 버튼:: 목록 검색 -->
            <@component.btn_header_search "Page" />
            <!-- 내 글 보기 버튼 -->
            <@component.btn_header_mypapr "Page" MY_PAPR!false />
        </div>
        <div class="col-lg-2 col-12 d-flex justify-content-end align-items-center mt-2">
            <a href="javascript:void(0);" class="btn btn-sm btn-primary mx-1" onclick="Page.regForm();"
               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.reg"/>">
                <i class="fas fa-plus"></i>등록
            </a>
            <#if boardCd == "cmpyLife">
                <button type="button" class="btn btn-sm btn-light btn-active-secondary mx-1" onclick="$('#cmpy_life_modal').modal('show');"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="슬기로운 회사생활 안내 팝업을 호출합니다.">
                    <span class="blink"><i class="bi bi-lightbulb-fill text-noti"></i></span>슬기로운 회사생활
                </button>
            </#if>
        </div>
    </div>

    <!-- #include:: component:: 태그 클릭 필터링 -->
    <#include "/view/_component/tag/_tag_list_header.ftlh">
</form>
