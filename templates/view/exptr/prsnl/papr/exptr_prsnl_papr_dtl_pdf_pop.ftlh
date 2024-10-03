<#--
  -- 경비지출서(exptr_prsnl_papr) > 상세 팝업 (출력용)
  -->

<#import "/layout/layout_without_sidebar.ftlh" as layout >

<@layout.layout toolbar=toolbar!"">

<script>
    const Page = (function() {
        return {
            /**
             * Page 객체 초기화
             */
            init: function() {
                Page.emptyListToggle();        // 추가추가 개수 0개인지 체크
                $("#totAmt").text(commons.util.getReqstItemTotSum("id^=exptrAmtSpan").toLocaleString());
            },
            /** 인쇄 */
            print: function() {
                window.print();
                window.close();
            },
            /** 추가추가 개수 0개인지 체크 */
            emptyListToggle: function() {
                if ($("#exptr_prsnl_item_reg_div tr").length === 0) {
                    $("#empty_list_div").show();
                } else {
                    $("#empty_list_div").hide();
                }
            }
        }
    })();
    document.addEventListener("DOMContentLoaded", function() {
        Page.init();
    });
</script>

<div class="card post page">
    <#--begin::Card body-->
    <div class="card-body">
        <#--begin::title-->
        <div class="mb-0">
            <div class="d-flex align-items-center mb-9">
                <#--begin::Title-->
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
            <div class="d-flex flex-stack flex-wrap mb-3">
                <#--begin::Author-->
                <div class="d-flex align-items-center">
                    <#--begin::Name-->
                    <div class="d-flex flex-column align-items-start justify-content-center">
                        <span class="text-gray-800 fs-6 fw-bold lh-1 mb-2">${post.regstrNm!"-"}</span>
                        <span class="text-muted fs-7 lh-1">${post.regDt!}</span>
                    </div>
                    <!--end::Name-->
                </div>
                <!--end::Author-->
                <#--begin::Info-->
                <div class="d-flex align-items-center py-1">
                    <button type="button" class="btn btn-sm btn-primary px-4 me-2" onclick="Page.print();">
                        <i class="bi bi-printer"></i>출력
                    </button>
                </div>
                <!--end::Info-->
            </div>
        </div>

        <div class="separator separator-dashed border-gray-300 my-8"></div>

        <#--begin::cn-->
        <table class="table align-middle table-row-dashed fs-small gy-5" id="kt_customers_table">
            <#--begin::Table head-->
            <thead>
                <tr class="text-start text-muted fw-bolder fs-7 text-uppercase gs-0">
                    <th class="col-2 min-w-100px text-center wb-keepall">날짜</th>
                    <th class="col-2 min-w-100px text-center wb-keepall">지출구분</th>
                    <th class="min-w-150px text-center wb-keepall">지출내역</th>
                    <th class="w-12 min-w-75px text-center wb-keepall">금액</th>
                    <th class="min-w-150px text-center wb-keepall">인원 및 비고</th>
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
            <#--begin::Table body-->
            <tbody id="sumDiv">
                <tr class="bg-light">
                    <td colspan="3" class="text-center">
                    <td colspan="1" class="text-end pe-6">
                        합계 : <span id="totAmt">-</span>원
                    </td>
                    <td colspan="3" class="text-center">
                </tr>
            </tbody>
        </table>
    </div>
</div>

<form name="procForm" id="procForm" method="get">
    <input type="hidden" name="postNo" id="postNo" value="${post.postNo!}"/>
</form>

</@layout.layout>