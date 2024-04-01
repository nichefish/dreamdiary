<!--
 -- 레이아웃 > 기본 레이아웃
 -- @included in : aside/_aside_menu_portal
 -- @included in : aside/_aside_menu_admin
 -->
<#macro managtBtn post isMngr>
    <#if post.managt??>
        <!-- 수정권한에 따라 수정 권한 부여 -->
        <#if (post.mdfable!"") == (Constant.MDFABLE_REGSTR)>
            <#if post.isRegstr>
                <button type="button" class="btn btn-light btn-active-primary me-2 btn-sm"
                        onclick="Page.mdfForm();"
                        data-bs-toggle="tooltip" data-bs-placement="top" title="<@spring.message "bs.tooltip.page.mdf"/>">
                    <i class="bi bi-pencil-square"></i>(내 글)수정
                </button>
            </#if>
        <#elseif (post.mdfable!"") == (Constant.MDFABLE_MNGR)>
            <button type="button" class="btn btn-light btn-active-primary me-2 btn-sm"
                    onclick="Page.mdfForm();"
                    data-bs-toggle="tooltip" data-bs-placement="top" title="<@spring.message "bs.tooltip.page.mdf"/>">
                <i class="bi bi-pencil-square"></i>(관리자+)수정
            </button>
        <#elseif (post.mdfable!"") == (Constant.MDFABLE_USER)>
            <button type="button" class="btn btn-light btn-active-primary me-2 btn-sm"
                    onclick="Page.mdfForm();"
                    data-bs-toggle="tooltip" data-bs-placement="top" title="<@spring.message "bs.tooltip.page.mdf"/>">
                <i class="bi bi-pencil-square"></i>(사용자+)수정
            </button>
        <#elseif (post.mdfable!"") == (Constant.MDFABLE_ALL)>
            <button type="button" class="btn btn-light btn-active-primary me-2 btn-sm"
                    onclick="Page.mdfForm();"
                    data-bs-toggle="tooltip" data-bs-placement="top" title="<@spring.message "bs.tooltip.page.mdf"/>">
                <i class="bi bi-pencil-square"></i>(전체+)수정
            </button>
        </#if>
    </#if>
    <!-- 등록자 또는 관리자가 삭제 가능 -->
    <#if (post.isRegstr)!false || isMngr>
        <button type="button" class="btn btn-light btn-active-danger me-2 btn-sm"
                onclick="Page.delAjax();"
                data-bs-toggle="tooltip" data-bs-placement="top" title="<@spring.message "bs.tooltip.del"/>">
            <i class="bi bi-trash"></i>삭제
        </button>
    </#if>
    <button type="button" class="btn btn-light btn-sm" onclick="Page.list();"
            data-bs-toggle="tooltip" data-bs-placement="top" title="<@spring.message "bs.tooltip.page.list"/>">
        <i class="bi bi-list"></i>목록
    </button>
</#macro>
