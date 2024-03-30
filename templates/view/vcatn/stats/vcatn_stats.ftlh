<#--
  -- 휴가관리 > 년도별 휴가관리 화면
  -->
<#import "/layout/layout.ftlh" as layout >

<@layout.layout>

<script>
    const Page = function() {
        return {
            init: function () {
                <#list statsList![] as stats>
                    Page.totYryc("${stats?counter}");
                </#list>

                // 숫자와 점 제외하곤 입력불가
                commons.validate.onlyNumAndDot(".num");

                // 모든 table 헤더에 클릭 이벤트를 설정한다.
                Page.sortMode = "FORWARD";
                commons.util.sortTable();
            },
            /** 조회년도 변경 */
            chgYy: function(obj) {
                commons.util.blockUIReplace("${SiteUrl.VCATN_STATS_YY!}?statsYy=" + $(obj).val());
            },
            /** 연자 숫자 계산 */
            totYryc: function(idx) {
                // 근속년수에 따라 근속추가연차 계산
                const cnwkYryc = Math.floor(commons.util.toNumber("#cnwkYy"+idx) / 2);
                $("#cnwkYryc"+idx).val(cnwkYryc);
                $("#cnwkYryc"+idx+"Span").text(cnwkYryc);
                // 총연차 계산
                const bsYryc = commons.util.toNumber("#bsYryc"+idx) + commons.util.toNumber("#newbieYryc"+idx) + commons.util.toNumber("#cnwkYryc"+idx)
                const totYryc = bsYryc + commons.util.toNumber("#prjctYryc"+idx) + commons.util.toNumber("#refreshYryc"+idx);
                $("#totYryc"+idx).val(totYryc);
                $("#totYryc"+idx+"Span").text(totYryc);
                // 총연차 - 소진연차 = 잔여연차
                const remndrYryc = commons.util.toNumber("#totYryc"+idx) - commons.util.toNumber("#exhsYryc"+idx);
                $("#remndrYryc"+idx).val(remndrYryc);
                $("#remndrYryc"+idx+"Span").text(remndrYryc);
            },
            /** 정보 업데이트 (저장) */
            updt: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.mdf"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.VCATN_STATS_YY_UPDT_AJAX!}";
                    let ajaxData = $("#vcatnStatsForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({text: res.message})
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 엑셀 다운로드 */
            xlsxDownload: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.download"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    commons.util.blockUIFileDownload();
                    $("#vcatnStatsForm").attr("action", "${SiteUrl.VCATN_STATS_YY_XLSX_DOWNLOAD!}").submit();
                });
            }
        }
    }();

    $(document).ready(function() {
        Page.init();
    });
</script>

