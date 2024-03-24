<#--
  -- 분류 코드(CL_CD) > 목록 화면
  -->
<#import "/layout/layout.ftlh" as layout >
<#import "/spring.ftl" as spring />

<@layout.layout>

<script>
    const Page = function() {
        return {
            init: function() {
                /** 검색인자 세팅 */
                <#if searchParamMap?has_content >
                    <#list searchParamMap?keys as key>
                        $("#${key!}").val("${searchParamMap[key]!}");
                    </#list>
                </#if>

                // 모든 table 헤더에 클릭 이벤트를 설정한다.
                Page.sortMode = "FORWARD";
                commons.util.fnSortTable();
            },
            /** 목록 검색 */
            fnSearch: function() {
                $("#pageNo").val("");
                commons.util.blockUISubmit("#listForm", "${SiteUrl.CL_CD_LIST!}?actionTyCd=SEARCH");
            },
            /** 사용으로 변경 (Ajax) */
            fnUseAjax: function(clCd) {
                event.stopPropagation();
                Swal.fire({
                    text: <@spring.message "view.cnfm.use"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = '${SiteUrl.CL_CD_USE_AJAX!}';
                    let ajaxData = { "clCd": clCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 미사용으로 변경 (Ajax) */
            fnUnuseAjax: function(clCd) {
                event.stopPropagation();
                Swal.fire({
                    text: <@spring.message "view.cnfm.unuse"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = '${SiteUrl.CL_CD_UNUSE_AJAX!}';
                    let ajaxData = { "clCd": clCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 등록 팝업 호출 */
            fnRegForm: function() {
                commons.util.handlebarsTemplate({}, "cl_cd_reg", "show");
                commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                commons.validate.replaceBlankRegex("#clCdRegForm #clCd");
                commons.validate.toUpperCase("#clCdRegForm #clCd");
            },
            /** 등록 또는 수정 처리(Ajax) */
            fnRegAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    $("#clCdRegForm #regYn").val("Y");
                    const url = "${SiteUrl.CL_CD_REG_AJAX!}";
                    let ajaxData = $("#clCdRegForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReplace("${SiteUrl.CL_CD_LIST!}");
                            });
                    });
                });
            },
            /** 상세 화면 이동 */
            fnDtl: function(clCd) {
                commons.util.blockUIRequest();
                $("#procForm #clCd").val(clCd);
                commons.util.blockUISubmit("#procForm", "${SiteUrl.CL_CD_DTL!}");
            },
            /** 상세 모달 팝업 호출 */
            fnDtlAjax: function(clCd) {
                event.stopPropagation();
                const url = "${SiteUrl.CL_CD_DTL_AJAX!}";
                let ajaxData = { "clCd": clCd };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.resultObj, "cl_cd_dtl", "show");
                });
            },
            /** 삭제 처리 (ajax) */
            fnDelAjax: function(clCd) {
                event.stopPropagation();
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.CL_CD_DEL_AJAX!}";
                    let ajaxData = { "clCd" : clCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 사이트 관리 페이지로 이동 */
            fnSiteMngList: function() {
                commons.util.blockUISubmit("#procForm", "${SiteUrl.SITE_MNG!}");
            },
            /** 코드 관리 페이지로 이동 */
            fnCdList: function() {
                commons.util.blockUISubmit("#procForm", "${SiteUrl.CL_CD_LIST!}");
            },
        }
    }();

    $(document).ready(function() {
        Page.init();
    });
</script>

