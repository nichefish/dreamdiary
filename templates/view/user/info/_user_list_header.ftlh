<#--
  -- 사용자 관리 > 계정 및 권한 관리 목록 > 검색 영역 Header
  -- @included_in : user_list
  -->
<form name="listForm" id="listForm" method="get">
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
    <div class="row mb-8 p-0">
        <div class="col-lg-4 col-12 align-items-center d-flex">
            <div class="position-relative col-3 px-1 searchType margins">
                <select name="authCd" id="authCd" class="form-select form-select-solid fw-bolder form-select-sm py-3" data-hide-search="true">
                    <option value="">전체 권한</option>
                    <#list AUTH_CD![] as auth>
                        <option value="${auth.dtlCd!}" id="${auth.dtlCd!}">${auth.dtlCdNm!}</option>
                    </#list>
                </select>
            </div>
            <div class="position-relative col-2 authCd mx-1 margins">
                <!--begin::Select-->
                <select name="jobTitleCd" id="jobTitleCd" class="form-select form-select-solid fw-bolder form-select-sm py-3" data-hide-search="true">
                    <option value="">직급</option>
                    <#list JOB_TITLE_CD![] as jobTitle>
                        <option value="${jobTitle.dtlCd!}" id="${jobTitle.dtlCd!}">${jobTitle.dtlCdNm!}</option>
                    </#list>
                </select>
            </div>
            <div class="position-relative col-2 mx-1 margins">
                <!--begin::Select-->
                <select name="teamCd" id="teamCd" class="form-select form-select-solid fw-bolder form-select-sm py-3" data-hide-search="true">
                    <option value="">팀</option>
                    <#list TEAM_CD![] as team>
                        <option value="${team.dtlCd!}" id="${team.dtlCd!}">${team.dtlCdNm!}</option>
                    </#list>
                </select>
            </div>
            <div class="position-relative col-3 mx-1 margins">
                <!--begin::Select-->
                <select name="cmpyCd" id="cmpyCd" class="form-select form-select-solid fw-bolder form-select-sm py-3" data-hide-search="true">
                    <option value="">회사</option>
                    <#list CMPY_CD![] as cmpy>
                        <option value="${cmpy.dtlCd!}" id="${cmpy.dtlCd!}">${cmpy.dtlCdNm!}</option>
                    </#list>
                </select>
            </div>
        </div>
        <div class="col-lg-6 col-12 d-flex align-items-center">
            <div class="position-relative col-lg-2 col-4 mx-1 searchType">
                <select name="searchType" id="searchType" class="form-select form-select-solid fw-bolder form-select-sm py-3">
                    <option value="nickNm">이름</option>
                    <option value="userId">아이디</option>
                </select>
            </div>
            <div class="position-relative me-md-2 col-lg-3 col-4 mx-1 searchType">
                <!-- 인풋:: searchKeyword -->
                <@component.input_header_searchKeyword "Page" />
            </div>
            <div class="col-lg-2 col-4">
                <!-- 버튼:: 목록 검색 -->
                <@component.btn_header_search "Page" />
            </div>
        </div>
        <div class="col-lg-2 col-12 d-flex justify-content-end align-items-center">
            <a href="javascript:void(0);" class="btn btn-sm btn-primary" onclick="Page.regForm();"
               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.reg"/>">
                <i class="fas fa-plus"></i>계정 등록
            </a>
        </div>
    </div>
</form>


