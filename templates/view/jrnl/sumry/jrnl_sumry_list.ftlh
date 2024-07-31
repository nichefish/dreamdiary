<#--
 * 저널 결산(jrnl_sumry) : 페이지
 -->

<#import "/layout/layout_with_aside.ftlh" as layout >

<#--toolbar::버튼 추가-->
<#assign toolbar>
    <#include "/view/jrnl/sumry/_jrnl_sumry_list_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<script type="text/javascript" src="/metronic/assets/plugins/custom/tinymce/tinymce.bundle.js"></script>
<script type="text/javascript" src="/js/commons/commons-tinymce.js?20230908"></script>
<link rel="stylesheet" type="text/css" href="/css/jrnl.css?${releaseDate!}" />

<#--include::script modules-->
<#include "/view/jrnl/sumry/_jrnl_sumry_scripts.ftlh">
<#include "/view/jrnl/tag/_jrnl_tag_scripts.ftlh">
<#include "/view/jrnl/dream/_jrnl_dream_scripts.ftlh">
<#include "/view/_component/modal/comment/_comment_scripts.ftlh">

<script>
    const Page = (function() {
        return {
            init: function () {
                //
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
        <div class="d-flex justify-content-between fs-5">
            <div class="d-flex fs-5">
                <div class="text-gray-700 d-flex-center me-5">
                    <span class="fw-bold me-2">
                        전체 꿈
                    </span>
                    <i class="bi bi-moon-stars fs-4 me-2"></i>
                    (<span class="text-underline-dotted text-primary fw-bold mx-1">${totalSumry.dreamDayCnt!}</span>일
                    /
                    <span class="text-underline-dotted text-primary fw-bold mx-1">${totalSumry.dreamCnt!}</span>개)
                </div>
                <div class="text-gray-700 d-flex-center">
                    <span class="fw-bold me-2">
                        전체 일기
                    </span>
                    <i class="bi bi-book fs-4 mt-1 me-2"></i>
                    (<span class="text-underline-dotted text-primary fw-bold mx-1">${totalSumry.diaryDayCnt!}</span>일)
                </div>
            </div>

            <button type="button" class="btn btn-sm btn-outline btn-light-primary px-4"
                    onclick="JrnlTag.dreamTagListAllAjax();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="헤당 년도 전체 태그 모달 팝업을 호출합니다.">
                <i class="bi bi-tag pe-0"></i>
            </button>
        </div>
    </div>
</div>
<#list jrnlSumryList![] as jrnlSumry>
    <!--begin::Card-->
    <div class="card post">
        <#--begin::Card body-->
        <div class="card-body">
            <div class="row jrnl-sumry">
                <div class="col-2 d-flex align-items-center">
                    <i class="bi bi-calendar3 fs-6 me-2"></i>
                    <#if jrnlSumry.title??>
                        <span class="fs-5 me-1">${jrnlSumry.title!}</span>
                    <#else>
                        <span class="fs-5 fw-bold me-1">${jrnlSumry.yy!}</span>
                        년 결산
                    </#if>
                    <a class="badge badge-secondary ms-3 p-2 btn-white bg-hover-white blank blink-slow float-end"
                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="상세 보기">
                        <i class="bi bi-stickies fs-5 text-noti opacity-hover" onclick="JrnlSumry.dtl(${jrnlSumry.postNo!});"></i>
                    </a>
                </div>
                <div class="col d-flex align-items-center fs-5">
                    <div class="text-gray-700 d-flex-center me-5">
                        <span class="fw-bold me-2">
                            꿈
                        </span>
                        <#if jrnlSumry.dreamComptYn! == "Y">
                            <span class="cursor-help"
                                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="꿈 기록을 완료했습니다.">
                                <i class="bi bi-moon-stars-fill fs-4 me-2 text-success"></i>
                                <i class="bi bi-check text-success" style="margin-left:-0.8rem"></i>
                            </span>
                        <#else>
                            <span>
                                <i class="bi bi-moon-stars fs-4 me-2"></i>
                            </span>
                        </#if>
                        (<span class="text-underline-dotted text-primary fw-bold mx-1">${jrnlSumry.dreamDayCnt!}</span>일
                        /
                        <span class="text-underline-dotted text-primary fw-bold mx-1">${jrnlSumry.dreamCnt!}</span>개)
                    </div>
                    <div class="text-gray-700 d-flex-center">
                        <span class="fw-bold me-2">
                            일기
                        </span>
                        <i class="bi bi-book fs-4 mt-1 me-2"></i>
                        (<span class="text-underline-dotted text-primary fw-bold mx-1">${jrnlSumry.diaryDayCnt!}</span>일)
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!--end::Card-->
</#list>

<form name="procForm" id="procForm">
    <input type="hidden" name="postNo" id="postNo" value="" />
</form>

<#--include:: Aside-->
<#include "/view/jrnl/sumry/_jrnl_sumry_aside_base.ftlh">
<#include "/view/jrnl/tag/_jrnl_tag_dtl_modal.ftlh">
<#include "/view/jrnl/tag/_jrnl_tag_list_modal.ftlh">
<#include "/view/jrnl/dream/_jrnl_dream_reg_modal.ftlh">
<#include "/view/_component/modal/comment/_comment_reg_modal.ftlh">

</@layout.layout>