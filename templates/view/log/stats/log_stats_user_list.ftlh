<#--
  -- 활동 로그 목록 화면
  -->
<#import "/layout/layout.ftlh" as layout >

<@layout.layout toolbar=toolbar!"">

<script>
    const Page = (function() {
        return {
            init: function () {
                /* 모든 table 헤더에 클릭 이벤트를 설정한다. */
                commons.util.initSortTable();
            },
            /** 전체 로그 페이지로 이동 */
            logActvtyList: function() {
                commons.util.blockUISubmit("#listForm", "${Url.LOG_ACTVTY_LIST!}");
            },
            /** 전체 로그 페이지로 이동 */
            logStatsUserList: function() {
                commons.util.blockUISubmit("#listForm", "${Url.LOG_STATS_USER_LIST!}");
            },
            /** 목록 검색 */
            search: function() {
                $("#listForm #pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${Url.LOG_STATS_USER_LIST!}?actionTyCd=SEARCH");
            },
            /** 엑셀 다운로드 */
            xlsxDownload: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.download"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    commons.util.blockUIFileDownload();
                    $("#listForm").attr("action", "${Url.LOG_ACTVTY_LIST_XLSX_DOWNLOAD!}").submit();
                });
            }
        }
    })();
    $(document).ready(function() {
        Page.init();
    });
</script>

<div class="card post">
    <!--begin::Card header 검색+헤더 UI -->
    <#include "/view/log/stats/_log_stats_user_list_header.ftlh">

    <!--begin::Tab -->
    <ul class="nav nav-tabs nav-tabs-line ps-5 my-5">
        <li class="nav-item">
            <a class="nav-link px-6" data-bs-toggle="tab" href="#kt_tab_pane_1" onclick="Page.logActvtyList();">
                <span class="nav-icon">
                    <i class="bi bi-sort-down-alt"></i>
                </span>
                <span class="nav-text">전체 로그</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link px-6 active" data-bs-toggle="tab" href="#kt_tab_pane_2">
                <span class="nav-icon">
                    <i class="bi bi-people"></i>
                </span>
                <span class="nav-text">사용자별</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link px-6" data-bs-toggle="tab" href="#kt_tab_pane_3"
               onclick="Page.logStatsUserList();">
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
                <th class="col-3 min-w-125px text-center wb-keepall hidden-table">작업자</th>
                <th class="col-xl-2 col-5 min-w-80px text-center wb-keepall">권한</th>
                <th class="col-xl-2 col-1 text-center wb-keepall hidden-table">기간내 작업 건수</th>
                <th class="col-xl-4 col-5 min-w-80px text-center wb-keepall">작업URL</th>
                <th class="col-xl-4 col-5 min-w-80px text-center wb-keepall">작업내용</th>
                <th class="col-xl-1 text-center wb-keepall hidden-table">작업결과</th>
            </tr>
            <!--end::Table row-->
        </thead>
        <!--end::Table head-->
        <!--begin::Table body-->
        <tbody class="text-gray-600">
            <#list logStatsUserList![] as user>
                <tr >
                    <td class="text-center hidden-table">${user.rnum!}</td>
                    <td class="text-start ps-10 hidden-table">
                        <span class="fw-bold">${user.userNm!"-"}</span>
                        <span class="ms-1">(${user.userId!})</span>
                        <#if (user.userProflYn!"Y") == "N">
                            <i class="bi bi-person-dash vertical-middle text-muted"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="직원정보가 없습니다."></i>
                        <#elseif (user.retireYn!"N") == "Y">
                            <i class="bi bi-person-dash vertical-middle text-danger"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="퇴사한 인원입니다."></i>
                        <#else>
                            <i class="bi bi-person-check vertical-middle text-success"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="소속된 직원입니다."></i>
                        </#if>
                    </td>
                    <td class="text-center">
                        ${user.authNm!"-"}
                        <#if (user.authCd! == Constant.AUTH_MNGR)>
                            <i class="bi bi-person-lines-fill text-info ms-1 opacity-75"></i>   <!-- 관리자 -->
                        <#elseif (user.authCd! == Constant.AUTH_USER)>
                            <i class="bi bi-people-fill ms-1"></i>        <!-- 사용자 -->
                        </#if>
                    </td>
                    <td class="text-center hidden-table">
                        총 ${user.actvtyCnt!0?string.number}건
                    </td>
                    <td class="text-center">
                        ${user.url!}
                    </td>
                    <td class="text-center">
                        ${user.cn!"-"}
                    </td>
                    <!--begin::actionTyCd=-->
                    <td class="text-center text-end hidden-table">
                        <#if (user.rslt!"false") == "true">
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
            <#if logStatsNotUserList?has_content>
                <tr class="border-top-5"></tr>
            </#if>
            <#list logStatsNotUserList![] as user>
                <tr >
                    <td class="text-center hidden-table">${user.rnum!}</td>
                    <td class="text-start ps-10 hidden-table">
                        <span class="fw-bold">${user.userNm!"-"}</span>
                        <span class="ms-1">(${user.userId!"-"})</span>
                    </td>
                    <td class="text-center hidden-table">
                        총 ${user.actvtyCnt!0?string.number}건
                    </td>
                    <td class="text-center">
                        ${user.url!}
                    </td>
                    <td class="text-center">
                        ${user.param!"-"}
                    </td>
                    <td class="text-center">
                        ${user.cn!"-"}
                    </td>
                    <!--begin::actionTyCd=-->
                    <td class="text-center text-end hidden-table">
                        <#if (user.rslt!"false") == "true">
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

<form name="procForm" id="procForm" method="get">
    <input type="hidden" name="logActvtyNo" id="logActvtyNo" />
</form>

</@layout.layout>