<#--
  -- 휴가계획서 > 등록 화면
  -->
<#import "/layout/layout.ftlh" as layout >

<script src="/metronic/assets/plugins/custom/tinymce/tinymce.bundle.js?${releaseDate!}"></script>
<script type="text/javascript" src="/js/commons/commons-tinymce.js?${releaseDate!}"></script>
<script type="text/javascript" src="/lib/prism/js/prism.js?${releaseDate!}"></script>

<#assign isReg = (isReg!false)>
<#assign isMdf = (isMdf!false)>

<@layout.layout toolbar=toolbar!"">

<script>
    const Page = function() {
        return {
            init: function () {
                // 첨부파일 영역 0개인지 체크
                AtchFile.atchFileListToggle();

                commons.validate.noSpaces(".no-space");        // 공백 자동 제거

                // 잔디발송여부 클릭시 글씨 변경
                commons.util.chckboxLabel("jandiYn", "발송//미발송", "#0095E8//gray", function () {
                    $("#trgetTopicSpan").show();
                }, function () {
                    $("#trgetTopicSpan").hide();
                });
                <#if isReg>
                    $("#jandiYn").trigger("click");
                </#if>

                /** jquery :: validation */
                $(function () {
                    $("#vcatnPaprRegForm").validate({
                        submitHandler: function () {
                            Swal.fire({
                                text: <#if isMdf><@spring.message "view.cnfm.mdf"/><#else><@spring.message "view.cnfm.reg"/></#if>,
                                showCancelButton: true,
                            }).then(function(result) {
                                if (!result.value) return;
                                Page.<#if isReg>reg</#if><#if isMdf>mdf</#if>Ajax();
                            });
                        },
                        errorPlacement: function (error, element) {
                            commons.validate.errorSpan(error, element);        // 공통 함수로 분리
                        },
                    });
                });

                <#if isReg>
                    /** 휴가 추가 폼 기본 뿌리기 */
                    Page.addVcatnInfoRegItem();
                <#elseif isMdf>
                    <#if (post.schdulList)?has_content>
                        Page.addVcatnInfoItemList();      // 기존 항목에 대하여 행 쭈루룩 추가
                    </#if>
                </#if>
            },
            /** 반차 선택 시 종료일 안보이게 */
            delEndDt: function(idx) {
                let result = $("#vcatnCd" + idx).children('option:selected').val();
                if (result === "AM_HALF" || result === "PM_HALF") {
                    $("#lineDiv"+idx).css("visibility", "hidden");
                    $("#endDtDiv"+idx).css("visibility", "hidden");
                    $("#endDt"+idx).val($("#bgnDt"+idx).val());
                    $("#endDt"+idx+"Span").empty().css("visibility", "hidden");
                } else {
                    $("#lineDiv"+idx).css("visibility", "visible");
                    $("#endDtDiv"+idx).css("visibility", "visible");
                    $("#endDt"+idx+"Span").css("visibility", "visible");
                }
            },
            /** 시작일 이전 날짜 선택 불가 */
            noBefore: function(beginSelectorStr, endSelectorStr, idx) {
                let result = commons.date.dateCompare(beginSelectorStr, endSelectorStr);
                let selected = $("#vcatnCd" + idx).children('option:selected').val();
                if (selected === "AM_HALF" || selected === "PM_HALF") {
                    $(endSelectorStr).val($(beginSelectorStr).val());
                } else if (result === false && $(endSelectorStr).val() !== "") {
                    Swal.fire("휴가일을 다시 설정해 주세요.");
                    $(endSelectorStr).val("");
                }
            },
            /** 등록/수정 처리(Ajax) */
            <#if isReg>reg</#if><#if isMdf>mdf</#if>Ajax: function() {
                const url = "<#if isReg>${SiteUrl.VCATN_PAPR_REG_AJAX!}</#if><#if isMdf>${SiteUrl.VCATN_PAPR_MDF_AJAX!}</#if>";
                let ajaxData = new FormData($("#vcatnPaprRegForm")[0]);
                commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rslt) Page.list();
                        });
                });
            },
            /** submit */
            submit: function() {
                $("#vcatnPaprRegForm").submit();
            },
            /** 목록 화면 이동 */
            list: function() {
                const listUrl = "${SiteUrl.VCATN_PAPR_LIST!}<#if isMdf>?isBackToList=Y</#if>";
                commons.util.blockUIReplace(listUrl);
            },
            /**
             * 휴가등록 부분 추가
             */
            addVcatnInfoRegItem: function() {
                let reqstItemIdx = commons.util.getReqstItemIdx("span", "id^=vcatnItemId", "vcatnItemId");// elmt, selector, elmtId
                let tableTmpl = "<div class='card shadow-sm mb-5' style='width:100%' id='itemContainer" + reqstItemIdx + "'>" + $("#vcatn_reg_item_template").html() + "</div>";
                let str = tableTmpl.replace(/__INDEX__/g, reqstItemIdx);
                $("#vcatnListDiv").append(str);
                commons.datepicker.singleDatePicker("#bgnDt"+reqstItemIdx, "yyyy-MM-DD", null, function() {
                    Page.delEndDt(reqstItemIdx);
                    Page.noBefore("#bgnDt"+reqstItemIdx, "#endDt"+reqstItemIdx, reqstItemIdx);
                });
                commons.datepicker.singleDatePicker("#endDt"+reqstItemIdx, "yyyy-MM-DD", null, function() {
                    if ($("#endDt"+reqstItemIdx).val() !== "") {
                        Page.noBefore("#bgnDt"+reqstItemIdx, "#endDt"+reqstItemIdx, reqstItemIdx);
                    }
                });
            },
            /**
             * 사용자 정보 추가항목 영역 기존 정보 목록 추가
             */
            addVcatnInfoItemList: function() {
                <#if vcatn?? && (post.schdulList)?has_content>
                let reqstItemIdx, tableTmpl, str, vcatnCdSelectDiv;
                <#list post.schdulList![] as item>
                reqstItemIdx = commons.util.getReqstItemIdx("span", "id^=vcatnItemId", "vcatnItemId");		// elmt, selector, elmtId
                tableTmpl = "<div class='row shadow-sm' id='itemContainer" + reqstItemIdx + "'>" + $("#vcatn_reg_item_template").html() + "</div>";
                str = tableTmpl.replace(/__INDEX__/g, reqstItemIdx);
                $("#vcatnListDiv").append(str);

                // 휴가사유 세팅
                $("#vcatnResn" + reqstItemIdx).val(`${(item.vcatnResn)!}`);
                // 휴가분류 selectbox 세팅
                vcatnCdSelectDiv = $("#vcatnCdSelectDiv" + reqstItemIdx).children('select').children('option');
                vcatnCdSelectDiv.each(function (idx, elmt) {
                    if (elmt.value === "${(item.vcatnCd)!}") $(elmt).attr("selected", "selected");
                });
                //반차일 경우 selectbox 안보이게
                if ('${item.vcatnCd}' == "AM_HALF" || '${item.vcatnCd}' == "PM_HALF") {
                    Page.delEndDt(reqstItemIdx);
                }
                commons.datepicker.singleDatePicker("#bgnDt" + reqstItemIdx, "yyyy-MM-DD", "${(item.bgnDt)}", function() {
                    Page.delEndDt(reqstItemIdx);
                    Page.noBefore("#bgnDt"+reqstItemIdx, "#endDt"+reqstItemIdx, reqstItemIdx);
                });
                commons.datepicker.singleDatePicker("#endDt" + reqstItemIdx, "yyyy-MM-DD", "${(item.endDt)}", function() {
                    if ($("#endDt"+reqstItemIdx).val() !== "") {
                        Page.noBefore("#bgnDt"+reqstItemIdx, "#endDt"+reqstItemIdx, reqstItemIdx);
                    }
                });
                </#list>
                </#if>
            },
            /** 추가항목 영역 삭제 */
            delSpan: function(idx) {
                if ($("#vcatnItemId" + idx).val() === "") {
                    $("div#itemContainer" + idx).remove();
                } else {
                    $("#delYn" + idx).val("Y");
                    $("div#itemContainer" + idx).hide();
                }
            }
        }
    }();

    $(document).ready(function () {
        Page.init();
    });