<div class="card post">
    <form name="procForm" id="procForm" method="get">
        <input type="hidden" id="clCd" name="clCd" />
    </form>
    <!--begin::Tab -->
    <ul class="nav nav-tabs nav-tabs-line ps-5 my-5">
        <li class="nav-item">
            <a class="nav-link px-6" data-bs-toggle="tab" href="#kt_tab_pane_1" onclick="Page.fnSiteMngList();">
                <span class="nav-icon">
                    <i class="bi bi-sort-down-alt"></i>
                </span>
                <span class="nav-text">사이트 관리</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link px-6 active" data-bs-toggle="tab" href="#kt_tab_pane_2" onclick="Page.fnLogStatsUserList();">
                <span class="nav-icon">
                    <i class="bi bi-people"></i>
                </span>
                <span class="nav-text">코드 관리</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link px-6" data-bs-toggle="tab" href="#kt_tab_pane_3" onclick="Page.fnLogStatsUserList();">
                <span class="nav-icon">
                    <i class="bi bi-grid-1x2"></i>
                </span>
                <span class="nav-text">기능별</span>
            </a>
        </li>
    </ul>

    <!--begin::Card header 검색+헤더 UI -->
    <#include "/view/admin/cd/cl_cd_list_header.ftlh">
    <!--end::Card header-->
    <!--begin::Card Body-->
    <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
        <!--begin::Table head-->
        <thead>
            <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 fw-bolder text-muted">
                <th class="col-1 text-center wb-keepall w-8 hidden-table">번호</th>
                <th class="col-2 text-center wb-keepall">분류코드</th>
                <th class="col-2 text-center wb-keepall">분류코드명</th>
                <th class="col-4 text-center wb-keepall">분류코드 설명</th>
                <th class="col-1 text-center wb-keepall">상세코드 수</th>
                <th class="col-1 text-center wb-keepall">사용여부</th>
                <th class="col-1 text-center wb-keepall">-</th>
            </tr>
        </thead>
        <!--end::Table head-->
        <!--begin::Table body-->
        <tbody>
            <#list clCdList![] as clCd>
                <tr class="bg-hover-secondary" onclick="Page.fnDtl('${clCd.clCd!}');">
                    <td class="text-center hidden-table">
                        ${clCd.rnum!}
                    </td>
                    <td class="text-start px-6 cursor-pointer">
                        ${clCd.clCd!}
                        <a class="badge badge-secondary ms-2 p-2 btn-white blank blink-slow float-end"
                           data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="모달 팝업으로 보기">
                            <i class="bi bi-stickies fs-5 text-noti opacity-hover" onclick="Page.fnDtlAjax('${clCd.clCd!}');"></i>
                        </a>
                    </td>
                    <td class="text-start ps-6">
                        <span class="border-bottom">
                            ${clCd.clCdNm!}
                        </span>
                    </td>
                    <td class="text-start ps-6 hidden-table">
                        ${clCd.clCdDc!}
                    </td>
                    <td class="text-center hidden-table">
                        ${clCd.dtlCdCnt!0}
                    </td>
                    <td class="text-center hidden-table">
                        <#if (clCd.useYn!"Y") == "Y">
                            <div class="badge badge-success cursor-pointer"
                                 onclick="Page.fnUnuseAjax('${clCd.clCd}');"
                                 data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                                 title="클릭하여 사용 상태를&#10;변경합니다.">
                                <i class="bi bi-check text-light blink-slow"></i>사용
                            </div>
                        <#else>
                            <div class="badge badge-secondary btn-white cursor-pointer"
                                 onclick="Page.fnUseAjax('${clCd.clCd}');"
                                 data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                                 title="클릭하여 사용 상태를&#10;변경합니다.">
                                <i class="bi bi-x blink-slow"></i>미사용
                            </div>
                        </#if>
                    </td>
                    <td class="text-center hidden-table">
                        <div class="badge badge-danger badge-outlined cursor-pointer"
                             onclick="Page.fnDelAjax('${clCd.clCd}');"
                             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                             title="<@spring.message "bs.tooltip.del"/>">
                            <i class="bi bi-trash text-danger fs-7 blink-slow"></i>삭제
                        </div>
                    </td>
                </tr>
            <#else>
                <tr>
                    <td colspan=8" class="text-center"><@spring.message "view.list.empty"/></td>
                </tr>
            </#list>
            <!--end::Table row-->
        </tbody>
    </table>
    <!-- pagination::begin -->
    <#include "/view/_component/_pagination.ftlh">
    <!-- pagination::end -->
</div>

<#include "/view/admin/cd/cl_cd_reg_modal.ftlh">
<#include "/view/admin/cd/cl_cd_reg_template.ftlh">
<#include "/view/admin/cd/cl_cd_dtl_modal.ftlh">
<#include "/view/admin/cd/cl_cd_dtl_template.ftlh">

</@layout.layout>