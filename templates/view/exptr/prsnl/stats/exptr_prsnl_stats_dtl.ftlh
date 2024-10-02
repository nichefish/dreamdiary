<#--
 * 경비 관리 > 경비지출누적집계 > 상세 화면
 -->

<#import "/layout/layout.ftlh" as layout >

<@layout.layout toolbar=toolbar!"">

<script>
    const Page = (function() {
        return {
            init: function () {
                Page.emptyListToggle();        // 추가추가 개수 0개인지 체크
                $("#totAmt").text(commons.util.getReqstItemTotSum("id^=exptrAmtSpan").toLocaleString());

                <#if (post.cfYn!"N") == "Y">
                    $(".rject").attr("onclick", "return false");
                </#if>
            },
            /** 추가추가 개수 0개인지 체크 */
            emptyListToggle: function() {
                if ($("#exptr_prsnl_item_reg_div tr").length === 0) {
                    $("#empty_list_div").show();
                } else {
                    $("#empty_list_div").hide();
                }
            },
            /** PDF 인쇄 화면 출력 */
            pdfPop: function() {
                const url = "${Url.EXPTR_PRSNL_PAPR_PDF_POP!}?postNo="+$("#postNo").val();
                const popupNm = '경비지출서 팝업';
                const options = 'width=1280,height=1440,top=0,left=270';
                commons.util.popup(url, popupNm, options);
            },
            /** 수정 화면 이동 */
            mdfForm: function() {
                commons.util.blockUISubmit("#procForm", "${Url.EXPTR_PRSNL_PAPR_MDF_FORM!}");
            },
            /** 영수증 원본 제출 체크 */
            orgnlRciptAjax: function(exptrPrsnlItemNo, orgnlRciptYn) {
                Swal.fire({
                    text: "영수증 원본 제출상태를 변경하시겠습니까?",
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.EXPTR_PRSNL_ITEM_ORGNL_RCIPT_AJAX!}";
                    const ajaxData = { "postNo": '${post.postNo!}', "exptrPrsnlItemNo": exptrPrsnlItemNo, "orgnlRciptYn": orgnlRciptYn };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 지출항목 반려 팝업 호출 */
            rjectModal: function(idx) {
                $("#modal_idx").val(idx);
                $("#rject_resn_modal").modal("show");
            },
            /** 취합완료 처리 (Ajax) */
            cfAjax: function() {
                Swal.fire({
                    html: <@spring.message "view.cnfm.stacnt"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.EXPTR_PRSNL_STATS_CF_AJAX!}";
                    const ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) Page.stats();
                            });
                    });
                });
            },
            /** 삭제 처리 (ajax) */
            delAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.EXPTR_PRSNL_PAPR_DEL_AJAX!}";
                    const ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) Page.stats();
                            });
                    });
                });
            },
            /** 목록 화면 이동 */
            stats: function() {
                commons.util.blockUIReplace("${Url.EXPTR_PRSNL_STATS_PAGE!}");
            },
            /** PDF 다운로드 */
            pdfDownload: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.download"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    commons.util.blockUIFileDownload();
                    $("#procForm").attr("action", "${Url.EXPTR_PRSNL_PAPR_RCIPT_PDF_DOWNLOAD!}").submit();
                });
            },
            /** 지출항목 반려 처리 (Ajax) */
            rjectAjax: function(idx, rjectYn) {
                $("#exptrPrsnlItemNo").val($("#exptrPrsnlItemNo"+idx).val());
                $("#rjectYn").val(rjectYn);
                if (rjectYn === "Y") $("#rjectResn").val($("#rjectResnShow").val());
                const url = "${Url.EXPTR_PRSNL_STATS_RJECT!}";
                const ajaxData = $("#procForm").serializeArray();
                commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rslt) commons.util.blockUIReload();
                        });
                });
            }
        }
    })();

    $(document).ready(function() {
        Page.init();
    });
</script>