</script>

<!--begin::Row-->
<div class="row">
    <div class="col-12 d-flex justify-content-end align-items-center mt-4">
        <button type="button" class="btn btn-sm btn-secondary btn-active-primary mx-1"
                onclick="$('#vcatn_papr_rule_modal').modal('show');"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="휴가규칙 팝업을 호출합니다.">
            <i class="fas fa-info-circle"></i>휴가규칙
        </button>
        <#include "/view/vcatn/papr/_vcatn_papr_rule_modal.ftlh">
    </div>
</div>
<!-- class 속성이 덮어씌워지지 않아서 스타일 지정 -->
<div class="card post" style="margin-top:15px !important;">
    <!--begin:Form-->
    <form id="vcatnPaprRegForm" class="form" enctype="multipart/form-data">
        <input type="hidden" name="postNo" value="${post.postNo!}">
        <input type="hidden" name="title" id="title" value="${post.title!}"/>
        <input type="hidden" name="atchFileNo" value="${post.atchFileNo!}">
        <input type="hidden" name="hitCnt" value="${post.hitCnt!0?string.number}">
        <input type="hidden" name="cfYn" value="${post.cfYn!"N"}">

        <!--end::Input group-->
        <div id="vcatnListDiv" class="mb-8">
            <label class="mb-2">
                <span class="text-gray-700 fs-6 fw-bolder">휴가등록</span>
            </label>
            <a href="javascript:void(0);" class="btn btn-light-primary btn-sm me-2 mb-2 m-lg-2 justify-content-end mb-5"
               onclick="Page.addVcatnInfoRegItem();"
               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="휴가 일정을 추가합니다.">
                <i class="fas fa-plus"></i>추가
            </a>
            <span class="text-noti fs-7 ps-5">※주말 및 공휴일은 자동으로 휴가일자 산정에서 제외됩니다. (포함해서 올려도 상관없음)  </span>
        </div>

        <!-- #include:: component:: 첨부파일 추가추가 영역 -->
        <#include "/view/_component/atch_file/_atch_file_reg_area.ftlh">

        <div class="separator my-8"></div>

        <!-- 잔디 알림 발송 영역 -->
        <@component.jandi_reg_area 1 "VCATN" />

        <div class="separator my-8 w-"></div>

        <!-- 버튼 영역 -->
        <div class="d-flex justify-content-end">
            <@component.btn_post_save "Page" />
        </div>
    </form>
    <!--end:Form-->
</div>
<!--end::post-->

<!-- #include:: component:: 첨부파일 추가 영역 template-->
<#include "/view/_component/atch_file/_atch_file_area.ftlh">
<!-- #include:: component:: 첨부파일 추가 영역 template-->
<#include "/view/_component/_tinymce_img.ftlh">

<#include "/view/vcatn/papr/_vcatn_papr_reg_item_template.ftlh">

</@layout.layout>