<#--
 * 휴가관리 > 년도별 휴가관리 화면
 -->

<#import "/layout/layout_default.ftlh" as layout >

<#--toolbar::버튼 추가-->
<#assign toolbar>
    <#include "/view/domain/vcatn/stats/_vcatn_stats_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <#--begin::Form-->
        <form name="vcatnStatsForm" id="vcatnStatsForm" class="d-flex align-items-center flex-nowrap gap-2" enctype="multipart/form-data" method="post" >
            <input type="hidden" name="bgnDt" id="bgnDt" value="${(vcatnYy.bgnDt)!}">
            <input type="hidden" name="endDt" id="endDt" value="${(vcatnYy.endDt)!}">
            <input type="hidden" name="statsYy" id="statsYy" value="${(vcatnYy.statsYy)!}">

            <#--begin::Table-->
            <table class="table table-row-dashed table-row-gray-300 gy-7">
                <#--begin::Table head-->
                <thead>
                    <tr class="fw-bold fs-6 text-gray-800 border-bottom border-gray-200">
                        <th class="text-center">no</th>
                        <th class="text-center">직원명</th>
                        <th class="text-center">입사일</th>
                        <th class="text-center">
                            <span class="cursor-help"
                                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="해당일을 시작일로 하여&#10;해당 년도 연차를 산정합니다.">산정기준일</span>
                        </th>
                        <th class="text-center">
                            <span class="cursor-help"
                                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="입사일부터 산정기준일까지의 근속 년수입니다.">근속년수</span>
                        </th>
                        <th class="text-center">
                            <span class="cursor-help"
                                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="취업규칙에 근거하여&#10;일괄적으로 15일이 주어집니다.&#10;단 신입(근속년수 만 1년 미만)의 경우 별도의 기준으로 연차가 계산됩니다.">기본연차</span>
                        </th>
                        <th class="text-center">
                            <span class="cursor-help"
                                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="근속년수 2년마다 1일의&#10;근속 추가 연차가 주어집니다.&#10;(최대 15일)">근속<br/>추가연차</span>
                        </th>
                        <th class="text-center">
                            <span class="cursor-help"
                                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="담당하는 프로젝트가 있을 경우&#10;2일의 추가 연차가 주어집니다.">프로젝트<br/>추가연차</span>
                        </th>
                        <th class="text-center">안식주<br/>(2주연차)</th>
                        <th class="text-center">총연차</th>
                        <th class="text-center">소진</th>
                        <th class="text-center">
                            <span class="cursor-help"
                                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="(총연차 - 소진연차)">
                                잔여연차
                            </span>
                        </th>
                    </tr>
                </thead>
                <#--begin::Table body-->
                <tbody>
                    <#list statsList![] as stats>
                        <tr>
                            <td class="text-center">${stats?counter}</td>
                            <td class="text-center">
                                ${stats.user.userNm!}
                                <input type="hidden" name="statsList[${stats?counter}].userId" id="userId${stats?counter}" value="${stats.user.userId!}">
                                <#if (stats.user.userInfoYn!"Y") == "N">
                                    <i class="bi bi-person-dash vertical-middle text-muted"
                                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="직원정보가 없습니다."></i>
                                <#elseif (stats.user.retireYn!"N") == "Y">
                                    <i class="bi bi-person-dash vertical-middle text-danger"
                                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="퇴사한 인원입니다."></i>
                                <#else>
                                    <i class="bi bi-person-check vertical-middle text-success"
                                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="소속된 직원입니다."></i>
                                </#if>
                            </td>
                            <td class="w-125px text-center">
                                ${stats.user.ecnyDt!}
                            </td>
                            <td class="w-125px text-center">
                                ${DateUtils.getDateAddDayStr(vcatnYy.bgnDt, "yyyy-MM-dd", -1)!}
                            </td>
                            <td class="w-100px text-center">
                                ${stats.cnwkYy!0}
                                <input type="hidden" id="cnwkYy${stats?counter}" value="${stats.cnwkYy!0}" readonly>
                            </td>
                            <td class="w-100px text-center">
                                ${stats.bsYryc!0}
                                <#if stats.isNewbie!false>
                                    <span class="text-danger">(+${stats.newbieYryc!0})</span>
                                </#if>
                                <input type="hidden" id="bsYryc${stats?counter}" value="${stats.bsYryc!0}" readonly>
                                <input type="hidden" id="newbieYryc${stats?counter}" value="${stats.newbieYryc!0}" readonly>
                            </td>
                            <td class="w-100px text-center">
                                <input type="hidden" name="statsList[${stats?counter}].cnwkYryc" id="cnwkYryc${stats?counter}"
                                       onchange="Page.totYryc('${stats?counter}');"
                                       value="${stats.cnwkYryc!0}" maxlength="3" >
                                <span id="cnwkYryc${stats?counter}_validate_span"></span>
                            </td>
                            <td class="w-100px text-center">
                                <input type="text" name="statsList[${stats?counter}].prjctYryc" id="prjctYryc${stats?counter}" class="w-50px num"
                                       onchange="Page.totYryc('${stats?counter}');"
                                       value="${stats.prjctYryc!0}" maxlength="3">
                            </td>
                            <td class="w-100px text-center">
                                <input type="text" name="statsList[${stats?counter}].refreshYryc" id="refreshYryc${stats?counter}" class="w-50px num"
                                       onchange="Page.totYryc('${stats?counter}');"
                                       value="${stats.refreshYryc!0}" maxlength="3">
                            </td>
                            <td class="text-center">
                                <input type="hidden" name="statsList[${stats?counter}].totYryc" id="totYryc${stats?counter}"
                                       onchange="Page.totYryc('${stats?counter}');"
                                       value="${stats.totYryc!0}" >
                                <span id="totYryc${stats?counter}_validate_span">${stats.totYryc!0}</span>
                            </td>
                            <td class="text-center">
                                <input type="hidden" name="statsList[${stats?counter}].exhsYryc" id="exhsYryc${stats?counter}"
                                       onchange="Page.totYryc('${stats?counter}');"
                                       value="${stats.exhsYryc!0}" >
                                <span id="exhsYryc${stats?counter}_validate_span">${stats.exhsYryc!0}</span>
                            </td>
                            <td class="text-center">
                                <input type="hidden" name="statsList[${stats?counter}].remndrYryc" id="remndrYryc${stats?counter}"
                                       onchange="Page.totYryc('${stats?counter}');"
                                       value="${stats.remndrYryc!0}" >
                                <span id="remndrYryc${stats?counter}_validate_span">${stats.remndrYryc!0}</span>
                            </td>
                        </tr>
                    </#list>
                </tbody>
            </table>
        </form>
    </div>
    <#--begin::Card footer-->
    <div class="card-footer">
        <#--버튼 영역-->
        <div class="d-flex justify-content-end">
            <#--페이지:: 버튼: 저장하기-->
            <@component.post_btn_submit "Page.updt();" />
        </div>
    </div>
</div>

</@layout.layout>