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
<link rel="stylesheet" type="text/css" href="/css/jrnl.css?${releaseDate!}" />

<#--include::script modules-->
<#include "/view/jrnl/day/_jrnl_day_scripts.ftlh">
<#include "/view/jrnl/day/tag/_jrnl_day_tag_scripts.ftlh">
<#include "/view/jrnl/dream/_jrnl_dream_scripts.ftlh">
<#include "/view/jrnl/dream/tag/_jrnl_dream_tag_scripts.ftlh">
<#include "/view/jrnl/diary/_jrnl_diary_scripts.ftlh">
<#include "/view/jrnl/diary/tag/_jrnl_diary_tag_scripts.ftlh">
<#include "/view/jrnl/tag/_jrnl_tag_scripts.ftlh">
<#include "/view/_component/comment/_comment_modal_scripts.ftlh">

<script>
    const Page = (function() {
        return {
            init: function () {
                // 쿠키 설정하기
                let yyCookie = $.cookie("jrnl_yy");
                if (yyCookie !== undefined) $("#jrnl_aside #yy").val(yyCookie);
                let mnthCookie = $.cookie("jrnl_mnth");
                if (yyCookie !== undefined) $("#jrnl_aside #mnth").val(mnthCookie);
                let sortCookie = $.cookie("jrnl_day_sort");
                if (sortCookie !== undefined) $("#jrnl_aside #sort").val(sortCookie);
                // 아무 쿠키도 없을경우 전체 데이터 로딩을 막기 위해 올해 년도 세팅
                if (yyCookie === undefined && mnthCookie === undefined) $("#jrnl_aside #yy").val(commons.date.getCurrYyStr());
                JrnlDayAside.init();
                // 목록 조회
                JrnlDay.yyMnthListAjax();
                // 태그 조회
                JrnlDayTag.listAjax();
                JrnlDiaryTag.listAjax();
                JrnlDreamTag.listAjax();
                // template에 있는 tooltip들 활성화
                $("[data-bs-toggle='tooltip']").tooltip();
                // 꿈 키워드 검색에 엔터키 처리
                commons.util.enterKey("#dreamKeyword", JrnlDream.keywordListAjax);
            },
        }
    })();
    $(document).ready(function() {
        Page.init();
        // 모달 댓글 모듈 수동 init (refreshFunc 주입(
        Comment.modal.init({
            "refreshFunc": JrnlDay.yyMnthListAjax
        });
    });
</script>

<style>
</style>

<#--#include:: component:: 태그 클릭 필터링-->
<!-- 전체 태그 :: 모달 팝업 처리하기-->
<#--<#include "/view/_component/tag/_tag_list_header.ftlh">-->

<!-- 태그 헤더 :: 파일 분리 -->
<#include "/view/jrnl/day/tag/_jrnl_day_tag_header.ftlh">

<!--begin::Card-->
<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <#--begin::Table body-->
        <div id="jrnl_day_list_div">
            <#--handlebars template goes in here-->
        </div>
        <div id="jrnl_dream_list_div">
            <#--handlebars template goes in here-->
        </div>
    </div>
</div>
<!--end::Card-->

<#--include:: Aside-->
<#include "/view/jrnl/day/_jrnl_day_aside_base.ftlh">

<#--include::modals-->
<#include "/view/jrnl/day/_jrnl_day_list_template.hbs">
<#include "/view/jrnl/day/_jrnl_day_reg_modal.ftlh">
<#include "/view/jrnl/day/tag/_jrnl_day_tag_list_template.hbs">
<#include "/view/jrnl/day/tag/_jrnl_day_tag_dtl_modal.ftlh">

<#include "/view/jrnl/diary/_jrnl_diary_reg_modal.ftlh">
<#include "/view/jrnl/diary/tag/_jrnl_diary_tag_list_template.hbs">
<#include "/view/jrnl/diary/tag/_jrnl_diary_tag_dtl_modal.ftlh">

<#include "/view/jrnl/dream/_jrnl_dream_reg_modal.ftlh">
<#include "/view/jrnl/dream/_jrnl_dream_list_template.hbs">
<#include "/view/jrnl/dream/tag/_jrnl_dream_tag_list_template.hbs">
<#include "/view/jrnl/dream/tag/_jrnl_dream_tag_dtl_modal.ftlh">

<#include "/view/jrnl/tag/_jrnl_tag_list_modal.ftlh">
<#include "/view/_component/comment/_comment_reg_modal.ftlh">

</@layout.layout>