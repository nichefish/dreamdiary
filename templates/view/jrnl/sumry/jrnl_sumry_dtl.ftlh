<#--
 * 저널 결산(jrnl_sumry) : 페이지
 -->

<#import "/layout/layout.ftlh" as layout >

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
        <div class="row">
            <div class="col-2 d-flex align-items-center fs-5">
                <i class="bi bi-calendar3 fs-6 me-2"></i>
                <#if post.title??>
                    <span class="fs-5 me-1">${post.title!}</span>
                <#else>
                    <span class="fs-5 fw-bold me-1">${post.yy!}</span>
                    년 결산
                </#if>
            </div>
            <div class="col-2 d-flex align-items-center fs-5">
                <div class="text-gray-700 d-flex-center me-5">
                    <span class="fw-bold me-2">
                        꿈
                    </span>
                    <i class="bi bi-moon-stars fs-4 me-2"></i>
                    (<span class="text-underline-dotted ms-1">${post.dreamDayCnt!}</span>일 / <span class="text-underline-dotted ms-1">${post.dreamCnt!}</span>개)
                </div>
                <div class="text-gray-700 d-flex-center">
                    <span class="fw-bold me-2">
                        일기
                    </span>
                    <i class="bi bi-book fs-4 me-2"></i>
                    (<span class="text-underline-dotted ms-1">${post.diaryDayCnt!}</span>일)
                </div>
            </div>
            <div class="col-1 d-flex align-items-center">
                <button type="button" class="btn btn-sm btn-light-primary btn-outlined py-2 px-3 cursor-pointer"
                        onclick="JrnlSumryCn.regModal(${post.postNo!}, '${post.yy!}');"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="저널 일자 <@spring.message "bs.tooltip.modal.mdf"/>">
                    <i class="bi bi-book fs-4 pe-1"></i>
                    저널 결산 내용 등록
                </button>
            </div>
        </div>
    </div>
</div>

<!--begin::Card-->
<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <#list post.sumryCnList![] as sumryCn>
            <div class="row jrnl-sumry-cn">
                ${sumryCn.cn?no_esc} -
            </div>
        </#list>
    </div>
</div>

</@layout.layout>