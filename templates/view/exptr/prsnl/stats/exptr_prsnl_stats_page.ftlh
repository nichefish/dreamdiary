<#--
 * 경비 관리 > 경비지출누적집계 > 경비지출누적집계 화면
 -->

<#import "/layout/layout.ftlh" as layout >

<#--toolbar::버튼 추가-->
<#assign toolbar>
    <#include "/view/exptr/prsnl/stats/_exptr_prsnl_stats_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<script>
    const Page = (function() {
        return {
            init: function () {
                /* 모든 table 헤더에 클릭 이벤트를 설정한다. */
                commons.util.initSortTable();

                // 해당 월 총합 구하기
                for (let i = 1; i <= 12; i++) {
                    $("#mnthSumSpan"+i).text(commons.util.getReqstItemTotSum("id^=mnth"+i+"__").toLocaleString());
                }
            },
            /** 조회 년도 변경 */
            statsYy: function() {
                let statsYy = $("#statsYy option:selected").val();
                commons.util.blockUIReplace("${Url.EXPTR_PRSNL_STATS_PAGE}?statsYy="+statsYy);
            },
            /** 상세 화면 이동 */
            statsDtl: function(postNo) {
                $("#postNo").val(postNo);
                commons.util.blockUISubmit("#procForm", "${Url.EXPTR_PRSNL_STATS_DTL!}");
            },
            /** 엑셀 다운로드 */
            xlsxDownload: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.download"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    commons.util.blockUIFileDownload();
                    $("#listForm").attr("action", "${Url.EXPTR_PRSNL_STATS_XLSX_DOWNLOAD!}").submit();
                });
            }
        }
    })();
    $(document).ready(function() {
        Page.init();
    });
</script>

<div class="card post">
    <#--begin::Card Body-->
    <div class="card-body pt-0">
        <#--begin::Table-->
        <table class="table table-hover align-middle table-row-dashed fs-small gy-5 table-fixed">
            <#--begin::Table head-->
            <thead>
                <tr class="text-start text-muted fw-bolder fs-7 text-uppercase gs-0">
                    <th class="w-90px text-center wb-keepall">구분</th>
                    <th class="w-90px text-center wb-keepall">소속</th>
                    <th class="w-90px text-center wb-keepall">이름</th>
                    <#list [1,2,3,4,5,6,7,8,9,10,11,12] as mnth>
                        <th class="min-w-120px text-center wb-keepall">${mnth}월</th>
                    </#list>
                </tr>
            </thead>
            <#--begin::Table body-->
            <tbody>
                <#list statsList![] as stats>
                    <tr>
                        <td class="text-center">${(stats.user.emplymNm)!}</a></td>
                        <td class="text-center">${(stats.user.cmpyNm)!}</td>
                        <td class="text-center">${(stats.user.userNm)!}</td>
                        <#list (stats.exptrPrsnlList)![] as mnth>
                            <td class="text-center">
                                <#if mnth??>
                                    <#if (mnth.totAmt)! gt 0>
                                        <a href="javascript:void(0);"
                                        onclick="Page.statsDtl('${mnth.postNo!}');"
                                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="해당 항목으로 이동합니다.">
                                    </#if>
                                    <div class="d-flex justify-content-center rounded p-1 fs-7 badge-<#if (mnth.cfYn!"N") == "Y">success<#else>secondary</#if>">
                                        <div class="item">
                                            <input type="hidden" id="mnth${mnth?counter}__${stats?counter}" value="${mnth.totAmt!}">
                                            ${mnth.totAmt!?number?string.number}
                                        </div>
                                        <div class="item">
                                            <i class="bi bi-app-indicator mx-1 text-primary"></i>
                                        </div>
                                    </div>
                                    <div class="d-flex-center">
                                        <#if mnth.itemCnt == 0>
                                            - / -
                                        <#else>
                                            <i class="${mnth.atchRciptStus.icon!} text-${mnth.atchRciptStus.classNm!}"></i>
                                            <span>/</span>
                                            <i class="${mnth.orgnlRciptStus.icon!} text-${mnth.orgnlRciptStus.classNm!}"></i>
                                        </#if>
                                    </div>
                                    <#if (mnth.totAmt)! gt 0>
                                        </a>
                                    </#if>
                                <#else>
                                    0
                                </#if>
                            </td>
                        </#list>
                    </tr>
                <#else>
                    <tr>
                        <td colspan="15" class="text-center"><@spring.message "view.list.empty"/></td>
                    </tr>
                </#list>
            </tbody>
            <#--begin::Table footer-->
            <tfoot>
                <tr class="bg-light">
                    <td colspan="2" class="text-center"></td>
                    <td class="text-center fw-bold">합계</td>
                    <#list [1,2,3,4,5,6,7,8,9,10,11,12] as mnth>
                        <td class="text-center" id="mnthSumSpan${mnth!}">-</td>
                    </#list>
                </tr>
            </tfoot>
        </table>
    </div>
</div>

<form name="procForm" id="procForm" method="get">
    <input type="hidden" name="postNo" id="postNo" />
</form>

</@layout.layout>