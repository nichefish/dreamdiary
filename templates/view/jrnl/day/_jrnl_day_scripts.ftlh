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
            /** 년도-월 목록 조회 */
            yyMnthListAjax: function() {
                const mnth = $("#jrnl_aside #mnth").val();
                if (commons.util.isEmpty(mnth)) return;
                const url = "${Url.JRNL_DAY_LIST_AJAX!}";
                let ajaxData = { "yy": $("#jrnl_aside #yy").val(), "mnth": mnth };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    let rsltList = res.rsltList;
                    // 정렬 처리
                    let sortStr = $("#jrnl_aside #sort").val();
                    if (sortStr === "ASC") {
                        $("#sortIcon").removeClass("bi-sort-numeric-up-alt").addClass("bi-sort-numeric-down");
                    } else {
                        $("#sortIcon").removeClass("bi-sort-numeric-down").addClass("bi-sort-numeric-up-alt");
                        if (commons.util.isNotEmpty(rsltList)) rsltList.reverse();
                    }
                    $("#jrnl_dream_list_div").empty();
                    commons.util.closeModal();
                    commons.util.handlebarsTemplate(rsltList, "jrnl_day_list");
                }, "block");
            },
            /** */
            sort: function() {
                let asIs = $("#jrnl_aside #sort").val();
                let toBe = (asIs !== "ASC") ? "ASC" : "DESC";
                // 쿠키 설정하기
                let cookieOptions = {
                    path: "/jrnl/day/",
                    expires: commons.date.getCurrDateAddDay(36135)
                };
                $.cookie("jrnl_day_sort", toBe, cookieOptions);
                // 값 설정
                $("#jrnl_aside #sort").val(toBe);
                if (toBe === "DESC") {
                    $("#sortIcon").removeClass("bi-sort-numeric-down").addClass("bi-sort-numeric-up-alt");
                } else {
                    $("#sortIcon").removeClass("bi-sort-numeric-up-alt").addClass("bi-sort-numeric-down");
                }

                const container = document.querySelector('#jrnl_day_list_div'); // 모든 저널 일자를 포함하는 컨테이너
                const days = Array.from(container.querySelectorAll('.jrnl-day')); // 모든 'jrnl-day' 요소를 배열로 변환
                days.sort((a, b) => {
                    const dateA = new Date(a.querySelector('.jrnl-day-header .col-1').textContent.trim());
                    const dateB = new Date(b.querySelector('.jrnl-day-header .col-1').textContent.trim());
                    if (toBe === "ASC") return dateA - dateB; // 날짜를 비교하여 역순 정렬
                    return dateB - dateA; // 날짜를 비교하여 역순 정렬
                });
                // 컨테이너에서 모든 요소를 제거
                while (container.firstChild) {
                    container.removeChild(container.firstChild);
                }
                // 정렬된 요소를 다시 컨테이너에 추가
                days.forEach(day => {
                    container.appendChild(day);
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
            },
            /** 아이콘 새로고침 */
            refreshIcon: function() {
                const $iconClassElmt = $("#jrnlDayRegForm #weather");
                if (!$iconClassElmt.length) return;
                $("#jrnlDayRegForm #weather_icon_div").html($iconClassElmt.val());
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
                                if (res.rslt) JrnlDay.yyMnthListAjax();
                            });
                    }, "block");
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
                                if (res.rslt) JrnlDay.yyMnthListAjax();
                            });
                    }, "block");
                });
            },
        }
    })();
</script>