<!--
 -- 레이아웃 > 기본 레이아웃
 -- @included_in : aside/_aside_menu_portal
 -- @included_in : aside/_aside_menu_admin
 -->

<!--
 * 버튼 : 수정권한에 따른 수정 버튼
 -->
<#macro btn_mdfable post isMngr>
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

<!--
 * 버튼 : 글 작성 전 미리보기
 -->
<#macro btn_preview closerNm>
    <button type="button" class="btn btn-primary mx-2 float-start"
            onclick="${closerNm}.preview();"
            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
            title="작성한 내용에 대하여&#10;미리보기 팝업을 호출합니다.">
        <i class="bi bi-pencil-square"></i>
        <span class="indicator-label">미리보기</span>
        <span class="indicator-progress">Please wait...
            <span class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
    </button>
</#macro>

<!--
 * 버튼 : 글 저장 / 취소
 -->
<#macro btn_post closerNm>
    <span>
        <button type="button" class="btn btn-primary mx-2"
                onclick="${closerNm}.submit();"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.save"/>">
            <i class="bi bi-pencil-square"></i>
            <span class="indicator-label">저장</span>
            <span class="indicator-progress">Please wait...
            <span class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
        </button>
        <a href="javascript:void(0);" class="btn btn-light"
           onclick="Page.list();"
           data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.list"/>">
            <span class="indicator-label">취소</span>
        </a>
    </span>
</#macro>