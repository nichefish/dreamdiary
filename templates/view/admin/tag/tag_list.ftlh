<#--
 * 태그 관리 > 목록 화면
 -->
<#import "/layout/layout_with_aside.ftlh" as layout >

<#--toolbar::버튼 추가-->
<#assign toolbar>
    <#include "/view/admin/tag/_tag_list_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<script type="text/javascript" src="/metronic/assets/plugins/custom/draggable/draggable.bundle.js?${releaseDate!}"></script>

<#--include::script modules-->
<#include "/view/admin/tag/_tag_admin_scripts.ftlh">

<script>
    const Page = (function() {
        return {
            /**
             * Page 객체 초기화
             */
            init: function() {
                // 태그 조회
                const refContentType = "${refContentType!}";
                if (refContentType !== "") TagAdmin.tagListAjax('${refContentType!}');
            },
        }
    })();
    document.addEventListener("DOMContentLoaded", function() {
        Page.init();
    });
</script>
<style>
    input:placeholder-shown {
        font-style: italic;
    }
</style>


<#--include:: Aside-->
<#include "/view/admin/tag/_tag_aside_base.ftlh">

<!-- 컨텐츠 타입 헤더 -->
<div class="card post">
    <div class="d-flex align-items-center">
        <div class="fs-5 fw-bold me-5">
            태그 컨텐츠 :
        </div>
        <div id="jrnl_tag_ctgr_div">
            <#list contentTypeList![] as contentType>
                <#assign isChecked = refContentType! == contentType.key! />
                <div class="btn btn-sm btn-outlined <#if isChecked>btn-light-primary<#else>btn-light-secondary</#if> text-dark align-middler"
                     onclick="commons.util.reloadWithParam('refContentType', '${contentType.key!}');">
                <span class="d-flex align-items-center">
                    <#if contentType.icon??>
                        <i class="bi bi-${contentType.icon!} text-dark"></i>
                    </#if>
                    <span class="ms-2">
                        ${contentType.desc!}
                    </span>
                    <#if isChecked>
                        <i class="bi bi-check text-dark"></i>
                    </#if>
                </span>
                </div>
            </#list>
        </div>
    </div>
    <#if !refContentType?? || refContentType == "">
        <div class="ms-20 mt-5 text-noti">
            ※ 컨텐츠 타입을 선택해 주세요.
        </div>
    </#if>
</div>

<!-- 태그 카테고리 헤더 -->
<#include "/view/admin/tag/_tag_ctgr_header.ftlh">

<!-- 태그 목록 -->
<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <div id="tag_list_div">
            <!--Handlebars Template goes in here-->
        </div>
    </div>
    <#--begin::Card footer-->
    <div class="card-footer">
        <#--pagination::begin-->
        <#include "/view/_component/_pagination.ftlh">
        <#--pagination::end-->
    </div>
</div>

<form name="procForm" id="procForm" method="get">
    <input type="hidden" id="clCd" name="clCd" />
</form>

<#--include::modals-->
<#include "/view/admin/tag/_tag_list_template.hbs">
<#include "/view/admin/tag/_tag_ctgr_template.hbs">
<#include "/view/admin/tag/_tag_admin_dtl_modal.ftlh">
<#include "/view/admin/tag/property/_tag_property_reg_modal.ftlh">

</@layout.layout>