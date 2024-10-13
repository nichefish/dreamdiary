<#--
 * 경비 관리 > 월간지출내역 화면
 -->

<#import "/layout/layout_default.ftlh" as layout >

<#--toolbar::버튼 추가-->
<#assign toolbar>
    <#include "view/domain/board//prsnl/rpt/_exptr_prsnl_rpt_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<script>
    const Page = (function() {
        return {
            /**
             * Page 객체 초기화
             */
            init: function() {
                /* 모든 table 헤더에 클릭 이벤트를 설정한다. */
                commons.util.initSortTable();
            },

            /**
             * 조회 년도 변경
             */
            statsYyMnth: function() {
                let statsYy = $("#statsYy option:selected").val();
                let statsMnth = $("#statsMnth option:selected").val();
                commons.util.blockUIReplace("${Url.EXPTR_PRSNL_RPT_ITEMS}?statsYy="+statsYy+"&statsMnth="+statsMnth);
            },

            /**
             * 엑셀 다운로드
             */
            xlsxDownload: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.download"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    commons.util.blockUIFileDownload();
                    $("#listForm").attr("action", "${Url.EXPTR_PRSNL_RPT_XLSX_DOWNLOAD!}").submit();
                });
            }
        }
    })();
    document.addEventListener("DOMContentLoaded", function() {
        Page.init();
    });
</script>

<div class="card post">
    <#--begin::Card body-->
    <div class="card-body pt-0">
        <h3>전체 리스트</h3>
        <div class="card pt-5">
            <#--begin::Table-->
            <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
                <#--begin::Table head-->
                <thead>
                    <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 fw-bolder text-muted">
                        <th class="text-center wb-keepall w-10">지출일</th>
                        <th class="text-center wb-keepall w-10">소속</th>
                        <th class="text-center wb-keepall w-8">이름</th>
                        <th class="text-center wb-keepall w-15 hidden-table">항목</th>
                        <th class="text-center wb-keepall w-15 hidden-table">상세</th>
                        <th class="text-center wb-keepall w-15 hidden-table">금액</th>
                        <th class="text-center wb-keepall hidden-table">비고</th>
                        <th class="text-center wb-keepall w-5 hidden-table">스캔본</th>
                        <th class="text-center wb-keepall w-5 hidden-table">원본</th>
                    </tr>
                </thead>
                <#--begin::Table body-->
                <tbody>
                    <#list exptrRptItemList![] as exptr>
                        <tr>
                            <td class="text-center">${exptr.exptrDt!}</td>
                            <td class="text-center">${exptr.cmpyNm!}</td>
                            <td class="text-center">${exptr.userNm!}</td>
                            <td class="text-center">${exptr.exptrTyNm!}</td>
                            <td>${exptr.exptrCn!}</td>
                            <td class="text-end pe-10">
                                ${exptr.exptrAmt!?number?string.number}원
                            </td>
                            <td>${exptr.exptrRm!}</td>
                            <td class="text-center">
                                <#if exptr.atchFileDtlNo??>
                                    <i class="bi bi-circle fs-8 text-success"></i>
                                <#else>
                                    <i class="bi bi-x text-danger"></i>
                                </#if>
                            </td>
                            <td class="text-center">
                                <#if exptr.orgnlRciptYn == "Y">
                                    <i class="bi bi-circle fs-8 text-success"></i>
                                <#elseif exptr.orgnlRciptYn == "X">
                                    <i class="bi bi-dash text-muted"></i>
                                <#else>
                                    <i class="bi bi-x text-danger"></i>
                                </#if>
                            </td>
                        </tr>
                    <#else>
                        <tr>
                            <td colspan="9" class="text-center"><@spring.message "view.list.empty"/></td>
                        </tr>
                    </#list>
                </tbody>
                <#--begin::Table footer-->
                <tfoot>
                    <tr class="bg-light">
                        <td colspan="5" class="text-center"></td>
                        <td class="text-end pe-10">
                            ${exptrRptItemSm!?number?string.number}원
                        </td>
                        <td colspan="3"></td>
                    </tr>
                </tfoot>
            </table>
        </div>

        <!--begin::Menu separator-->
        <div class="separator my-2 mb-10"></div>
        <!--end::Menu separator-->

        <h3>집계</h3>
        <div class="card pt-5">
            <#--begin::Table-->
            <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
                <#--begin::Table head-->
                <thead>
                    <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 fw-bolder text-muted">
                        <th class="text-center wb-keepall w-10">소속</th>
                        <th class="text-center wb-keepall w-8">이름</th>
                        <th class="text-center wb-keepall w-15 hidden-table">계좌</th>
                        <th class="text-center wb-keepall w-12 hidden-table">총합계</th>
                        <th class="text-center wb-keepall w-10 hidden-table">영수증 스캔</th>
                        <th class="text-center wb-keepall w-10 hidden-table">영수증 원본</th>
                        <#list exptrTyList![] as exptrTy>
                            <th class="text-center wb-keepall w-10 hidden-table">${exptrTy!}</th>
                        </#list>
                    </tr>
                </thead>
                <#--begin::Table body-->
                <tbody>
                    <#list exptrRptSmList![] as exptr>
                        <tr>
                            <td class="text-center">${exptr.cmpyNm!}</td>
                            <td class="text-center">${exptr.userNm!}</td>
                            <td class="text-center">
                                ${exptr.acntBank!}<br/>${exptr.acntNo!}
                            </td>
                            <td class="text-end pe-10">
                                ${exptr.totAmt!?number?string.number}원
                            </td>
                            <td class="text-center">
                                <i class="${exptr.atchRciptStus.icon!} text-${exptr.atchRciptStus.classNm!}"></i>
                            </td>
                            <td class="text-center">
                                <i class="${exptr.orgnlRciptStus.icon!} text-${exptr.orgnlRciptStus.classNm!}"></i>
                            </td>
                            <#list exptrTyList![] as exptrTy>
                                <#assign flag=false>
                                <#list exptr.itemList![] as item>
                                    <#if exptrTy == item.exptrTyNm>
                                        <#assign flag=true>
                                        <td class="text-end pe-10">${item.exptrSmAmt?string.number!"-"}</td>
                                    </#if>
                                </#list>
                                <#if !flag>
                                    <td class="text-center">-</td>
                                </#if>
                            </#list>
                        </tr>
                    <#else>
                        <tr>
                            <td colspan="6" class="text-center"><@spring.message "view.list.empty"/></td>
                        </tr>
                    </#list>
                </tbody>
                <#--begin::Table footer-->
                <tfoot>
                    <tr class="bg-light">
                        <td colspan="3" class="text-center"></td>
                        <td class="text-end pe-10">
                            ${totSm!?number?string.number}원
                        </td>
                        <td colspan="2" class="text-center"></td>
                        <#list exptyTySmList![] as exptrTySm>
                            <td class="text-end pe-10">${exptrTySm!?number?string.number}</td>
                        </#list>
                    </tr>
                </tfoot>
            </table>
        </div>
    </div>
</div>

<form name="procForm" id="procForm" method="get">
    <input type="hidden" name="postNo" id="postNo" value=""/>
    <input type="hidden" name="prevYn" id="prevYn" value="N"/>
</form>

</@layout.layout>