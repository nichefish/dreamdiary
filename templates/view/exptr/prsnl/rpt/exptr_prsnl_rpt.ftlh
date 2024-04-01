<#--
  -- 경비 관리 > 월간지출내역 화면
  -->
<#import "/layout/layout.ftlh" as layout >

<@layout.layout>

<script>
    const Page = function() {
        return {
            init: function () {
                commons.util.initSortTable();       // 모든 table 헤더에 클릭 이벤트를 설정한다.
            },
            /** 조회 년도 변경 */
            statsYyMnth: function() {
                let statsYy = $("#statsYy option:selected").val();
                let statsMnth = $("#statsMnth option:selected").val();
                commons.util.blockUIReplace("${SiteUrl.EXPTR_PRSNL_RPT_ITEMS}?statsYy="+statsYy+"&statsMnth="+statsMnth);
            },
            /** 엑셀 다운로드 */
            xlsxDownload: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.download"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    commons.util.blockUIFileDownload();
                    $("#listForm").attr("action", "${SiteUrl.EXPTR_PRSNL_RPT_XLSX_DOWNLOAD!}").submit();
                });
            }
        }
    }();

    $(document).ready(function() {
        Page.init();
    });
</script>

<div class="card post">
    <div class="card-header border-0 pt-6">
        <form name="listForm" id="listForm" method="get">
            <div class="d-flex align-items-center">
                <div class="position-relative w-md-100px me-md-2"
                     data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="조회 년도를 변경합니다.">
                    <select name="statsYy" id="statsYy" class="form-select form-select-solid" onchange="Page.statsYyMnth();">
                        <#list yyList![] as yy>
                            <option value="${yy}" label="${yy}년" ${fn.selected(statsYy!, yy)} />
                        </#list>
                    </select>
                </div>
                <div class="position-relative pt-2 me-md-2 pe-2">
                    <h4><label for="statsYy">년</label></h4>
                </div>
                <div class="position-relative w-md-75px me-md-2"
                     data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="조회 월을 변경합니다.">
                    <select name="statsMnth" id="statsMnth" class="form-select form-select-solid" onchange="Page.statsYyMnth();">
                        <#list ["1","2","3","4","5","6","7","8","9","10","11","12"] as mnth>
                            <option value="${mnth}" label="${mnth}월" ${fn.selected(statsMnth!, mnth)} />
                        </#list>
                    </select>
                </div>
                <div class="position-relative w-md-100px pt-2 me-md-2">
                    <h4><label for="statsMnth">월 지출내역</label></h4>
                </div>
            </div>
        </form>
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

    <div class="separator separator-dashed border-gray-300 mt-5 mb-10"></div>

    <div class="card-body pt-0">
        <h3>전체 리스트</h3>
        <div class="card pt-5">
            <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
                <!--begin::Table head-->
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
                <!--end::Table head-->
                <!--begin::Table body-->
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
                                <#if exptr.atchFileDtlId??>
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
                <!--end::Table row-->
                </tbody>
                <!--begin::Table body-->
                <tfoot>
                    <tr class="bg-light">
                        <td colspan="5" class="text-center"></td>
                        <td class="text-end pe-10">
                            ${exptrRptItemSm!?number?string.number}원
                        </td>
                        <td colspan="3"></td>
                    </tr>
                    <!--end::Table row-->
                </tfoot>
            </table>
        </div>

        <!--begin::Menu separator-->
        <div class="separator my-2 mb-10"></div>
        <!--end::Menu separator-->

        <h3>집계</h3>
        <div class="card pt-5">
            <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
                <!--begin::Table head-->
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
                <!--end::Table head-->
                <!--begin::Table body-->
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
                <!--end::Table row-->
                </tbody>
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

    <form name="procForm" id="procForm">
        <input type="hidden" name="postNo" id="postNo" value=""/>
        <input type="hidden" name="prevYn" id="prevYn" value="N"/>
    </form>
</div>

</@layout.layout>