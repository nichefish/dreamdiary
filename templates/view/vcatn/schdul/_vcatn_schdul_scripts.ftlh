<#--
 * 휴가일자(vcatn_schdul) : 스크립트 모듈 분리
 -->

<script>
    const VcatnSchdul = function() {
        return {
            /** 조회년도 변경 */
            chgYy: function(obj) {
                commons.util.blockUIReplace("${Url.VCATN_SCHDUL_LIST!}?statsYy=" + $(obj).val());
            },
            /** 등록 팝업 호출 */
            regModal: function() {
                commons.util.handlebarsTemplate({}, "vcatn_schdul_reg", "show");
                commons.datepicker.singleDatePicker("#bgnDt", "yyyy-MM-DD", null, function() {
                    VcatnSchdul.propEndDt();
                    VcatnSchdul.noBefore("#bgnDt", "#endDt");
                });
                commons.datepicker.singleDatePicker("#endDt", "yyyy-MM-DD", null, function() {
                    if ($("#endDt").val() !== "") VcatnSchdul.noBefore("#bgnDt", "#endDt");
                });
            },
            /** 반차 여부 */
            isHalf: function(idx) {
                if (idx === undefined) idx = "";
                const vcatnCd = $("#vcatnCd" + idx).children('option:selected').val();
                return vcatnCd === "${Constant.VCATN_AM_HALF}" || vcatnCd === "${Constant.VCATN_PM_HALF}";
            },
            /** 시작일 이전 날짜 선택 불가 */
            noBefore: function(beginSelectorStr, endSelectorStr, idx) {
                if (VcatnSchdul.isHalf(idx)) {
                    $(endSelectorStr).val($(beginSelectorStr).val());
                } else {
                    const isBefore = commons.date.isBefore(beginSelectorStr, endSelectorStr);
                    if (!isBefore && $(endSelectorStr).val() !== "") {
                        Swal.fire("휴가일을 다시 설정해 주세요.");
                        $(endSelectorStr).val("");
                    }
                }
            },
            /** submit */
            submit: function() {
                $("#vcantSchdulRegForm").submit();
            },
            /** 등록 또는 수정 처리(Ajax) */
            regAjax: function() {
                let ajaxData = new FormData($("#vcantSchdulRegForm")[0]);
                let isReg = ($("#vcatnSchdulNo").val() === "");
                const url = isReg ? '${Url.VCATN_SCHDUL_REG_AJAX!}' : '${Url.VCATN_SCHDUL_MDF_AJAX!}';
                commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rslt) commons.util.blockUIReload();
                        });
                });
            },
            /** 반차 선택 시 종료일 안보이게 */
            propEndDt: function(idx) {
                if (idx === undefined) idx = "";
                if (VcatnSchdul.isHalf(idx)) {
                    $("#lineDiv"+idx).addClass("d-none");
                    $("#endDtDiv"+idx).addClass("d-none");
                    $("#endDt"+idx).val($("#bgnDt"+idx).val());
                    $("#endDt"+idx+"_valid_span").empty();
                } else {
                    $("#lineDiv"+idx).removeClass("d-none");
                    $("#endDtDiv"+idx).removeClass("d-none");
                }
            },
            /** 수정 팝업 호출 (ajax) */
            mdfModal: function(vcatnSchdulNo) {
                $("#vcatnSchdulNoProc").val(vcatnSchdulNo);
                const url = "${Url.VCATN_SCHDUL_DTL_AJAX!}";
                let ajaxData = $("#procForm").serializeArray();
                commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                    if (!res.rslt) return;
                    let rsltObj = res.rsltObj;
                    // 수정 modal_form에 값 세팅
                    $("#vcatnSchdulNo").val(vcatnSchdulNo);
                    $("#userId").val(rsltObj.userId);
                    $("#vcatnCd").val(rsltObj.vcatnCd);
                    $("#bgnDt").val(rsltObj.bgnDt);
                    $("#endDt").val(rsltObj.endDt);
                    $("#resn").val(rsltObj.resn);
                    $("#vcatnRm").val(rsltObj.vcatnRm);
                    $("#vcatn_schdul_reg_modal").modal("show");
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
                    $("#procForm").attr("action", "${Url.VCATN_SCHDUL_XLSX_DOWNLOAD!}").submit();
                });
            },
            /** 삭제 처리 (ajax) */
            delAjax: function(vcatnSchdulNo) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    $("#vcatnSchdulNoProc").val(vcatnSchdulNo);
                    const url = "${Url.VCATN_SCHDUL_DEL_AJAX!}";
                    let ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    });
                });
            }
        }
    }();
</script>