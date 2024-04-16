<#--
  -- 저널 일자(jrnl_day) > 목록 툴바 Header
  -- @included_in : cl_cd_list
  -->

<form name="listForm" id="listForm" class="d-flex align-items-center flex-nowrap gap-2" method="get">
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
    <div>
        <!-- 인풋:: datepicker -->
        <@component.header_datepicker searchParam "searchStartDt" "" />
        <!-- 인풋:: datepicker -->
        <@component.header_datepicker searchParam "searchEndDt" "today" />
    </div>
    <div>
        <select name="yy" id="yy" class="form-select form-select-sm form-select-solid">
            <option value="">년도</option>
            <#list YY_CD![] as yy>
                <option value="${yy.dtlCd!}" label="${yy.dtlCdNm!}" ${fn.selected(searchParam.yy!, yy.dtlCd!)} />
            </#list>
        </select>
    </div>
    <div>
        <select name="mnth" id="mnth" class="form-select form-select-sm form-select-solid">
            <option value="">월</option>
            <#list MNTH_CD![] as mnth>
                <option value="${mnth.dtlCd!}" label="${mnth.dtlCdNm!}" ${fn.selected(searchParam.mnth!, mnth.dtlCd!)} />
            </#list>
        </select>
    </div>
    <div>
        <select name="searchType" id="searchType" class="form-select form-select-sm form-select-solid">
            <option value="clCd">분류코드</option>
            <option value="clCdNm">분류코드명</option>
            <option value="clCdDc">분류코드 설명</option>
        </select>
    </div>
    <div>
        <!-- 인풋:: searchKeyword -->
        <@component.input_header_searchKeyword searchParam />
    </div>
    <div>
        <!-- 버튼:: 목록 검색 -->
        <@component.btn_header_search "Page.search();" />
    </div>
    <div>
        <!-- 버튼:: 등록 모달 호출하기 -->
        <@component.btn_header_reg_modal "JrnlDay.regModal();" />
    </div>
</form>
<!--end::Header-->