<#--
 * 경비지출서(exptr_prsnl_papr) > 목록 툴바 Header
 * @included_in : exptr_prsnl_papr_list
 -->

<form name="listForm" id="listForm" class="d-flex align-items-center flex-nowrap gap-2" method="get">
    <div class="d-flex align-items-center">
        <div class="position-relative w-md-100px me-md-2"
             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="조회 년도를 변경합니다.">
            <select name="statsYy" id="statsYy" class="form-select form-select-solid" onchange="Page.statsYyMnth();">
                <#list yyList![] as yy>
                    <option value="${yy}" label="${yy}년" ${fn.selected(statsYy!, yy)} />
                </#list>
            </select>
        </div>
        <div class="position-relative pt-2 me-md-2 pe-2">
            <h4><label for="statsYy">년</label></h4>
        </div>
        <div class="position-relative w-md-75px me-md-2"
             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="조회 월을 변경합니다.">
            <select name="statsMnth" id="statsMnth" class="form-select form-select-solid" onchange="Page.statsYyMnth();">
                <#list ["1","2","3","4","5","6","7","8","9","10","11","12"] as mnth>
                    <option value="${mnth}" label="${mnth}월" ${fn.selected(statsMnth!, mnth)} />
                </#list>
            </select>
        </div>
        <div class="position-relative w-md-100px pt-2 me-md-2">
            <h4><label for="statsMnth">월 지출내역</label></h4>
        </div>
        <div class="card-toolbar">
            <#--헤더:: 버튼: 엑셀 다운로드하기-->
            <@component.header_btn_xlsx_download "Page.xlsxDownload();" />
        </div>
    </div>
</form>