<div class="card post">
    <form name="vcatnStatsForm" id="vcatnStatsForm" class="form" enctype="multipart/form-data" method="post" >
        <!--begin::Card header 검색+헤더 UI -->
        <div class="card-header border-0 pt-6">
            <input type="hidden" name="bgnDt" id="bgnDt" value="${(vcatnYy.bgnDt)!}">
            <input type="hidden" name="endDt" id="endDt" value="${(vcatnYy.endDt)!}">
            <div class="d-flex align-items-center">
                <div class="position-relative w-md-100px me-md-2"
                     data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="조회 년도를 변경합니다.">
                    <select class="form-select form-select-solid w-90px mx-1" name="statsYy" id="statsYy" data-hide-search="true" onchange="Page.chgYy(this);">
                        <#list yyList![] as yy>
                            <option value="${yy.statsYy!}" <#if yy.statsYy == vcatnYy.statsYy>selected="selected"</#if>>${yy.statsYy!}</option>
                        </#list>
                    </select>
                </div>
                <div class="position-relative w-md-150px pt-2 me-md-2">
                    <h4>년도 휴가관리</h4>
                </div>
                <div class="position-relative w-md-500px me-md-2" style="padding-left:3rem">
                    <span class="fs-6 text-gray-600 align-center mt-4">■ 연차 사용 기간 : ${(vcatnYy.bgnDt)!} ~ ${(vcatnYy.endDt)!}</span>
                </div>
            </div>

            <div class="card-title text-end">
                <div class="d-flex align-items-center position-relative my-1">
                </div>
            </div>
            <div class="card-toolbar">
                <!--begin::Toolbar-->
                <div class="d-flex justify-content-end" data-kt-user-table-toolbar="base">
                    <button type="button" class="btn btn-sm btn-light-primary me-md-2 ms-auto" onclick="Page.xlsxDownload();"
                            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="데이터를 엑셀 파일로&#10;다운로드합니다.">
                        <i class="fas fa-download fs-7"></i>다운로드
                    </button>
                </div>
                <!--end::Toolbar-->
            </div>
        </div>

        <div class="separator separator-dashed border-gray-300 mt-5 mb-5"></div>

        <table class="table table-row-dashed table-row-gray-300 gy-7">
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
                            <input type="hidden" id="cnwkYy${stats?counter}" value="${stats.cnwkYy!0}" maxlength="3" readonly>
                        </td>
                        <td class="w-100px text-center">
                            ${stats.bsYryc!0}
                            <#if stats.isNewbie!false>
                                <span class="text-danger">(+${stats.newbieYryc!0})</span>
                            </#if>
                            <input type="hidden" id="bsYryc${stats?counter}" value="${stats.bsYryc!0}" maxlength="3" readonly>
                            <input type="hidden" id="newbieYryc${stats?counter}" value="${stats.newbieYryc!0}" maxlength="3" readonly>
                        </td>
                        <td class="w-100px text-center">
                            <input type="hidden" class="w-50px" name="statsList[${stats?counter}].cnwkYryc" id="cnwkYryc${stats?counter}" value="${stats.cnwkYryc!0}" maxlength="3" onchange="Page.totYryc('${stats?counter}');">
                            <span id="cnwkYryc${stats?counter}Span"></span>
                        </td>
                        <td class="w-100px text-center">
                            <input type="text" class="w-50px num" name="statsList[${stats?counter}].prjctYryc" id="prjctYryc${stats?counter}" value="${stats.prjctYryc!0}" maxlength="3" onchange="Page.totYryc('${stats?counter}');">
                        </td>
                        <td class="w-100px text-center">
                            <input type="text" class="w-50px num" name="statsList[${stats?counter}].refreshYryc" id="refreshYryc${stats?counter}" value="${stats.refreshYryc!0}" maxlength="3" onchange="Page.totYryc('${stats?counter}');">
                        </td>
                        <td class="text-center">
                            <input type="hidden" class="w-50px" name="statsList[${stats?counter}].totYryc" id="totYryc${stats?counter}" value="${stats.totYryc!0}" onchange="Page.totYryc('${stats?counter}');">
                            <span id="totYryc${stats?counter}Span">${stats.totYryc!0}</span>
                        </td>
                        <td class="text-center">
                            <input type="hidden" class="w-50px" name="statsList[${stats?counter}].exhsYryc" id="exhsYryc${stats?counter}" value="${stats.exhsYryc!0}" onchange="Page.totYryc('${stats?counter}');">
                            <span id="exhsYryc${stats?counter}Span">${stats.exhsYryc!0}</span>
                        </td>
                        <td class="text-center">
                            <input type="hidden" class="w-50px" name="statsList[${stats?counter}].remndrYryc" id="remndrYryc${stats?counter}" value="${stats.remndrYryc!0}" onchange="Page.totYryc('${stats?counter}');">
                            <span id="remndrYryc${stats?counter}Span">${stats.remndrYryc!0}</span>
                        </td>
                    </tr>
                </#list>
            </tbody>
        </table>
    </form>

    <div class="card-footer d-flex justify-content-end py-6 px-9">
        <button type="button" class="btn btn-primary me-2" onclick="Page.updt();">
            <i class="bi bi-pencil-square"></i>저장
        </button>
    </div>
</div>

</@layout.layout>