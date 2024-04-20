<#--
 * 경비 관리 > 경비지출누적집계 > 경비지출누적집계 화면
 -->

<form name="listForm" id="listForm" class="d-flex align-items-center flex-nowrap gap-2" method="get">
    <div>
        <select class="form-select form-select-solid"
                onchange="Page.statsYy();"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="조회 년도를 변경합니다.">
            <#list yyList![] as yy>
                <option value="${yy}" ${fn.selected(statsYy!, yy)}>${yy}년"</option>
            </#list>
        </select>
    </div>
    <div>
        년도 경비지출누적집계
    </div>
    <div>
        <!-- 버튼:: 엑셀 다운로드하기 -->
        <@component.btn_header_xlsx_download "Page.xlsxDownload();" />
    </div>
</form>
