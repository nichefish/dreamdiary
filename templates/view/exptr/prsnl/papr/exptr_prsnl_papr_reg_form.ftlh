<#--
 * 경비지출서(exptr_prsnl_papr) > 등록/수정 화면
 * TODO: Handlebars로 교체하기
 -->

<#import "/layout/layout_default.ftlh" as layout >

<#assign isReg = isReg!false>
<#assign isMdf = isMdf!false>
<#assign isPrevY = ((prevYn!"N") == "Y")>
<#assign isPrevMnthY = (isReg && isPrevY)>

<@layout.layout toolbar=toolbar!"">

<script>
    const Page = (function() {
        return {
            /**
             * Page 객체 초기화
             */
            init: function() {
                Page.itemIdx = ${(post.itemList![])?size};
                <#if isReg!false>
                    Page.setTitle();
                <#elseif isMdf!false>
                    Page.addExptrPrsnlItemList();
                    Page.emptyItemListToggle();        // 추가추가 개수 0개인지 체크
                    commons.util.thousandSeparator(".num");
                    Page.setTotAmt();
                </#if>

                /* initialize form. */
                ExptrPrsnlPapr.initForm();
            },

            /**
             * 제목 자동설정
             */
            setTitle: function() {
                const yy = $("#yy").val();
                const mnth = $("#mnth").val();
                $("#title").val(yy + "년 " + mnth + "월 경비지출서");
            },

            /**
             * 년도/월 등록 가능한지 검사
             */
            validateYyMnth: function() {
                const url = "${Url.EXPTR_PRSNL_PAPR_YY_MNTH_CHCK_AJAX!}";
                const ajaxData = {"yy": $("#yy").val(), "mnth": $("#mnth").val()};
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    const { rsltObj } = res;
                    if (rsltObj === undefined) return;
                    if (rsltObj.cfYn === "Y") {
                        Swal.fire("이미 해당 월 경비 취합이 완료된 상태입니다.")
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    } else {
                        Swal.fire("이미 등록중인 글이 있습니다. 해당 글로 이동합니다.")
                            .then(function() {
                                $("#postNo").val(rsltObj.postNo);
                                commons.util.blockUISubmit("#procForm", "${Url.EXPTR_PRSNL_PAPR_MDF_FORM!}");
                            });
                    }
                });
            },

            /**
             * 추가추가 개수 0개인지 체크
             */
            emptyItemListToggle: function() {
                if ($("#exptr_prsnl_item_reg_div tr").length === 0) {
                    $("#empty_list_div").show();
                } else {
                    $("#empty_list_div").hide();
                }
            },

            /**
             * 지출항목 등록 영역 새로 추가
             */
            addExptrPrsnlItem: function() {
                let reqstItemIdx = Page.itemIdx++;
                commons.util.handlebarsAppend({ "rnum": reqstItemIdx }, "exptr_prsnl_item_reg");

                commons.validate.onlyNumAndComma(".num");
                // input에서 날짜 변경시 날짜 순서대로 행 정렬
                commons.datepicker.singleDatePicker("#exptrDt" + reqstItemIdx, "yyyy-MM-DD", null, function() {
                    // 날짜범위 유효성검사
                    let yy = $("#yy").val();
                    let mnth = $("#mnth").val();
                    let exptrDt = $("#exptrDt"+reqstItemIdx);
                    let mnthBgnDay = commons.date.dateToStr(new Date(yy, mnth - 1, 1));
                    let mnthEndDay = commons.date.dateToStr(new Date(yy, mnth, 0));
                    if (!commons.date.getDateWithinChk(exptrDt.val(), mnthBgnDay, mnthEndDay)) {
                        Swal.fire("날짜가 신청 범위를 벗어났습니다. (" + mnth + "월)");
                        exptrDt.val("");
                        return false;
                    }
                    // 날짜 변경시 순으로 자동 정렬
                    let divTable = document.getElementById('exptr_prsnl_table');
                    (function(divTable, n) {
                        commons.util.sortReqstTable(divTable, 0, "FORWARD");
                    }(divTable, 0));
                });
                commons.util.thousandSeparator(".num");

                Page.emptyItemListToggle();        // 추가추가 개수 0개인지 체크
            },

            /**
             * 사용자 정보 추가항목 수정 영역 기존 정보 목록 추가
             */
            addExptrPrsnlItemList: function() {
                // TODO: API 응답 JSON으로 받아와서 handlebars로 그려버리기
                const url = "${Url.EXPTR_PRSNL_ITEM_LIST_AJAX!}";
                const ajaxData = $("#procForm").serializeArray();
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        Swal.fire({text: res.message});
                        return;
                    }
                    const rsltList = res.rsltList;
                    if (commons.util.isEmpty(rsltList)) return;
                    rsltList.forEach(function(item, idx) {
                        item.rnum = idx;
                        commons.util.handlebarsAppend(item, "exptr_prsnl_item_reg");

                        // 달력 함수
                        commons.datepicker.singleDatePicker("#exptrDt" + idx, "yyyy-MM-DD", item.exptrDt, function() {
                            // 날짜범위 유효성검사
                            let yy = $("#yy").val();
                            let mnth = $("#mnth").val();
                            let exptrDt = $("#exptrDt" + idx);
                            let firstDay = commons.date.dateToStr(new Date(yy, mnth - 1, 1));
                            let lastDay = commons.date.dateToStr(new Date(yy, mnth, 0));
                            if (!commons.date.getDateWithinChk(exptrDt.val(), firstDay, lastDay)) {
                                Swal.fire("날짜가 신청 범위를 벗어났습니다. (" + mnth + "월)");
                                exptrDt.val("");
                                return false;
                            }
                        });
                    });

                    // // 날짜순으로 자동 정렬
                    // let divTable = document.getElementById('exptr_prsnl_table');
                    // (function(table, n) {
                    //     commons.util.sortReqstTable(table, n, "FORWARD");
                    // }(divTable, 0));

                    commons.validate.onlyNumAndComma(".num");
                    Page.emptyItemListToggle();        // 추가추가 개수 0개인지 체크
                });
            },

            /** 영수증 이미지 삭제 */
            rciptDel: function(idx) {
                $("#rciptFile" + idx).empty();
                $("#rciptRegBtn" + idx).show();
                $("#rciptDel" + idx).hide();
                $("#atchFileDtlNo" + idx).val("");
            },

            /** 추가항목 영역 삭제 */
            delExptrPrsnlItemSpan: function(idx) {
                if ($("#exptrPrsnlItemNo" + idx).val() === "") {
                    $("#itemContainer" + idx).remove();
                } else {
                    $("#delYn" + idx).val("Y");
                    $("#itemContainer" + idx).hide();
                }
                Page.emptyItemListToggle();        // 추가추가 개수 0개인지 체크
                Page.setTotAmt();
            },

            /**
             * 사용자 정보 추가항목 영역 합계 구하기
             */
            setTotAmt: function() {
                $("#totAmt").val(commons.util.getReqstItemTotSum("id^=exptrAmt").toLocaleString());
            },
        }
    })();
    document.addEventListener("DOMContentLoaded", function() {
        Page.init();
    });
