<!--
 -- 수정 권한mdfable 관련 컴포넌트
 -- @included in : lib/macros.ftl
 -->

<#macro mdfable post >
    <#if ((post.mdfable!"") == (Constant.MDFABLE_MNGR)) && isMngr>
        <i class="bi bi-person-lines-fill text-info ms-1 opacity-75"
           data-bs-toggle="tooltip" data-bs-placement="top" title="관리자 수정 가능"></i>
    <#elseif ((post.mdfable!"") == (Constant.MDFABLE_USER)) || ((post.mdfable!"") == (Constant.MDFABLE_ALL))>
        <i class="bi bi-people-fill ms-1 blink-slow"
           data-bs-toggle="tooltip" data-bs-placement="top" title="모든 사용자가 수정 가능"></i>
    </#if>
</#macro>