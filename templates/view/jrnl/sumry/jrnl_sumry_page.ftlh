<#--
 * 저널 결산(jrnl_sumry) : 페이지
 -->

<#import "/layout/layout_with_aside.ftlh" as layout >

<#--toolbar::버튼 추가-->
<#assign toolbar>
    <#include "/view/jrnl/sumry/_jrnl_sumry_page_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<script type="text/javascript" src="/metronic/assets/plugins/custom/tinymce/tinymce.bundle.js"></script>
<script type="text/javascript" src="/js/commons/commons-tinymce.js?20230908"></script>
<link rel="stylesheet" type="text/css" href="/css/dream.css?${releaseDate!}" />

<#--include::script modules-->
<#include "/view/jrnl/sumry/_jrnl_sumry_scripts.ftlh">
<#include "/view/jrnl/sumry_cn/_jrnl_sumry_cn_scripts.ftlh">

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
        ${totalSumry.dreamDayCnt!} / ${totalSumry.dreamCnt!} / ${totalSumry.diaryDayCnt!}

    </div>
</div>
<#list jrnlSumryList![] as jrnlSumry>
    <!--begin::Card-->
    <div class="card post">
        <#--begin::Card body-->
        <div class="card-body">
            <#--begin::Table body-->
            <div class="row jrnl-sumry">
                <div class="col-1 d-flex align-items-center">
                    <i class="bi bi-calendar3 fs-6 me-2"></i>
                    <span class="fs-5 fw-bold me-1">${jrnlSumry.yy!}</span> 년 결산
                </div>
                <div class="col-1 d-flex align-items-center">
                    <button type="button" class="btn btn-sm btn-light-primary btn-outlined py-2 px-3 cursor-pointer"
                            onclick="JrnlSumryCn.regModal(${jrnlSumry.postNo!}, '${jrnlSumry.yy!}');"
                            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="저널 일자 <@spring.message "bs.tooltip.modal.mdf"/>">
                        <i class="bi bi-book fs-4 pe-1"></i>
                        저널 결산 내용 등록
                    </button>
                </div>
                <div class="col-2 d-flex align-items-center fs-5">
                    <div class="text-gray-700 d-flex-center me-5">
                        <span class="fw-bold me-2">
                            꿈
                        </span>
                        <i class="bi bi-moon-stars fs-4 me-2"></i>
                        (<span class="text-underline-dotted ms-1">${jrnlSumry.dreamDayCnt!}</span>일 / <span class="text-underline-dotted ms-1">${jrnlSumry.dreamCnt!}</span>개)
                    </div>
                    <div class="text-gray-700 d-flex-center">
                        <span class="fw-bold me-2">
                            일기
                        </span>
                        <i class="bi bi-book fs-4 me-2"></i>
                        (<span class="text-underline-dotted ms-1">${jrnlSumry.diaryDayCnt!}</span>일)
                    </div>
                </div>
            </div>
            <div class="row jrnl-item mt-5">
                <#list jrnlSumry.sumryCnList![] as sumryCn>
                    <div class="row jrnl-sumry-cn">
                        ${sumryCn.cn?no_esc} -
                    </div>
                </#list>
            </div>
        </div>
    </div>
    <!--end::Card-->
</#list>

<#--include:: Aside-->
<#include "/view/jrnl/sumry/_jrnl_sumry_aside_base.ftlh">

<#--include::modals-->
<#include "/view/jrnl/sumry_cn/_jrnl_sumry_cn_reg_modal.ftlh">

</@layout.layout>