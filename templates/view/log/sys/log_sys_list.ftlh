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
            /** 목록 검색 */
            search: function() {
                $("#pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${SiteUrl.LOG_SYS_LIST!}?actionTyCd=SEARCH");
            },
        }
    }();

    $(document).ready(function() {
        Page.init();
    });
</script>

<div class="card post">
    <form name="procForm" id="procForm" method="get">
        <input type="hidden" name="logSysNo" id="logSysNo" />
    </form>
    <!--begin::Card header 검색+헤더 UI -->
    <#include "/view/log/sys/log_sys_list_header.ftlh">

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
                <th class="col-xl-1 col-4 text-center wb-keepall w-10 hidden-table">번호</th>
                <th class="col-xl-2 col-4 text-center wb-keepall w-10">작업일시</th>
                <th class="col-xl-2 col-3 min-w-125px text-center wb-keepall hidden-table">작업자</th>
                <th class="col-xl-2 col-1 text-center wb-keepall hidden-table">작업IP</th>
                <th class="col-xl-4 col-5 min-w-80px text-center wb-keepall">작업URL</th>
                <th class="col-xl-4 col-5 min-w-80px text-center wb-keepall">작업 파라미터</th>
                <th class="col-xl-4 col-5 min-w-80px text-center wb-keepall">작업내용</th>
                <th class="col-xl-1 text-center wb-keepall hidden-table">작업결과</th>
            </tr>
            <!--end::Table row-->
        </thead>
        <!--end::Table head-->
        <!--begin::Table body-->
        <tbody class="text-gray-600">
            <#list logSysList![] as log>
                <tr <#if log.isJobUser!false>class="bg-fxd-list"</#if>>
                    <td class="text-center hidden-table">${log.rnum!}</td>
                    <td class="text-center">
                        ${log.logDt!}
                    </td>
                    <td class="text-center hidden-table">
                        <div>
                            ${log.logUserNm!"-"}
                            <i class="bi bi-gear s-1 opacity-75"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="시스템 계정입니다."></i>
                        </div>
                        <div>(${log.logUserId!})</div>
                        <#if log.isRegstr!false>
                            <span class="badge badge-secondary opacity-75 mx-1 fs-9">나</span>
                        </#if>
                    </td>
                    <td class="text-center hidden-table">
                        -
                    </td>
                    <td class="text-center">
                        ${log.url!}
                    </td>
                    <td class="text-center">
                        -
                    </td>
                    <td class="text-center">
                        ${log.cn!"-"}
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
                    <td colspan="7" class="text-center"><@spring.message "view.list.empty"/></td>
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

</@layout.layout>