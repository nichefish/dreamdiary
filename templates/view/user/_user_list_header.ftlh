<#--
  -- 사용자 관리 > 계정 및 권한 관리 목록 > 검색 영역 Header
  -- @included_in : user_list
  -->

<form name="listForm" id="listForm" class="d-flex align-items-center flex-nowrap gap-2" method="get">
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
    <div>
        <select name="authCd" id="authCd" class="form-select form-select-sm form-select-solid">
            <option value="">전체 권한</option>
            <#list AUTH_CD![] as auth>
                <option value="${auth.dtlCd!}" id="${auth.dtlCd!}">${auth.dtlCdNm!}</option>
            </#list>
        </select>
    </div>
    <div>
        <!--begin::Select-->
        <select name="rankCd" id="rankCd" class="form-select form-select-sm form-select-solid">
            <option value="">직급</option>
            <#list RANK_CD![] as rank>
                <option value="${rank.dtlCd!}" id="${rank.dtlCd!}">${rank.dtlCdNm!}</option>
            </#list>
        </select>
    </div>
    <div>
        <!--begin::Select-->
        <select name="teamCd" id="teamCd" class="form-select form-select-sm form-select-solid">
            <option value="">팀</option>
            <#list TEAM_CD![] as team>
                <option value="${team.dtlCd!}" id="${team.dtlCd!}">${team.dtlCdNm!}</option>
            </#list>
        </select>
    </div>
    <div>
        <!--begin::Select-->
        <select name="cmpyCd" id="cmpyCd" class="form-select form-select-sm form-select-solid">
            <option value="">회사</option>
            <#list CMPY_CD![] as cmpy>
                <option value="${cmpy.dtlCd!}" id="${cmpy.dtlCd!}">${cmpy.dtlCdNm!}</option>
            </#list>
        </select>
    </div>
    <div>
        <select name="searchType" id="searchType" class="form-select form-select-sm form-select-solid">
            <option value="nickNm">이름</option>
            <option value="userId">아이디</option>
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
        <!-- 버튼:: 등록 화면 이동하기 -->
        <@component.btn_header_reg_form "Page.regForm();" />
    </div>
    <div>
        <!-- 버튼:: 엑셀 다운로드하기 -->
        <@component.btn_header_xlsx_download "Page.xlsxDownload();" />
    </div>
</form>


