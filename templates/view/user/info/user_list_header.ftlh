<#--
  -- 사용자 관리 > 계정 및 권한 관리 목록 > 검색 영역 Header
  -- @included in : user_list
  -->
<form name="listForm" id="listForm" method="get">
    <input type="hidden" name="pageNo" id="pageNo" value="${pageNo!}">
    <input type="hidden" name="pageSize" id="pageSize" value="${pageSize!}">
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
                <!--begin::Svg Icon | path: icons/duotune/general/gen021.svg-->
                <span class="svg-icon svg-icon-3 svg-icon-gray-500 position-absolute top-50 translate-middle ms-6">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <rect opacity="0.5" x="17.0365" y="15.1223" width="8.15546" height="2" rx="1" transform="rotate(45 17.0365 15.1223)" fill="black" />
                        <path d="M11 19C6.55556 19 3 15.4444 3 11C3 6.55556 6.55556 3 11 3C15.4444 3 19 6.55556 19 11C19 15.4444 15.4444 19 11 19ZM11 5C7.53333 5 5 7.53333 5 11C5 14.4667 7.53333 17 11 17C14.4667 17 17 14.4667 17 11C17 7.53333 14.4667 5 11 5Z" fill="black" />
                    </svg>
                </span>
                <!--end::Svg Icon-->
                <input type="text" class="form-control form-control-solid ps-10 form-control-sm py-3" name="searchKeyword" id="searchKeyword" placeholder="검색.." maxlength="30" />
                <!--end::Input group-->
            </div>
            <div class="col-lg-2 col-4">
                <button type="submit" class="btn btn-sm btn-primary" onclick="Page.fnSearch();">
                    <i class="bi bi-search"></i>검색
                </button>
            </div>
        </div>
        <div class="col-lg-2 col-12 d-flex justify-content-end align-items-center">
            <a href="javascript:void(0);" class="btn btn-sm btn-primary" onclick="Page.fnRegForm();"
               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.reg"/>">
                <i class="fas fa-plus"></i>계정 등록
            </a>
        </div>
    </div>
</form>


