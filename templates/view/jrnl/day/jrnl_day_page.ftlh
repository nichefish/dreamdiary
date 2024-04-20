<#--
 * 저널 일자(jrnl_day) : 페이지
 * @included_in : jrnl_day_page.ftlh
 -->

<#import "/layout/layout_with_aside.ftlh" as layout >

<#--toolbar::버튼 추가-->
<#assign toolbar>
    <#include "/view/jrnl/day/_jrnl_day_page_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<script type="text/javascript" src="/metronic/assets/plugins/custom/tinymce/tinymce.bundle.js"></script>
<script type="text/javascript" src="/js/commons/commons-tinymce.js?20230908"></script>
<link rel="stylesheet" type="text/css" href="/css/dream.css?${releaseDate!}" />

<#--include::script modules-->
<#include "/view/jrnl/day/_jrnl_day_scripts.ftlh">
<#include "/view/jrnl/dream/_jrnl_dream_scripts.ftlh">
<#include "/view/jrnl/diary/_jrnl_diary_scripts.ftlh">

<script>
    const Page = (function() {
        return {
            init: function () {
                // 쿠키 설정하기
                let yyCookie = $.cookie("jrnl_yy");
                let mnthCookie = $.cookie("jrnl_mnth");
                if (yyCookie !== undefined) $("#jrnl_aside #yy").val(yyCookie);
                if (yyCookie !== undefined) $("#jrnl_aside #mnth").val(mnthCookie);
                // 목록 조회
                JrnlDay.listAjax();
                $('[data-bs-toggle="tooltip"]').tooltip();      // template에 있는 tooltip들 활성화
            },
        }
    })();
    $(document).ready(function() {
        Page.init();
    })
</script>

<!--begin::Card-->
<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <#--begin::Table body-->
        <div id="jrnl_day_list_div">
            <!-- handlebars template goes in here -->
        </div>
    </div>
</div>
<!--end::Card-->

<form name="procForm" id="procForm">
    <input type="hidden" name="jrnlDayNo" id="jrnlDayNo" />
</form>

<#--include:: Aside-->
<#include "/view/jrnl/day/_jrnl_day_aside_base.ftlh">

<#--include::modals-->
<#include "/view/jrnl/day/_jrnl_day_list_template.hbs">
<#include "/view/jrnl/day/_jrnl_day_reg_modal.ftlh">
<#include "/view/jrnl/dream/_jrnl_dream_reg_modal.ftlh">
<#include "/view/jrnl/diary/_jrnl_diary_reg_modal.ftlh">

</@layout.layout>