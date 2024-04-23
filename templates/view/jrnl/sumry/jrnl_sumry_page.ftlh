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
        <#--begin::Table body-->
        <div id="jrnl_sumry_list_div">
            <#list jrnlSumryList![] as jrnlSumry>
                <div class="row jrnl-sumry">
                    <div class="row jrnl-sumry-header">
                        <div class="col-1 d-flex align-items-center fs-5 fw-bold">
                            <i class="bi bi-calendar3 fs-6 me-2"></i>
                            ${jrnlSumry.yy!}
                        </div>
                    </div>
                </div>
            </#list>
        </div>
    </div>
</div>
<!--end::Card-->

<#--include:: Aside-->
<#include "/view/jrnl/sumry/_jrnl_sumry_aside_base.ftlh">

<#--include::modals-->
<#include "/view/jrnl/sumry/_jrnl_sumry_list_template.hbs">

</@layout.layout>