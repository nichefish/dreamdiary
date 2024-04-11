<#--
 -- 상세/수정 페이지 관련 컴포넌트
 -- @included_in : lib/components.ftl
 -->

<!--
 * 체크박스
 -->
<#macro checkbox param checkboxNm ynLabel ynColor  >
    <script>
        $(document).ready(function() {
            commons.util.chckboxLabel("${checkboxNm}", "${ynLabel}", "${ynColor}");
            if ("${param[checkboxNm]!}" == "Y") $("#${checkboxNm}").click();
        });
    </script>
    <div class="form-check form-switch form-check-custom form-check-solid">
        <input class="form-check-input ms-3" type="checkbox" value="Y" name="${checkboxNm}" id="${checkboxNm}" />
        <label class="form-check-label fw-bold ms-3" for="${checkboxNm}" id="${checkboxNm}Label" style="color:<#if param[checkboxNm]! == "Y">blue<#else>gray</#if>;">
            <#if param[checkboxNm]! != "Y">고정<#else>해제</#if>
        </label>
    </div>
</#macro>