<#--
  -- 꿈 관리 : ASIDE (우측 사이드바)
  -- @included_in : dream_day_page
  -->
<#import "/layout/layout_with_aside.ftlh" as layout >

<@layout.layout>

<script type="text/javascript" src="/metronic/assets/plugins/custom/tinymce/tinymce.bundle.js"></script>
<script type="text/javascript" src="/js/commons/commons-tinymce.js?20230908"></script>
<link type="text/css" rel="stylesheet" href="/css/dream.css?${releaseDate!}" />

<script>
    const Page = function() {
        return {
            init: function () {
                //
            },
        }
    }();
</script>

<!--begin::Card-->
<div class="card post">
    <!--begin::Card header 검색+헤더 UI -->
    <#include "/view/dream/day/dream_day_page_header.ftlh">

    <div class="card-body p-9">
            <!--end::Table head-->
            <!--begin::Table body-->
            <div id="dream_day_list_div">
                <!-- handlebars template goes in here -->
            </div>
        </div>
        <!-- pagination::begin -->
        <#include "/view/_component/_pagination.ftlh">
        <!-- pagination::end -->
    </div>

    <form name="procForm" id="procForm">
        <input type="hidden" name="dreamDayNo" id="dreamDayNo" />
    </form>
</div>
<!--end::Card-->

<#include "/view/dream/day/dream_day_list_template.ftlh">
<#include "/view/dream/day/dream_day_reg_modal.ftlh">
<#include "/view/dream/day/dream_day_reg_template.ftlh">
<#include "/view/dream/piece/dream_piece_reg_modal.ftlh">
<#include "/view/dream/piece/dream_piece_reg_header_template.ftlh">
<#include "/view/dream/piece/dream_piece_reg_template.ftlh">

<!--include:: Dream - Aside-->
<#include "/view/dream/day/dream_day_aside_base.ftlh">

</@layout.layout>