<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <#--begin::title-->
        <div class="mb-0">
            <div class="d-flex align-items-center mb-9">
                <#--begin::title-->
                <div class="col-12 d-flex">
                    <h1 class="fs-2x fw-bolder text-gray-900 mb-0 me-1">
                        ${post.title}
                    </h1>
                    <div class="px-5 me-5 col-form-label">
                        <label class="badge badge-<#if (post.cfYn!"N") == "Y">success<#else>secondary</#if>">
                            <#if (post.cfYn!"N") == "Y">취합완료<#else>미취합</#if>
                        </label>
                    </div>
                </div>
            </div>

            <#--페이지:: 헤더: 메타 (+프린트) 영역-->
            <@component.post_header_meta_with_print post />
        </div>

        <div class="separator my-2"></div>

        <#--begin::cn-->
        <div class="table-responsive-sm">
            <#--begin::Table-->
            <table class="table align-middle table-row-dashed fs-small gy-5">
                <#--begin::Table head-->
                <thead>
                    <tr class="text-start text-muted fw-bolder fs-7 text-uppercase gs-0">
                        <th class="col-xl-1 col-2 text-center wb-keepall w-10">날짜</th>
                        <th class="col-xl-1 col-2 text-center wb-keepall w-10">지출구분</th>
                        <th class="min-w-175px text-center wb-keepall">지출내역</th>
                        <th class="min-w-100px text-center wb-keepall">금액</th>
                        <th class="min-w-200px text-center wb-keepall">인원 및 비고</th>
                        <th colspan="2" class="min-w-175px text-center wb-keepall w-20">
                            영수증
                            <a href="javascript:void(0);" class="badge badge-primary card mt-4 mx-1"
                               onclick="Page.pdfDownload();"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="영수증 사본 이미지 묶음을&#10;PDF 파일로 다운로드합니다.">
                                <i class="bi bi-file-earmark-pdf text-light pe-1"></i>PDF 다운로드
                            </a>
                        </th>
                        <th class="min-w-120px text-center wb-keepall">처리</th>
                    </tr>
                </thead>
                <#--begin::Table body-->
                <tbody id="exptr_prsnl_item_reg_div">
                    <#list post.itemList![] as exptr>
                        <tr<#if (exptr.rjectYn!"N") == "Y"> class="bg-light-danger strikethrough text-muted"</#if>>
                            <td class="text-center">
                                ${exptr.exptrDt!}
                            </td>
                            <td class="text-center">
                                ${exptr.exptrTyNm!}
                            </td>
                            <td class="text-center">
                                ${exptr.exptrCn!}
                            </td>
                            <td class="text-end pe-6">
                                <#if (exptr.rjectYn!"N") != "Y">
                                    <input type="hidden" id="exptrAmtSpan${exptr?index}" value="${exptr.exptrAmt!}">
                                </#if>
                                ${exptr.exptrAmt!?number?string.number}원
                            </td>
                            <td class="text-start ps-6">
                                ${exptr.exptrRm!}
                            </td>
                            <td class="text-end">
                                <#if exptr.atchFileDtlNo??>
                                    <a href="javascript:void(0);" class="text-primary fs-6 opacity-75-hover"
                                       onclick="commons.util.fileDownload('', '${exptr.atchFileDtlNo!}');"
                                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.file.download"/>">
                                        다운로드
                                        <span class="svg-icon svg-icon-2x svg-icon-primary" >
                                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                                                <path opacity="0.3" d="M19 22H5C4.4 22 4 21.6 4 21V3C4 2.4 4.4 2 5 2H14L20 8V21C20 21.6 19.6 22 19 22ZM13 15.4V10C13 9.4 12.6 9 12 9C11.4 9 11 9.4 11 10V15.4H8L11.3 18.7C11.7 19.1 12.3 19.1 12.7 18.7L16 15.4H13Z" fill="black"/>
                                                <path d="M15 8H20L14 2V7C14 7.6 14.4 8 15 8Z" fill="black"/>
                                            </svg>
                                        </span>
                                    </a>
                                <#else>
                                    <span class="text-danger fs-6">(업로드<i class="bi bi-x text-danger"></i>)</span>
                                </#if>
                            </td>
                            <td class="text-center">
                                <input type="hidden" id="exptrPrsnlItemNo${exptr?index}" value="${exptr.exptrPrsnlItemNo!}">
                                <input type="hidden" id="orgnlRciptYn${exptr?index}" value="${(exptr.orgnlRciptYn)!"N"}">
                                <span id="orgnlRciptYn${exptr?index}Span" class="text-${exptr.orgnlRciptClass!"muted"} cursor-pointer"
                                      onclick="Page.orgnlRciptAjax('${exptr.exptrPrsnlItemNo}', '<#if (exptr.orgnlRciptYn!"N") == "N">Y<#else>N</#if>');"
                                      data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                                      title="영수증 원본 제출여부를&#10;변경합니다.">
                                    <#if (exptr.orgnlRciptYn!"N") == "Y">
                                        원본 제출완료<i class="bi bi-check text-${exptr.orgnlRciptClass!"muted"} fs-8"></i>
                                    <#elseif (exptr.orgnlRciptYn!"N") == "X">
                                        불필요<i class="bi bi-credit-card opacity-hover"></i>
                                    <#else>
                                        (원본 미제출)<i class="bi bi-x text-${exptr.orgnlRciptClass!"muted"} fs-8"></i>
                                    </#if>
                                </span>
                                <#if (exptr.orgnlRciptYn!"N") == "N">
                                    <span class="badge badge-sm badge-light badge-active-primary badge-outlined opacity-50 fs-7 ms-2 cursor-pointer"
                                          onclick="Page.orgnlRciptAjax('${exptr.exptrPrsnlItemNo}', 'X');"
                                          data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                                          title="영수증 원본이 없는 경우&#10;체크합니다.&#10;(ex.전자거래)">
                                       <i class="bi bi-credit-card opacity-hover"></i>
                                       <i class="bi bi-three-dots opacity-hover"></i>
                                    </span>
                                </#if>
                            </td>
                            <td>
                                <#if ((exptr.rjectYn!"N") == "N" && (post.cfYn!"N") != "Y")>
                                    <label class="badge badge-secondary cursor-pointer rject"
                                           onclick="Page.rjectModal('${exptr?index}', 'Y');"
                                           data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="해당 지출건에 대한 경비지급을 반려합니다.">
                                        반려하기
                                    </label>
                                </#if>
                            </td>
                        </tr>
                        <#if (exptr.rjectYn!"N") == "Y">
                            <tr class="bg-light-danger">
                                <td class="py-2"></td>
                                <td colspan="6" class="py-2">반려사유: ${exptr.rjectResn!}</td>
                                <td class="py-2">
                                    <label class="badge badge-secondary cursor-pointer rject" onclick="rjectAjax('${exptr?index}', 'N');">
                                        반려취소
                                    </label>
                                </td>
                            </tr>
                        </#if>
                    </#list>
                </tbody>
                <#--begin::Table body-->
                <tbody id="empty_list_div">
                    <tr>
                        <td colspan="8" class="text-center">지출항목이 없습니다.</td>
                    </tr>
                </tbody>
                <#--begin::Table body-->
                <tbody id="sumDiv">
                    <tr class="bg-light">
                        <td colspan="3" class="text-center"></td>
                        <td class="text-end pe-6">
                            합계 : <span id="totAmt">-</span>원
                        </td>
                        <td colspan="4" class="text-center"></td>
                    </tr>
                </tbody>
            </table>
        </div>

        <div class="separator separator-dashed border-gray-300 my-8"></div>

        <#--begin::meta-->
        <div class="mb-0">
            <#--페이지:: 열람자 표시 영역-->
            <@component.post_viewer_area post />
        </div>
    </div>
    <#--begin::Card footer-->
    <div class="card-footer">
        <#--버튼 영역-->
        <div class="d-flex justify-content-end">
            <#if (post.cfYn!"N") != "Y">
                <button type="button" class="btn btn-sm btn-primary me-2"
                        onclick="Page.cfAjax();"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="경비지출서를 취합 처리합니다.&#10;취합 처리 후에는&#10;관리자만 수정할 수 있습니다.">
                    취합처리
                </button>
            </#if>
            <button type="button" class="btn btn-sm btn-light btn-active-primary me-2"
                    onclick="Page.mdfForm();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.mdf"/>">
                <i class="bi bi-pencil-square"></i>수정
            </button>
            <button type="button" class="btn btn-sm btn-light btn-active-danger me-2"
                    onclick="Page.delAjax();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.del"/>">
                <i class="bi bi-trash"></i>삭제
            </button>
            <button type="button" class="btn btn-sm btn-light"
                    onclick="Page.stats();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.prev"/>">
                <i class="bi bi-list"></i>뒤로가기
            </button>
        </div>
    </div>
    <!--end::Card footer-->
</div>
<!--end::Card-->

<div class="separator my-2"></div>

<div class="card">
    <#--페이지:: 댓글 영역-->
    <#include "/view/_component/comment/_comment_page_area.ftlh">
</div>

<form name="procForm" id="procForm" method="get">
    <input type="hidden" name="postNo" id="postNo" value="${post.postNo!}" />
    <input type="hidden" name="exptrPrsnlItemNo" id="exptrPrsnlItemNo" />
    <input type="hidden" name="orgnlRciptYn" id="orgnlRciptYn" />
    <input type="hidden" name="rjectYn" id="rjectYn" />
    <input type="hidden" name="rjectResn" id="rjectResn" />
    <input type="hidden" name="mngrYn" value="Y">
</form>

<#include "/view/exptr/prsnl/stats/_exptr_prsnl_stats_dtl_rject_resn_modal.ftlh">

</@layout.layout>