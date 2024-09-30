<#--
  -- 경비지출서(exptr_prsnl_papr) 상세 화면
  -->

<#import "/layout/layout.ftlh" as layout >

<@layout.layout toolbar=toolbar!"">

<script>
    const Page = function() {
        return {
            init: function () {
                Page.emptyListToggle();        // 추가추가 개수 0개인지 체크
                $("#totAmt").text(commons.util.getReqstItemTotSum("id^=exptrAmtSpan").toLocaleString());
            },
            /** 관리 페이지로 이동 */
            statsPage: function() {
                commons.util.blockUISubmit("#procForm", "${Url.EXPTR_PRSNL_STATS_DTL!}");
            },
            /** 추가추가 개수 0개인지 체크 */
            emptyListToggle: function() {
                if ($("#exptr_prsnl_item_reg_div tr").length == 0) {
                    $("#empty_list_div").show();
                } else {
                    $("#empty_list_div").hide();
                }
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
            /** PDF 인쇄 화면 출력 */
            pdfPop: function() {
                const url = "${Url.EXPTR_PRSNL_PAPR_PDF_POP!}?postNo="+$("#postNo").val();
                const popupNm = '경비지출서 팝업';
                const options = 'width=1280,height=1440,top=0,left=270';
                commons.util.popup(url, popupNm, options);
            },
            /** 개별 영수증 PDF 취합 다운로드 */
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
        }
    }();

    $(document).ready(function() {
        Page.init();
    });
</script>

<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <#--begin::title-->
        <div class="mb-0">
            <#--begin::Head-->
            <div class="d-flex align-items-center mb-9">
                <#--begin::title-->
                <div class="col-12 d-flex">
                    <h1 class="fs-2x fw-bolder text-gray-900 mb-0 me-1">
                        ${post.title}
                    </h1>
                    <div class="px-5 me-5 col-form-label">
                        <label class="text-<#if (post.cfYn!"N") == "Y">success<#else>muted</#if>">
                            <#if (post.cfYn!"N") == "Y">
                                취합완료<i class="bi bi-check"></i>
                            <#else>
                                (미취합)
                            </#if>
                        </label>
                    </div>
                </div>
            </div>
            <#--페이지:: 헤더: 메타 (+프린트) 영역-->
            <@component.post_header_meta_with_print post />
        </div>

        <div class="separator separator-dashed border-gray-300 my-8"></div>

        <#--begin::cn-->
        <div class="table-responsive-sm min-h-150px">
            <#--begin::Table-->
            <table class="table align-middle table-row-dashed fs-small gy-5" id="kt_customers_table">
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
                            <a href="javascript:void(0);" class="badge badge-primary mx-1"
                               onclick="Page.pdfDownload();"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="영수증 사본 이미지 묶음을&#10;PDF 파일로 다운로드합니다.">
                                <i class="bi bi-file-earmark-pdf text-light pe-1"></i>PDF 다운로드
                            </a>
                        </th>
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
                            <td class="text-end w-10">
                                <#if exptr.atchFileDtlNo??>
                                    <a href="javascript:void(0);" class="text-primary opacity-75-hover"
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
                                    <#if (post.isRegstr!false) || (isMngr)>
                                        <span class="text-danger">(업로드<i class="bi bi-x text-danger"></i>)</span>
                                        <span class="badge badge-primary ms-1 p-2 text-danger cursor-pointer blink-slow fs-4"
                                              onclick="Rcipt.uploadDtlAjax('${exptr.exptrPrsnlItemNo!}')">
                                            <i class="bi bi-cloud-arrow-up"></i>
                                        </span>
                                    <#else>
                                        <span class="text-danger">(미첨부)</span>
                                    </#if>
                                </#if>
                            </td>
                            <td class="text-start w-12">
                                <span class="text-${exptr.orgnlRciptClass!"muted"} fw-normal <#if (post.isRegstr!false) || (isMngr)>cursor-pointer</#if>"
                                    <#if (post.isRegstr!false) || (isMngr)>
                                      onclick="Page.orgnlRciptAjax('${exptr.exptrPrsnlItemNo}', '<#if (exptr.orgnlRciptYn!"N") == "N">Y<#else>N</#if>');"
                                      data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="영수증 원본 제출여부를&#10;변경합니다."
                                    </#if>>
                                    <#if (exptr.orgnlRciptYn!"N") == "Y">
                                        원본 제출완료<i class="bi bi-check text-${exptr.orgnlRciptClass!"muted"} fs-8"></i>
                                    <#elseif (exptr.orgnlRciptYn!"N") == "X">
                                        불필요<i class="bi bi-credit-card opacity-hover ms-2"></i>
                                    <#else>
                                        (원본 미제출)<i class="bi bi-x text-${exptr.orgnlRciptClass!"muted"} fs-8"></i>
                                    </#if>
                                </span>
                                <#if ((post.isRegstr!false) || (isMngr)) && (exptr.orgnlRciptYn!"N") == "N">
                                    <span class="badge badge-sm badge-light badge-active-primary badge-outlined opacity-50 ms-2 cursor-pointer"
                                          onclick="Page.orgnlRciptAjax('${exptr.exptrPrsnlItemNo}', 'X');"
                                          data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                                          title="영수증 원본이 없는 경우&#10;체크합니다.&#10;(ex.전자거래)">
                                       <i class="bi bi-credit-card opacity-hover"></i>
                                       <i class="bi bi-three-dots opacity-hover"></i>
                                    </span>
                                </#if>
                            </td>
                        </tr>
                        <#if (exptr.rjectYn!"N") == "Y">
                            <tr class="bg-light-danger">
                                <td class="py-2"></td>
                                <td colspan="6" class="py-2 ps-10">반려사유: ${exptr.rjectResn!}</td>
                            </tr>
                        </#if>
                    </#list>
                </tbody>
                <#--begin::Table body-->
                <tbody id="empty_list_div">
                    <tr>
                        <td colspan="7" class="text-center">지출항목이 없습니다.</td>
                    </tr>
                </tbody>
                <tfoot id="sumDiv">
                    <tr class="bg-light">
                        <td colspan="3" class="text-center">
                        <td colspan="1" class="text-end pe-6">
                            합계 : <span id="totAmt">-</span>원
                        </td>
                        <td colspan="3" class="text-center">
                    </tr>
                </tfoot>
            </table>
        </div>

        <div class="separator separator-dashed border-gray-300 my-8"></div>

        <#--begin::meta-->
        <div class="mb-0">
            <#--페이지:: 열람자 표시 영역-->
            <@component.post_viewer_area post />

            <#--페이지:: 관리자 취합 영역-->
            <#if isMngr>
                <div class="row mb-5">
                    <div class="col-xl-2">
                        <div class="col-form-label fs-6 fw-bold mx-5">
                            <label for="cfYn">취합여부</label>
                        </div>
                    </div>
                    <div class="col-xl-10">
                        <div class="form-check form-switch mt-2 form-check-custom form-check-solid">
                            <#if post.cfYn == "N">
                                <span class="text-muted me-10">
                                    미취합<i class="bi bi-dash text-muted"></i>
                                </span>
                                <button class="btn btn-sm btn-primary"
                                        onclick="Page.statsPage();"
                                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="경비지출서 관리자 페이지로&#10;이동합니다.">
                                    관리 페이지로 이동
                                </button>
                            <#else>
                                <button class="btn btn-sm btn-success" disabled>
                                    취합완료<i class="bi bi-check"></i>
                                </button>
                                <span class="ms-3 text-success">취합 완료되었습니다.</span>
                            </#if>
                        </div>
                    </div>
                </div>
            </#if>
        </div>
    </div>
    <#--begin::Card footer-->
    <div class="card-footer">
        <#--버튼 영역-->
        <div class="d-flex mt-10 justify-content-end">
            <!-- 등록자만 수정 가능, 등록자+관리자도 삭제 가능 -->
            <#if (post.isRegstr!false && (post.cfYn!"N") != "Y")>
                <button type="button" class="btn btn-sm btn-light btn-active-primary me-2"
                        onclick="ExptrPrsnlPapr.mdfForm();"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.mdf"/>">
                    수정
                </button>
            </#if>
            <#if (((post.isRegstr)!false || isMngr) && (post.cfYn!"N") != "Y")>
                <button type="button" class="btn btn-sm btn-light btn-active-danger me-2"
                        onclick="ExptrPrsnlPapr.delAjax();"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.del"/>">
                    <i class="bi bi-trash"></i>삭제
                </button>
            </#if>
            <#--페이지:: 버튼: 목록으로 가기-->
            <@component.post_btn_toList "ExptrPrsnlPapr.list();" />
        </div>
    </div>

    <#--페이지:: 댓글 영역-->
    <#include "/view/_component/comment/_comment_area.ftlh">
</div>

<form name="procForm" id="procForm" method="get">
    <input type="hidden" name="postNo" id="postNo" value="${post.postNo!}"/>
</form>

<#include "/view/exptr/prsnl/papr/_exptr_prsnl_item_reg_rcipt_template.ftlh">
<#include "/view/exptr/prsnl/papr/_exptr_prsnl_item_rject_template.ftlh">

</@layout.layout>