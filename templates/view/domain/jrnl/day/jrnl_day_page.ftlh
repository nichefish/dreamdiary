<#--
 * 저널 일자(jrnl_day) : 페이지
 -->

<#import "/layout/layout_with_aside.ftlh" as layout >

<#--toolbar::버튼 추가-->
<#assign toolbar>
    <#include "/view/domain/jrnl/day/_jrnl_day_page_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<link rel="stylesheet" type="text/css" href="/css/jrnl.css?${releaseDate!}" />

<!--begin::Card-->
<div class="card post">
    <#--begin::Card header-->
    <div class="card-header min-h-auto mb-7">
        <#--begin::Tab-->
        <ul class="nav nav-tabs nav-tabs-line ps-5 mt-5">
            <li class="nav-item">
                <a class="nav-link px-6 active" data-bs-toggle="tab" href="#kt_tab_pane_1"
                   onclick="Page.listPage();">
                    <span class="nav-icon">
                        <i class="bi bi-people"></i>
                    </span>
                    <span class="nav-text">목록 VIEW</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link px-6" data-bs-toggle="tab" href="#kt_tab_pane_2"
                   onclick="Page.calPage();">
                    <span class="nav-icon">
                        <i class="bi bi-sort-down-alt"></i>
                    </span>
                    <span class="nav-text">달력 VIEW</span>
                </a>
            </li>
        </ul>
    </div>

    <!-- 태그 헤더 :: 파일 분리 -->
    <#include "/view/domain/jrnl/day/tag/_jrnl_day_tag_header.ftlh">

    <#--begin::Card body-->
    <div class="card-body">
        <#--begin::Table body-->
        <div id="jrnl_day_list_div">
            <#--handlebars template goes in here-->
        </div>
        <div id="jrnl_diary_list_div">
            <#--handlebars template goes in here-->
        </div>
        <div id="jrnl_dream_list_div">
            <#--handlebars template goes in here-->
        </div>
    </div>
</div>
<!--end::Card-->

<#--include::aside-->
<#include "/view/domain/jrnl/day/_aside/_jrnl_day_aside_base.ftlh">

<#--include::tinymce-->
<script type="text/javascript" src="/metronic/assets/plugins/custom/tinymce/tinymce.bundle.js"></script>
<script type="text/javascript" src="/js/common/helper/tinymce.js?${releaseDate!}"></script>
<script type="text/javascript" src="/lib/prism/js/prism.js"></script>
<#include "/view/global/_tinymce_img.ftlh">

<#--include::fullcalendar-->
<script type="text/javascript" src="/metronic/assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
<script type="text/javascript" src="/js/common/helper/fullcalendar.js?${releaseDate!}"></script>

<#--include::jrnl_day_module-->
<#include "/view/domain/jrnl/day/_modal/_jrnl_day_reg_modal.ftlh">
<#include "/view/domain/jrnl/day/_modal/_jrnl_day_dtl_modal.ftlh">
<#include "/view/domain/jrnl/day/_jrnl_day_list_template.hbs">
<#include "/view/domain/jrnl/day/_partial/_jrnl_day_mdf_btn_partial.hbs">
<script type="text/javascript" src="/js/view/domain/jrnl/day/jrnl_day_module.js?${releaseDate!}"></script>

<#--include::jrnl_diary_module-->
<#include "/view/domain/jrnl/diary/_modal/_jrnl_diary_reg_modal.ftlh">
<#include "/view/domain/jrnl/diary/_jrnl_diary_list_template.hbs">
<#include "/view/domain/jrnl/diary/_partial/_jrnl_diary_reg_btn_partial.hbs">
<#include "/view/domain/jrnl/diary/_partial/_jrnl_diary_mdf_btn_partial.hbs">
<#include "/view/domain/jrnl/diary/_partial/_jrnl_diary_cn_partial.hbs">
<script type="text/javascript" src="/js/view/domain/jrnl/diary/jrnl_diary_module.js?${releaseDate!}"></script>

<#--include::jrnl_dream_module-->
<#include "/view/domain/jrnl/dream/_modal/_jrnl_dream_reg_modal.ftlh">
<#include "/view/domain/jrnl/dream/_jrnl_dream_list_template.hbs">
<#include "/view/domain/jrnl/dream/_partial/_jrnl_dream_reg_btn_partial.hbs">
<#include "/view/domain/jrnl/dream/_partial/_jrnl_dream_mdf_btn_partial.hbs">
<#include "/view/domain/jrnl/dream/_partial/_jrnl_dream_cn_partial.hbs">
<script type="text/javascript" src="/js/view/domain/jrnl/dream/jrnl_dream_module.js?${releaseDate!}"></script>

<#--include::jrnl_todo_module-->
<#include "/view/domain/jrnl/todo/_jrnl_todo_list_template.hbs">
<script type="text/javascript" src="/js/view/domain/jrnl/todo/jrnl_todo_module.js?${releaseDate!}"></script>

<#--include::jrnl_day_tag_module-->
<#include "/view/domain/jrnl/diary/tag/_modal/_jrnl_diary_tag_dtl_modal.ftlh">
<#include "/view/domain/jrnl/diary/tag/_jrnl_diary_tag_list_template.hbs">
<script type="text/javascript" src="/js/view/domain/jrnl/diary/tag/jrnl_diary_tag_module.js?${releaseDate!}"></script>

<#--include::jrnl_day_tag_module-->
<#include "/view/domain/jrnl/day/tag/_modal/_jrnl_day_tag_dtl_modal.ftlh">
<#include "/view/domain/jrnl/day/tag/_jrnl_day_tag_list_template.hbs">
<script type="text/javascript" src="/js/view/domain/jrnl/day/tag/jrnl_day_tag_module.js?${releaseDate!}"></script>

<#--include::jrnl_dream_tag_module-->
<#include "/view/domain/jrnl/dream/tag/_modal/_jrnl_dream_tag_dtl_modal.ftlh">
<#include "/view/domain/jrnl/dream/tag/_jrnl_dream_tag_list_template.hbs">
<script type="text/javascript" src="/js/view/domain/jrnl/dream/tag/jrnl_dream_tag_module.js?${releaseDate!}"></script>

<#--include::tags-->
<#include "/view/domain/jrnl/tag/_jrnl_tag_list_modal.ftlh">
<#include "/view/extension/clsf/tag/_partial/_tag_list_partial.hbs">
<#include "/view/extension/clsf/tag/_partial/_tag_list_sized_partial.hbs">

<#--include::comment-->
<#include "/view/extension/clsf/comment/_modal/_comment_reg_modal.ftlh">
<#include "/view/extension/clsf/comment/_partial/_comment_list_partial.hbs">
<#include "/view/extension/clsf/comment/_partial/_comment_reg_btn_partial.hbs">
<script type="text/javascript" src="/js/view/extension/clsf/comment/comment_modal_module.js?${releaseDate!}"></script>

<!--include::page scripts-->
<script type="text/javascript" src="/js/view/domain/jrnl/day/jrnl_day_page.js?${releaseDate!}"></script>

</@layout.layout>