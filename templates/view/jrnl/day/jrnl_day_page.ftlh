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
<#include "/view/jrnl/dream/_jrnl_dream_scripts.ftlh">
<#include "/view/jrnl/diary/_jrnl_diary_scripts.ftlh">
<#include "/view/jrnl/tag/_jrnl_tag_scripts.ftlh">
<#include "/view/_component/modal/comment/_comment_scripts.ftlh">

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
                // 목록 조회
                JrnlDay.yyMnthListAjax();
                // 태그 조회
                JrnlTag.dayTagListAjax();
                JrnlTag.diaryTagListAjax();
                JrnlTag.dreamTagListAjax();
                // template에 있는 tooltip들 활성화
                $("[data-bs-toggle='tooltip']").tooltip();
                // 꿈 키워드 검색에 엔터키 처리
                commons.util.enterKey("#dreamKeyword", JrnlDream.keywordListAjax);
            },
            tagCtgrSyncAjax: function() {
                const url = "${Url.JRNL_DREAM_TAG_CTGR_SYNC_AJAX!}";
                commons.util.blockUIAjax(url, 'post', {}, function(res) {
                    Swal.fire({ text: res.message });
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                });
            }
        }
    })();
    $(document).ready(function() {
        Page.init();
        // 모달 댓글 모듈 수동 init (refreshFunc 주입(
        Comment.init(JrnlDay.yyMnthListAjax);
    });
</script>

<style>
</style>

<#--#include:: component:: 태그 클릭 필터링-->
<!-- 전체 태그 :: 모달 팝업 처리하기-->
<#--<#include "/view/_component/tag/_tag_list_header.ftlh">-->

<#--begin::Tags-->
<hr class="my-6 text-muted" />
<div class="mb-6 ms-4">
    <div class="d-flex align-items-center mb-6 ms-4">
        <div class="col-1 ms-4 me-6 text-center fs-6">
            <b>일자 태그 : </b>
        </div>
        <div class="col" id="jrnl_day_tag_list_div">
            <#--handlebars template goes in here-->
        </div>
    </div>
    <div class="d-flex align-items-center mb-6 ms-4">
        <div class="col-1 ms-4 me-6 text-center fs-6">
            <b>일기 태그 : </b>
        </div>
        <div class="col" id="jrnl_diary_tag_list_div">
            <#--handlebars template goes in here-->
        </div>
    </div>
    <div class="d-flex align-items-center mb-6 ms-4">
        <div class="col-1 ms-4 me-6 text-center fs-6">
            <b>꿈 태그 : </b>
        </div>
        <div class="col" id="jrnl_dream_tag_list_div">
            <#--handlebars template goes in here-->
        </div>
        <div class="col-1 ms-4 pe-0 border-2 border-gray-300 border-end h-75 w-10px">&nbsp;</div>
        <div class="col-1 ms-4 text-center fs-6 w-auto gap-3">
            <button type="button" class="btn btn-sm btn-outline btn-light-primary px-4"
                    onclick="JrnlTag.hideSingleTag('#jrnl_dream_tag_list_div');"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="1개짜리 태그를 숨기고 보입니다.">
                <i class="bi bi-tag pe-0"></i>
            </button>
            <button type="button" class="btn btn-sm btn-outline btn-light-primary px-4"
                    onclick="JrnlTag.dreamTagListAllAjax();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="헤당 년도 전체 태그 모달 팝업을 호출합니다.">
                <i class="bi bi-tag pe-0"></i>
            </button>
        </div>
        <div class="col-1 ms-2 pe-0 border-2 border-gray-300 border-end h-75 w-10px">&nbsp;</div>
        <div class="col-1 mx-4 w-auto">
            <button type="button" class="btn btn-sm btn-outline btn-light-primary px-4"
                    onclick="JrnlDay.sort();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="날짜 정렬을 변경합니다.">
                <i class="bi bi-sort-numeric-up-alt fs-2 pe-0" id="sortIcon"></i>
            </button>
        </div>
        <div class="col-1 w-auto">
            <button type="button" class="btn btn-sm btn-outline btn-light-primary px-4"
                    onclick="Page.tagCtgrSyncAjax();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="태그 카테고리 정보를&#10;동기화합니다.">
                <i class="bi bi-arrow-repeat fs-2 pe-0" id="sortIcon"></i>
            </button>
        </div>
    </div>
</div>

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
<#include "/view/jrnl/day/_jrnl_day_tag_list_template.hbs">
<#include "/view/jrnl/day/_jrnl_day_reg_modal.ftlh">
<#include "/view/jrnl/diary/_jrnl_diary_tag_list_template.hbs">
<#include "/view/jrnl/diary/_jrnl_diary_reg_modal.ftlh">
<#include "/view/jrnl/dream/_jrnl_dream_list_template.hbs">
<#include "/view/jrnl/dream/_jrnl_dream_tag_list_template.hbs">
<#include "/view/jrnl/dream/_jrnl_dream_reg_modal.ftlh">
<#include "/view/jrnl/tag/_jrnl_tag_dtl_modal.ftlh">
<#include "/view/jrnl/tag/_jrnl_tag_list_modal.ftlh">
<#include "/view/_component/modal/comment/_comment_reg_modal.ftlh">

</@layout.layout>