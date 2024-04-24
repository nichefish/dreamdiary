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
<#include "/view/jrnl/sumry/_jrnl_sumry_cn_scripts.ftlh">

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
                            onclick="JnrlSumryCn.regModal(${jrnlSumry.postNo});"
                            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="저널 일자 <@spring.message "bs.tooltip.modal.mdf"/>">
                        <i class="bi bi-pencil-square p-0"></i>
                    </button>
                </div>
            </div>
            <div class="row jrnl-sumry">
                <div class="col-1 d-flex align-items-center fs-5 fw-bold">
                    ${jrnlSumry.dreamDayCnt!} / ${jrnlSumry.dreamCnt!} / ${jrnlSumry.diaryDayCnt!}
                </div>
            </div>
        </div>
    </div>
    <!--end::Card-->
</#list>

<#--include:: Aside-->
<#include "/view/jrnl/sumry/_jrnl_sumry_aside_base.ftlh">

<#--include::modals-->
<#include "/view/jrnl/sumry/_jrnl_sumry_cn_reg_modal.ftlh">

</@layout.layout>