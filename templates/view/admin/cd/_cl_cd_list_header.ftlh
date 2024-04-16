<#--
 -- 분류 코드(cl_cd) > 목록 툴바 Header
 -- @included_in : cl_cd_list.ftlh
 -->

<form name="listForm" id="listForm" class="d-flex align-items-center flex-nowrap gap-2" method="get">
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
    <div>
        <select name="searchType" id="searchType" class="form-select form-select-sm form-select-solid">
            <option value="clCd" ${fn.selected(searchParam.searchType!, "clCd")}>분류코드</option>
            <option value="clCdNm" ${fn.selected(searchParam.searchType!, "clCdNm")}>분류코드명</option>
            <option value="clCdDc" ${fn.selected(searchParam.searchType!, "clCdDc")}>분류코드 설명</option>
        </select>
    </div>
    <div>
        <!-- 인풋:: searchKeyword -->
        <@component.input_header_searchKeyword searchParam />
    </div>
    <div>
        <!-- 버튼:: 목록 검색 -->
        <@component.btn_header_search "ClCd.search();" />
    </div>
    <div>
        <!-- 버튼:: 등록 모달 호출하기 -->
        <@component.btn_header_reg_modal "ClCd.regModal();" />
    </div>
    <div>
        <!-- 버튼:: 정렬순서 -->
        <@component.btn_header_sort_ordr "ClCd.sortOrdr();" />
    </div>
</form>
<!--end::Header-->