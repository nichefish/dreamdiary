<#--
  -- 분류 코드(CL_CD) > 목록 검색 영역 Header
  -- @included_in : cl_cd_list
  -->
<form name="listForm" id="listForm" method="get">
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
    <div class="row mb-8 p-0">
        <div class="col-lg-10 col-12 align-items-center d-flex my-2">
            <div class="position-relative me-md-3">
                <!-- 인풋:: datepicker -->
                <@component.header_datepicker searchParam "searchStartDt" "" />
                <!-- 인풋:: datepicker -->
                <@component.header_datepicker searchParam "searchEndDt" "today" />
            </div>
            <div class="position-relative me-md-2 searchType">
                <select name="yy" id="yy" class="form-select form-select-solid fw-bolder form-select-sm py-3">
                    <option value="">년도</option>
                    <#list YY_CD![] as yy>
                        <option value="${yy.dtlCd!}" label="${yy.dtlCdNm!}" ${fn.selected(searchParam.yy!, yy.dtlCd!)} />
                    </#list>
                </select>
            </div>
            <div class="position-relative me-md-2 searchType">
                <select name="mnth" id="mnth" class="form-select form-select-solid fw-bolder form-select-sm py-3">
                    <option value="">월</option>
                    <#list MNTH_CD![] as mnth>
                        <option value="${mnth.dtlCd!}" label="${mnth.dtlCdNm!}" ${fn.selected(searchParam.mnth!, mnth.dtlCd!)} />
                    </#list>
                </select>
            </div>
            <div class="position-relative me-md-2">
                <select name="searchType" id="searchType" class="form-select form-select-solid fw-bolder form-select-sm py-3">
                    <option value="clCd">분류코드</option>
                    <option value="clCdNm">분류코드명</option>
                    <option value="clCdDc">분류코드 설명</option>
                </select>
            </div>
            <div class="position-relative me-md-2">
                <!-- 인풋:: searchKeyword -->
                <@component.input_header_searchKeyword "Page" />
            </div>
            <div class="col-lg-4 col-4">
                <!-- 버튼:: 목록 검색 -->
                <@component.btn_header_search "Page" />
            </div>
        </div>
        <div class="col-lg-2 col-12 d-flex justify-content-end align-items-center buttons">
            <button type="button" class="btn btn-sm btn-primary mx-3"
               onclick="DreamDay.regModal();"
               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="등록 팝업을 호출합니다.">
                <i class="fas fa-plus"></i>등록
            </button>
        </div>
    </div>
</form>
<!--end::Header-->