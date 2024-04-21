<#--
 * 휴가계획서(vcatn_papr) > 등록 화면
 -->

<#import "/layout/layout.ftlh" as layout >

<script type="text/javascript" src="/lib/prism/js/prism.js?${releaseDate!}"></script>

<#assign isReg = (isReg!false)>
<#assign isMdf = (isMdf!false)>

<@layout.layout toolbar=toolbar!"">

<#--include::script modules-->
<#include "/view/vcatn/papr/_vcatn_papr_scripts.ftlh">

<script>
    const Page = (function() {
        return {
            idx: 0,
            init: function () {
                // 추가추가에 사용할 인덱스 설정
                Page.idx = ${(post.schdulList![])?size};

                /* jquery :: validation */
                VcatnPapr.initValidateForm();
                // 첨부파일 영역 0개인지 체크
                AtchFile.atchFileListToggle();
                // 공백 자동 제거
                commons.validate.noSpaces(".no-space");

                // 잔디발송여부 클릭시 글씨 변경
                commons.util.chckboxLabel("jandiYn", "발송//미발송", "#0095E8//gray", function () {
                    $("#trgetTopicSpan").show();
                }, function () {
                    $("#trgetTopicSpan").hide();
                });
                <#if isReg!false>
                    $("#jandiYn").trigger("click");
                </#if>

                <#if isReg!false>
                    /** 휴가 추가 폼 기본 뿌리기 */
                    Page.addVcatnSchdul();
                </#if>
            },
            isHalf: function(idx) {
                const vcatnCd = $("#vcatnCd" + idx).children('option:selected').val();
                return vcatnCd === "AM_HALF" || vcatnCd === "PM_HALF";
            },
            /** 반차 선택 시 종료일 안보이게 */
            propEndDt: function(idx) {
                if (Page.isHalf(idx)) {
                    $("#lineDiv"+idx).css("visibility", "hidden");
                    $("#endDtDiv"+idx).css("visibility", "hidden");
                    $("#endDt"+idx).val($("#bgnDt"+idx).val());
                    $("#endDt"+idx+"_valid_span").empty();
                } else {
                    $("#lineDiv"+idx).css("visibility", "visible");
                    $("#endDtDiv"+idx).css("visibility", "visible");
                }
            },
            /** 시작일 이전 날짜 선택 불가 */
            noBefore: function(beginSelectorStr, endSelectorStr, idx) {
                if (Page.isHalf(idx)) {
                    $(endSelectorStr).val($(beginSelectorStr).val());
                } else if ($(endSelectorStr).val() !== "") {
                    Swal.fire("휴가일을 다시 설정해 주세요.");
                    $(endSelectorStr).val("");
                }
            },
            /**
             * 휴가등록 부분 추가
             */
            addVcatnSchdul: function() {
                const idx = Page.idx;
                commons.util.handlebarsAppend({ "idx": idx }, "vcatn_schdul_reg");
                commons.datepicker.singleDatePicker("#bgnDt" + idx, "yyyy-MM-DD", null, function() {
                    Page.propEndDt(idx);
                    Page.noBefore("#bgnDt"+idx, "#endDt"+Page.idx, idx);
                });
                commons.datepicker.singleDatePicker("#endDt"+idx, "yyyy-MM-DD", null, function() {
                    if ($("#endDt"+idx).val() !== "") {
                        Page.noBefore("#bgnDt"+idx, "#endDt"+idx, idx);
                    }
                });
            },
            /** 추가항목 영역 삭제 */
            delSpan: function(idx) {
                $("div#itemContainer" + idx).remove();
            }
        }
    })();
    $(document).ready(function () {
        Page.init();
    });
</script>

<#--begin::Row-->
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
        <div class="mb-8">
            <label class="mb-2">
                <span class="text-gray-700 fs-6 fw-bolder">휴가등록</span>
            </label>
            <a href="javascript:void(0);" class="btn btn-light-primary btn-sm me-2 mb-2 m-lg-2 justify-content-end mb-5"
               onclick="Page.addVcatnSchdul();"
               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="휴가 일정을 추가합니다.">
                <i class="fas fa-plus"></i>추가
            </a>
            <span class="text-noti fs-7 ps-5">※주말 및 공휴일은 자동으로 휴가일자 산정에서 제외됩니다. (포함해서 올려도 상관없음)</span>
            <!-- vcatnSchduls -->
            <div id="vcatn_schdul_reg_div" class="mb-8">
                <!--휴가 목록-->
                <#include "/view/vcatn/papr/_vcatn_schdul_reg_area.ftlh">
                <!-- vcatnSchdulList goes in here -->
            </div>
        </div>

        <!-- #include:: component:: 첨부파일 추가추가 영역 -->
        <#include "/view/_component/atch_file/_atch_file_reg_area.ftlh">

        <div class="separator my-8"></div>

        <!-- 잔디 알림 발송 영역 -->
        <@component.jandi_reg_area 1 "VCATN" />

        <div class="separator my-8 w-"></div>

        <!-- 버튼 영역 -->
        <div class="d-flex justify-content-end">
            <@component.btn_post_submit "VcatnPapr.submit();" />
            <!-- 버튼 :: 목록으로 가기 -->
            <@component.btn_post_toList "VcatnPapr.list();" />
        </div>
    </form>
    <!--end:Form-->
</div>
<!--end::post-->

<#include "/view/vcatn/papr/_vcatn_schdul_reg_template.hbs">

<!-- #include:: component:: 첨부파일 추가 영역 template-->
<#include "/view/_component/atch_file/_atch_file_area.ftlh">
<!-- #include:: component:: 첨부파일 추가 영역 template-->
<#include "/view/_component/_tinymce_img.ftlh">

</@layout.layout>