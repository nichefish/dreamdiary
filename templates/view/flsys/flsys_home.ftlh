<#--
  -- 활동 로그 목록 화면
  -->
<#import "/layout/layout.ftlh" as layout >

<@layout.layout>

<script>
    const Page = function() {
        return {
            /** init */
            init: function () {
                commons.util.initSortTable();       // 모든 table 헤더에 클릭 이벤트를 설정한다.

                Flsys.flsysListAjax(Flsys.currPath);
            },
        }
    }();

    $(document).ready(function() {
        Page.init();
        Flsys.init("page");
    });
</script>

<div class="card post">
    <form name="procForm" id="procForm" method="get">
        <input type="hidden" name="logSysNo" id="logSysNo" />
    </form>
    <!--begin::Card header 검색+헤더 UI -->
    <#include "/view/flsys/flsys_home_header.ftlh">

    <!--begin::Tab -->
    <ul class="nav nav-tabs nav-tabs-line ps-5 my-5">
        <li class="nav-item">
            <a class="nav-link px-6" data-bs-toggle="tab" href="#kt_tab_pane_1" onclick="Flsys.activateTab(this); Flsys.flsysListAjax('${HOME_FLSYS!}');">
                <span class="nav-icon">
                    <i class="bi bi-sort-down-alt"></i>
                </span>
                <span class="nav-text ps-1">FLSYS</span>
            </a>
        </li>
    </ul>
    <!--end: Tab-->
    <!--begin::Table-->
    <div class="d-flex align-items-center my-4">
        <i class="bi bi-folder2-open text-primary"></i>
        <div class="text-muted fw-bold ms-2" id="absolute_path_div">
            <!-- absolutePath goes in here -->
        </div>
        <span class="badge badge-light-primary cursor-pointer ms-6 px-2" onclick="Flsys.flsysRefreshAjax();"
              data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="현재 디렉토리 내용을&#10;새로고침합니다.">
            <i class="bi bi-arrow-clockwise"></i>
        </span>
    </div>

    <div id="flsys_list_div">
        <!-- Handlebars Template goes in here -->
    </div>
    <!--end::Table-->
</div>
<!--end::Card-->

<#include "/view/flsys/flsys_list_template.ftlh">

<#include "/view/flsys/flsys_file_vod_modal.ftlh">
<#include "/view/flsys/flsys_file_vod_template.ftlh">

<#include "/view/flsys/flsys_meta_reg_modal.ftlh">
<#include "/view/flsys/flsys_meta_reg_template.ftlh">
<#include "/view/flsys/flsys_meta_dtl_modal.ftlh">
<#include "/view/flsys/flsys_meta_dtl_template.ftlh">
<#include "/view/flsys/flsys_meta_dtl_btn_template.ftlh">

<#include "/view/_component/tag/tag_dtl_modal.ftlh">
<#include "/view/_component/tag/tag_dtl_template.ftlh">
<#include "/view/_component/tag/tag_dtl_post_list_template.ftlh">
<#include "/view/_component/tag/tag_list_modal.ftlh">
<#include "/view/_component/tag/tag_list_template.ftlh">

</@layout.layout>