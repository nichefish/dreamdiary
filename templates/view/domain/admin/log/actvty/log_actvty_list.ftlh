<#--
 * 활동 로그(log_actvty) > 목록 화면
 -->

<#import "/layout/layout_default.ftlh" as layout >

<#--toolbar::버튼 추가-->
<#assign toolbar>
    <!--begin::Card header 검색+헤더 UI -->
    <#include "/view/domain/admin/log/actvty/_log_actvty_list_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <#--begin::Table-->
        <table class="table table-hover align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
            <#--begin::Table head-->
            <thead>
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
            </thead>
            <#--begin::Table body-->
            <tbody class="text-gray-600">
                <#list logActvtyList![] as log>
                    <tr class="cursor-pointer <#if log.isJobUser!false>bg-fxd-list</#if>"
                        onclick="dF.LogActvty.dtlModal('${log.logActvtyNo!}');">
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
                            <div>(${log.userId!})</div>
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
                        <td colspan="8" class="text-center"><@spring.message 'view.list.empty'/></td>
                    </tr>
                </#list>
            </tbody>
            <!--end::Table body-->
        </table>
    </div>
    <#--begin::Card footer-->
    <div class="card-footer">
        <#--pagination::begin-->
        <#include "/view/global/_pagination.ftlh">
        <#--pagination::end-->
    </div>
</div>
<!--end::Card-->

<form name="procForm" id="procForm" method="get">
    <input type="hidden" name="logActvtyNo" id="logActvtyNo" />
</form>

<#--include::log_actvty_module-->
<#include "/view/domain/admin/log/actvty/_modal/_log_actvty_dtl_modal.ftlh">
<script type="text/javascript" src="/js/view/domain/admin/log/actvty/log_actvty_module.js"></script>

<!--include::page scripts-->
<script type="text/javascript" src="/js/view/domain/admin/log/actvty/log_actvty_list.js"></script>

</@layout.layout>