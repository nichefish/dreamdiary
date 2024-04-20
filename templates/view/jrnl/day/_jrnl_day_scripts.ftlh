<#--
 * 저널 일자(jrnl_day) : 스크립트 모듈 분리
 -->

<script>
    const JrnlDay = (function() {
        return {
            /** form validation init */
            initValidateForm: function() {
                /* jquery :: validation */
                $("#jrnlDayRegForm").validate({
                    submitHandler: function() {
                        JrnlDay.regAjax();
                    },
                    errorPlacement : function(error, element) {
                        commons.validate.errorSpan(error, element);        // 공통 함수로 분리
                    },
                    rules: {
                        jrnlDt: {
                            required: function() {
                                return $("#jrnlDayRegForm #dtUnknownYn").prop(":checked", false);
                            }
                        },
                        aprxmtDt: {
                            required: function() {
                                return $("#jrnlDayRegForm #dtUnknownYn").prop(":checked", true);
                            }
                        },
                    },
                });
                $("#dtUnknownYn").change(function() {
                    $("#jrnlDt").valid(); // 체크박스 상태 변경시 details 필드 재검증
                    $("#aprxmtDt").valid(); // 체크박스 상태 변경시 details 필드 재검증
                });
            },
            /** 목록 조회 */
            listAjax: function() {
                const url = "${Url.JRNL_DAY_LIST_AJAX!}";
                let ajaxData = { "yy": $("#jrnl_aside #yy").val(), "mnth": $("#jrnl_aside #mnth").val() };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltList, "jrnl_day_list");
                });
            },
            /** 등록 화면 호출 */
            regModal: function() {
                commons.util.handlebarsTemplate({}, "jrnl_day_reg", "show");
                /* jquery :: validation */
                JrnlDay.initValidateForm();
                // 일자 datepicker 날짜 검색 init
                commons.datepicker.singleDatePicker("#jrnlDt", "yyyy-MM-DD", commons.date.getCurrDateStr("yyyy-MM-dd"));
                // 날짜미상 datepicker 날짜 검색 init
                commons.datepicker.singleDatePicker("#aprxmtDt", "yyyy-MM-DD")
                // 날짜미상 checkbox init
                commons.util.chckboxLabel("dtUnknownYn", "날짜미상//날짜미상", "blue//gray", function() {
                    $("#jrnlDtDiv").addClass("d-none");
                    $("#aprxmtDtDiv").removeClass("d-none");
                }, function() {
                    $("#jrnlDtDiv").removeClass("d-none");
                    $("#aprxmtDtDiv").addClass("d-none");
                });
                $('[data-bs-toggle="tooltip"]').tooltip();      // template에 있는 tooltip들 활성화
            },
            /** submit */
            submit: function() {
                $("#jrnlDayRegForm").submit();
            },
            /** 등록 */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.JRNL_DAY_REG_AJAX!}";
                    let ajaxData = new FormData($("#jrnlDayRegForm")[0]);
                    commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();;
                            });
                    });
                });
            },
            /** 등록 화면 호출 */
            mdfModal: function(postNo) {
                const url = "${Url.JRNL_DAY_DTL_AJAX!}";
                let ajaxData = { "postNo" : postNo };
                commons.util.blockUIAjax(url, "get", ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    const rsltObj = res.rsltObj;
                    commons.util.handlebarsTemplate(rsltObj, "jrnl_day_reg", "show");
                    /* jquery :: validation */
                    JrnlDay.initValidateForm();
                    // datepicker 날짜 검색 init
                    commons.datepicker.singleDatePicker("#jrnlDt", "yyyy-MM-DD", rsltObj.jrnlDt);
                    // 날짜미상 datepicker 날짜 검색 init
                    commons.datepicker.singleDatePicker("#aprxmtDt", "yyyy-MM-DD", rsltObj.aprxmtDt);
                    // 날짜미상 checkbox init
                    commons.util.chckboxLabel("dtUnknownYn", "날짜미상//날짜미상", "blue//gray", function() {
                        $("#jrnlDtDiv").addClass("d-none");
                        $("#aprxmtDtDiv").removeClass("d-none");
                    }, function() {
                        $("#jrnlDtDiv").removeClass("d-none");
                        $("#aprxmtDtDiv").addClass("d-none");
                    });
                    $('[data-bs-toggle="tooltip"]').tooltip();      // template에 있는 tooltip들 활성화
                });
            },
            /** 삭제 */
            delAjax: function(postNo) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.JRNL_DAY_DEL_AJAX!}";
                    let ajaxData = { "postNo": postNo };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();;
                            });
                    });
                });
            },
        }
    })();
</script>