<!--
 -- 레이아웃 > 기본 레이아웃
 -- @included in : aside/_aside_menu_portal
 -- @included in : aside/_aside_menu_admin
 -->
<#macro previewBtn closerNm>
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

<#macro postBtn closerNm>
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