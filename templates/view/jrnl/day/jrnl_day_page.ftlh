<#--
  -- 저널 일자(jrnl_day) : 페이지
  -- @included_in : jrnl_day_page.ftlh
  -->

<#import "/layout/layout_with_aside.ftlh" as layout >

<#-- 툴바 버튼 추가 -->
<#assign toolbar>
    <#include "/view/jrnl/day/_jrnl_day_page_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<script type="text/javascript" src="/metronic/assets/plugins/custom/tinymce/tinymce.bundle.js"></script>
<script type="text/javascript" src="/js/commons/commons-tinymce.js?20230908"></script>
<link rel="stylesheet" type="text/css" href="/css/dream.css?${releaseDate!}" />

<script>
    const Page = function() {
        return {
            init: function () {
                JrnlDay.init();
                JrnlDream.init();
            },
        }
    }();
    $(document).ready(function() {
        Page.init();
    })
</script>

<!--begin::Card-->
<div class="card post">
    <div class="card-body p-9">
            <!--end::Table head-->
            <!--begin::Table body-->
            <div id="jrnl_day_list_div">
                <!-- handlebars template goes in here -->
            </div>
        </div>
        <!-- pagination::begin -->
        <#include "/view/_component/_pagination.ftlh">
        <!-- pagination::end -->
    </div>

    <form name="procForm" id="procForm">
        <input type="hidden" name="jrnlDayNo" id="jrnlDayNo" />
    </form>
</div>
<!--end::Card-->

<#include "/view/jrnl/day/_jrnl_day_list_template.hbs">
<#include "/view/jrnl/day/_jrnl_day_reg_modal.ftlh">
<#include "/view/jrnl/dream/_jrnl_dream_reg_modal.ftlh">

<!--include:: script modules -->
<#include "/view/jrnl/day/jrnl_day_scripts.ftlh">
<#include "/view/jrnl/dream/jrnl_dream_scripts.ftlh">

<!--include:: Dream - Aside-->
<#include "/view/jrnl/day/_jrnl_day_aside_base.ftlh">

</@layout.layout>