</script>

<#--begin::Row-->
<div class="row">
    <div class="col-12 d-flex justify-content-end align-items-center mt-4">
        <button type="button" class="btn btn-sm btn-secondary btn-active-primary mx-1"
                onclick="$('#exptr_prsnl_rule_modal').modal('show');"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="작성 안내 팝업을 호출합니다.">
            <i class="fas fa-info-circle"></i>작성 안내
        </button>
        <#include "/view/exptr/prsnl/papr/_exptr_prsnl_papr_rule_modal.ftlh">
    </div>
</div>

<!-- class 속성이 덮어씌워지지 않아서 스타일 지정 -->
<div class="card post" style="margin-top:15px !important;">
    <#--begin::Card body-->
    <div class="card-body">
        <!--begin:Form-->
        <form name="exptrPrsnlRegForm" id="exptrPrsnlRegForm" class="form">
            <input type="hidden" name="postNo" value="${post.postNo!}">
            <input type="hidden" name="mngrYn" id="mngrYn" value="${mngrYn!}">
            <input type="hidden" name="title" id="title" value="${post.title!}">
            <input type="hidden" name="atchFileNo" value="${post.atchFileNo!}">
            <input type="hidden" name="hitCnt" value="${post.hitCnt!0?string.number}">
            <input type="hidden" name="cfYn" value="${post.cfYn!"N"}">

            <div class="card-header border-0 pt-6">
                <div class="d-flex justify-content-left align-items-center">
                    <#if isReg!false>
                        <div class="position-relative min-w-125px me-md-2">
                            <select name="yy" id="yy" class="form-select form-select-solid" onchange="Page.setTitle(); Page.validateYyMnth();">
                                <#if (prevYy)! != (currYy)!>
                                    <option value="${((prevYy!0)?c)?string}"<#if isPrevMnthY> selected="selected"</#if>>
                                        ${((prevYy!0)?c)?string}년
                                    </option>
                                </#if>
                                <option value="${((currYy!0)?c)?string}"<#if !isPrevMnthY> selected="selected"</#if>>
                                    ${((currYy!0)?c)?string}년
                                </option>
                            </select>
                        </div>
                        <div class="position-relative me-md-2">
                            <select name="mnth" id="mnth" class="form-select form-select-solid" onchange="Page.setTitle(); Page.validateYyMnth();">
                                <option value="${((prevMnth!0)?c)?string}"<#if isPrevMnthY> selected="selected"</#if>>
                                    ${prevMnth!}월
                                </option>
                                <option value="${((currMnth!0)?c)?string}"<#if !isPrevMnthY> selected="selected"</#if>>
                                    ${currMnth!}월
                                </option>
                            </select>
                        </div>
                        <div class="position-relative w-md-200px pt-2 me-md-2">
                            <h4>경비지출서</h4>
                        </div>
                    <#elseif isMdf!false>
                        <input type="hidden" name="yy" id="yy" value="${post.yy!}">
                        <input type="hidden" name="mnth" id="mnth" value="${post.mnth!}">
                        <div class="position-relative me-md-2">
                            <div class="fs-2 fw-bold col-form-label text-center">
                                <h4>${post.title!}</h4>
                            </div>
                        </div>
                    </#if>
                </div>
                <div class="card-title text-end">
                    <div class="d-flex align-items-center position-relative my-1">
                    </div>
                </div>
                <div class="card-toolbar">
                    <!--begin::Toolbar-->
                    <div class="d-flex justify-content-end" data-kt-user-table-toolbar="base">
                        <button type="button" class="btn btn-sm btn-primary"
                                onclick="Page.addExptrPrsnlItem();"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="지출내역 행을 추가합니다.">
                            <i class="fas fa-plus"></i>지출내역 추가
                        </button>
                    </div>
                    <!--end::Toolbar-->
                </div>
            </div>
            <#if isMdf!false>
                <div class="row ps-10 mt-2">
                    <div class="col-lg-1 col-3 d-flex align-items-center">
                        <label class="text-gray-700 fs-6 fw-bolder cursor-help"
                               data-bs-toggle="tooltip" data-bs-placement="top" title="미설정시 수정 일자가&#10;기존 일자로 유지됩니다.&#10;(기본 : ON)">
                            수정일자 변경
                            <i class="bi bi-question-circle"></i>
                        </label>
                    </div>
                    <div class="col-lg-2 col-9 d-flex align-items-center">
                        <@component.checkbox post "managtDtUpdtYn" "변경//미변경", "blue//gray", "defaultOn" />
                    </div>
                </div>
            </#if>
            <!--begin::Separator-->
            <div class="separator separator-dashed border-gray-300 my-8"></div>
            <!--end::Separator-->
            <div class="table-responsive">
                <#--begin::Table-->
                <table class="table align-middle fs-6 gy-5 hoverTable" id="exptr_prsnl_table">
                    <#--begin::Table head-->
                    <thead>
                        <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 fw-bolder text-muted border-bottom">
                            <th class="col-xl-2 col-2 min-w-125px max-w-125px text-center wb-keepall">날짜</th>
                            <th class="col-xl-1 col-2 min-w-100px text-center wb-keepall" style="width:9% !important">
                                <span class="hidden-table">지출</span>구분
                            </th>
                            <th class="col-xl-2 col-2 min-w-100px text-center wb-keepall">
                                <span class="hidden-table">지출</span>내역
                            </th>
                            <th class="col-xl-1 col-2 min-w-125px text-center wb-keepall">금액</th>
                            <th class="col-xl-5 col-4 min-w-250px text-center wb-keepall">
                                <span class="hidden-table">인원 및 </span>비고
                            </th>
                            <th class="col-1 min-w-50px text-center wb-keepall">
                                영수증<span class="hidden-table"> 첨부</span>
                            </th>
                            <th class="col-2 min-w-50px text-center wb-keepall"></th>
                        </tr>
                    </thead>
                    <#--begin::Table body-->
                    <tbody id="exptr_prsnl_item_reg_div">
                        <!-- 추가추가 -->
                    </tbody>
                    <#--begin::Table body-->
                    <tbody id="empty_list_div">
                        <tr>
                            <td colspan="7" class="text-center">지출 항목을 추가해주세요.</td>
                        </tr>
                    </tbody>
                    <#--begin::Table body-->
                    <tbody id="sumDiv">
                        <tr class="bg-light">
                            <td colspan="3" class="text-center"></td>
                            <td>
                                <div class="d-flex align-items-center">
                                    <input type="text" id="totAmt" class="form-control form-control-solid number text-end"
                                           value="" placeholder="금액(원)" readonly>
                                    <span class="menu-icon mx-2 vertical-middle">원</span>
                                </div>
                            </td>
                            <td colspan="3" class="text-center"></td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <!-- 추가추가 -->
        </form>
    </div>
    <#--begin::Card footer-->
    <div class="card-footer d-flex justify-content-end py-6 px-9">
        <#--페이지:: 버튼: 저장하기-->
        <@component.post_btn_submit "ExptrPrsnlPapr.submit();" />
        <#--페이지:: 버튼: 관리 화면으로 돌아가기-->
        <#if mngrYn! == "Y">
            <button type="button" class="btn btn-light"
                    onclick="ExptrPrsnlPapr.stats();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.prev"/>">
                <i class="bi bi-list"></i>뒤로가기
            </button>
        <#else>
            <#--페이지:: 버튼: 목록으로 가기-->
            <@component.post_btn_toList "ExptrPrsnlPapr.list();" />
        </#if>
    </div>
    <!--end::Card footer-->
</div>
<!--end::Card-->

<form name="procForm" id="procForm" method="get">
    <input type="hidden" name="postNo" id="postNo" value="${post.postNo!}"/>
</form>

<#include "/view/exptr/prsnl/papr/_exptr_prsnl_item_reg_template.hbs">
<#include "/view/exptr/prsnl/papr/_exptr_prsnl_item_reg_rcipt_template.ftlh">
<#include "/view/exptr/prsnl/papr/_exptr_prsnl_item_rject_template.ftlh">

</@layout.layout>