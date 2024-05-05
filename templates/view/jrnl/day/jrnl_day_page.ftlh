<#--
 * 저널 일자(jrnl_day) : 페이지
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
                // 아무 쿠키도 없을경우 전체 데이터 로딩을 막기 위해 올해 년도 세팅
                if (yyCookie === undefined && mnthCookie === undefined) $("#jrnl_aside #yy").val(commons.date.getCurrYyStr());
                // 목록 조회
                JrnlDay.listAjax();
                // Page.tagListAjax();
                // template에 있는 tooltip들 활성화
                $("[data-bs-toggle='tooltip']").tooltip();
            },
            // tagListAjax: function() {
            //     const url = "${Url.JRNL_DREAM_TAG_LIST_AJAX!}";
            //     let ajaxData = { "yy": $("#jrnl_aside #yy").val(), "mnth": $("#jrnl_aside #mnth").val() };
            //     commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
            //         if (!res.rslt) {
            //             if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
            //             return false;
            //         }
            //         commons.util.handlebarsTemplate(res.rsltList, "jrnl_dream_tag_list");
            //     });
            // }
        }
    })();
    $(document).ready(function() {
        Page.init();
    });
</script>

<#--#include:: component:: 태그 클릭 필터링-->
<#include "/view/_component/tag/_tag_list_header.ftlh">

<!--begin::Card-->
<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <#--begin::Table body-->
        <div id="jrnl_day_list_div">
            <#--handlebars template goes in here-->
        </div>
    </div>
</div>
<!--end::Card-->

<#--include:: Aside-->
<#include "/view/jrnl/day/_jrnl_day_aside_base.ftlh">

<#--include::modals-->
<#include "/view/jrnl/day/_jrnl_day_list_template.hbs">
<#include "/view/jrnl/dream/_jrnl_dream_tag_list_template.hbs">
<#include "/view/jrnl/day/_jrnl_day_reg_modal.ftlh">
<#include "/view/jrnl/dream/_jrnl_dream_reg_modal.ftlh">
<#include "/view/jrnl/diary/_jrnl_diary_reg_modal.ftlh">

</@layout.layout>