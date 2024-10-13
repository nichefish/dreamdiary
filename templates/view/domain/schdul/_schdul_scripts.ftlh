<#--
 * 일정(schdul) : 스크립트 모듈 분리
 -->

<script>
    const Schdul = (function() {
        return {
            prtcpntCnt: 0,

            /**
             * form init
             * @param {Object} obj - 폼에 바인딩할 데이터
             */
            initForm: function(obj = {}) {
                /* jquery validation */
                commons.validate.validateForm("#schdulRegForm", Schdul.submitHandler);
                // 엔터키 처리
                commons.util.enterKey("#searchKeyword", Page.search);
            },

            /**
             * Custom SubmitHandler
             */
            submitHandler: function() {
                const isReg = ($("#schdulRegForm #postNo").val() === "");
                Swal.fire({
                    text: isReg ? <@spring.message "view.cnfm.reg"/> : <@spring.message "view.cnfm.mdf"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    Schdul.regAjax();
                });
            },

            /**
             * 등록 모달 호출
             */
            regModal: function() {
                commons.util.handlebarsTemplate({}, "schdul_reg", "show");
                commons.util.chckboxLabel("jandiYn", "발송//미발송", "blue//gray");
                commons.datepicker.singleDatePicker("#bgnDt", "yyyy-MM-DD");
                commons.datepicker.singleDatePicker("#endDt", "yyyy-MM-DD");

                // 잔디발송여부 클릭시 글씨 변경
                commons.util.chckboxLabel("jandiYn", "발송//미발송", "blue//gray", function() {
                    $("#trgetTopicSpan").show();
                }, function() {
                    $("#trgetTopicSpan").hide();
                });
                $("#jandiYn").click();
                Schdul.addPrtcpnt();
            },

            /**
             * 일정 등록 모달 호출 (개인일정)
             */
            prvtRegModal: function() {
                commons.util.handlebarsTemplate({ "isPrvt": true }, "schdul_reg", "show");
                commons.datepicker.singleDatePicker("#bgnDt", "yyyy-MM-DD");
                commons.datepicker.singleDatePicker("#endDt", "yyyy-MM-DD");
            },

            /**
             * 참여자 추가
             */
            addPrtcpnt: function() {
                commons.util.handlebarsAppend({ "idx": Schdul.prtcpntCnt++ }, "schdul_reg_prtcpnt");
            },

            /**
             * 참여자 삭제
             */
            removePrtcpnt: function(idx) {
                $("#schdul_reg_prtcpnt_div #prtcpntRow"+idx).remove();
            },

            /**
             * 종료일자 토글 처리
             */
            schdulCd: function(obj) {
                if ($(obj).val() === "${Constant.SCHDUL_HLDY!}") {
                    $("#endDt").val($("#startDt").val());
                    $("#endDtDiv").hide();
                    $("#endDtSpan").hide();
                } else {
                    $("#endDtDiv").show();
                    $("#endDtSpan").show();
                }
            },

            /**
             * form submit
             */
            submit: function() {
                $("#schdulRegForm").submit();
            },

            /**
             * 등록/수정 처리(Ajax)
             */
            regAjax: function() {
                const url = "${Url.SCHDUL_REG_AJAX!}";
                const ajaxData = $("#schdulRegForm").serializeArray();
                commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rslt) commons.util.blockUIReload();
                        });
                }, "block");
            },

            /**
             * 상세 모달 호출
             * @param {string|number} postNo - 조회할 글 번호.
             */
            dtlModal: function(postNo) {
                event.stopPropagation();
                if (isNaN(postNo)) return;

                const url = "${Url.SCHDUL_DTL_AJAX!}";
                const ajaxData = { "postNo": postNo };
                commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    const obj = res.rsltObj;
                    commons.util.handlebarsTemplate(obj, "schdul_dtl", "show");
                    Schdul.key = obj.postNo;
                });
            },

            /**
             * 수정 모달 호출
             */
            mdfModal: function(key) {
                if (isNaN(key)) return;

                const url = "${Url.SCHDUL_DTL_AJAX!}";
                const ajaxData = { "postNo": key };
                commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    $("#schdul_dtl_modal").modal("hide");
                    commons.util.handlebarsTemplate(res.rsltObj, "schdul_reg", "show");
                    const { rsltObj: { prtcpntList: prtcpnt } } = res;
                    Schdul.prtcpntCnt = prtcpnt !== undefined ? prtcpnt.length : 0;

                    commons.datepicker.singleDatePicker("#bgnDt", "yyyy-MM-DD", rsltObj.bgnDt);
                    commons.datepicker.singleDatePicker("#endDt", "yyyy-MM-DD", rsltObj.endDt);
                    // 잔디발송여부 클릭시 글씨 변경
                    commons.util.chckboxLabel("jandiYn", "발송//미발송", "blue//gray", function() {
                        $("#trgetTopicSpan").show();
                    }, function() {
                        $("#trgetTopicSpan").hide();
                    });
                });
            },

            /**
             * 삭제 (Ajax)
             * @param {string|number} key - 참여자 번호.
             */
            delAjax: function(key) {
                if (isNaN(key)) return;

                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.SCHDUL_DEL_AJAX!}";
                    const ajaxData = { "postNo" : key }
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            }
        }
    })();
</script>