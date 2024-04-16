<#--
  -- 휴가일자(vcatn_schdul) : 모듈 스크립트 분리
  -->

<script>
    const VcatnSchdul = function() {
        return {
            /** 조회년도 변경 */
            chgYy: function(obj) {
                commons.util.blockUIReplace("${Url.VCATN_SCHDUL_LIST!}?statsYy=" + $(obj).val());
            },
            /** 등록 팝업 호출 */
            regForm: function() {
                $("#vcatn_schdul_reg_modal").modal("show");
                $('[data-bs-toggle="tooltip"]').tooltip();      // template에 있는 tooltip들 활성화
            },
            /** 등록 또는 수정 처리(Ajax) */
            regAjax: function() {
                let ajaxData = new FormData($("#vcantDyRegForm")[0]);
                let isReg = ($("#vcatnSchdulNo").val() === "");
                const url = isReg ? '${Url.VCATN_SCHDUL_REG_AJAX!}' : '${Url.VCATN_SCHDUL_MDF_AJAX!}';
                commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rslt) commons.util.blockUIReload();
                        });
                });
            },
            /** 유효성검사:: 시작일 이전 날짜 선택 불가하게 막기 */
            noBefore: function(beginSelectorStr, endSelectorStr) {
                let result = commons.date.dateCompare(beginSelectorStr, endSelectorStr);
                let select = $("#vcatnCd").children('option:selected').val();
                if (select === "AM_HALF" || select === "PM_HALF") {
                    $(endSelectorStr).val($(beginSelectorStr).val());
                } else if (result === false && $(endSelectorStr).val() !== "") {
                    Swal.fire("접속 IP는 최소 한 개 이상 입력해야 합니다.");
                    $(endSelectorStr).val("");
                }
            },
            /** 반차 선택 시 종료일 안보이게 막기 */
            delEndDt: function() {
                let selected = $("#vcatnCd").children('option:selected').val();
                if (selected === "AM_HALF" || selected === "PM_HALF") {
                    $("#endDtDiv").attr("style", "display:none !important");        // 강제로 !important 적용
                    $("#endDt").val($("#bgnDt").val());
                    $("#endDtSpan").empty();
                } else {
                    $("#endDtDiv").show();
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
                    $("#vcatnResn").val(rsltObj.vcatnResn);
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