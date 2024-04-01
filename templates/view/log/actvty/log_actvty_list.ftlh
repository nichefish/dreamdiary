<#--
  -- 활동 로그 목록 화면
  -->
<#import "/layout/layout.ftlh" as layout >

<@layout.layout>

<script>
    const Page = function() {
        return {
            init: function () {
                commons.util.initSortTable();       // 모든 table 헤더에 클릭 이벤트를 설정한다.
            },
            /** 사용자별 로그 페이지로 이동 */
            logStatsUserList: function() {
                commons.util.blockUISubmit("#listForm", "${SiteUrl.LOG_STATS_USER_LIST!}");
            },
            /** 상세 모달 팝업 호출 */
            dtlAjax: function (logActvtyNo) {
                event.stopPropagation();
                const url = "${SiteUrl.LOG_ACTVTY_DTL_AJAX!}";
                let ajaxData = { "logActvtyNo": logActvtyNo };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.resultObj, "log_actvty_dtl", "show");
                });
            },
            /** 목록 검색 */
            search: function() {
                $("#pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${SiteUrl.LOG_ACTVTY_LIST!}?actionTyCd=SEARCH");
            },
            /** 엑셀 다운로드 */
            xlsxDownload: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.download"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    commons.util.blockUIFileDownload();
                    $("#listForm").attr("action", "${SiteUrl.LOG_ACTVTY_LIST_XLSX_DOWNLOAD!}").submit();
                });
            }
        }
    }();

    $(document).ready(function() {
        Page.init();
    });
</script>

<div class="card post">
    <form name="procForm" id="procForm" method="get">
        <input type="hidden" name="logActvtyNo" id="logActvtyNo" />
    </form>
    <!--begin::Card header 검색+헤더 UI -->
    <#include "/view/log/actvty/log_actvty_list_header.ftlh">

    <!--begin::Tab -->
    <ul class="nav nav-tabs nav-tabs-line ps-5 my-5">
        <li class="nav-item">
            <a class="nav-link px-6 active" data-bs-toggle="tab" href="#kt_tab_pane_1">
                <span class="nav-icon">
                    <i class="bi bi-sort-down-alt"></i>
                </span>
                <span class="nav-text">전체 로그</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link px-6" data-bs-toggle="tab" href="#kt_tab_pane_2" onclick="Page.logStatsUserList();">
                <span class="nav-icon">
                    <i class="bi bi-people"></i>
                </span>
                <span class="nav-text">사용자별</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link px-6" data-bs-toggle="tab" href="#kt_tab_pane_3" onclick="Page.logStatsUserList();">
                <span class="nav-icon">
                    <i class="bi bi-grid-1x2"></i>
                </span>
                <span class="nav-text">기능별</span>
            </a>
        </li>
    </ul>
    <!--end: Tab-->
    <!--begin::Table-->
    <table class="table table-hover align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
        <!--begin::Table head-->
        <thead>
            <!--begin::Table row-->
            <tr class="text-start text-muted fs-7 text-uppercase gs-0">
                <th class="col-xl-1 text-center wb-keepall w-10 hidden-table">번호</th>
                <th class="col-xl-2 col-4 text-center wb-keepall w-10">작업일시</th>
                <th class="col-xl-2 col-3 min-w-125px text-center wb-keepall">작업자</th>
                <th class="col-xl-1 text-center wb-keepall hidden-table">작업IP</th>
                <th class="col-xl-1 text-center wb-keepall hidden-table">활동 유형</th>
                <th class="col-xl-3 col-5 min-w-80px text-center wb-keepall">작업URL</th>
                <th class="col-xl-3 col-5 min-w-80px text-center wb-keepall">작업결과 메세지</th>
                <th class="col-xl-1 col-1 text-center wb-keepall">작업결과</th>
            </tr>
            <!--end::Table row-->
        </thead>
        <!--end::Table head-->
        <!--begin::Table body-->
        <tbody class="text-gray-600">
            <#list logActvtyList![] as log>
                <tr class="cursor-pointer <#if log.isJobUser!false>bg-fxd-list</#if>" onclick="Page.dtlAjax('${log.logActvtyNo!}');">
                    <td class="text-center hidden-table">${log.rnum!}</td>
                    <td class="text-center">
                        ${log.logDt!"-"}
                    </td>
                    <td class="text-center hidden-table">
                        <div>
                            ${log.logUserNm!"-"}
                            <#if (log.authCd! == Constant.AUTH_MNGR)>
                                <i class="bi bi-person-lines-fill text-info ms-1 opacity-75"
                                   data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="${log.authNm!"-"}"></i>   <!-- 관리자 -->
                            <#elseif (log.authCd! == Constant.AUTH_USER)>
                                <i class="bi bi-people-fill ms-1"
                                   data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="${log.authNm!"-"}"></i>   <!-- 관리자 -->
                            </#if>
                        </div>
                        <div>(${log.logUserId!})</div>
                        <#if log.isRegstr!false>
                            <span class="badge badge-secondary opacity-75 mx-1 fs-9">나</span>
                        </#if>
                    </td>
                    <td class="text-center hidden-table">
                        ${log.ipAddr!"-"}
                    </td>
                    <td class="text-center hidden-table">
                        ${log.actionTyNm!"-"}
                    </td>
                    <td class="text-start ps-4">
                        <label class="text-decoration-underline text-primary cursor-help"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="URL:&#10;${log.url!}">
                            ${log.urlNm!log.url}
                        </label>
                    </td>
                    <td class="text-center">
                        ${log.rsltMsg!"-"}
                    </td>
                    <!--begin::actionTyCd=-->
                    <td class="text-center text-end hidden-table">
                        <#if (log.rslt!"false") == "true">
                            <span class="text-success">
                                성공<i class="bi bi-check text-success fs-8"></i>
                            </span>
                        <#else>
                            <span class="text-danger">
                                실패<i class="bi bi-x text-danger fs-8"></i>
                            </span>
                        </#if>
                    </td>
                    <!--end::actionTyCd=-->
                </tr>
            <#else>
                <tr>
                    <td colspan="8" class="text-center"><@spring.message "view.list.empty"/></td>
                </tr>
            </#list>
        </tbody>
        <!--end::Table body-->
    </table>
    <!--end::Table-->
    <!-- pagination::begin -->
    <#include "/view/_component/_pagination.ftlh">
    <!-- pagination::end -->
</div>
<!--end::Card-->

<#include "/view/log/actvty/log_actvty_dtl_modal.ftlh">
<#include "/view/log/actvty/log_actvty_dtl_template.ftlh">

</@layout.layout>