<#--
 * 저널 일자(jrnl_day) : 스크립트 모듈 분리
 -->

<#--include::script modules-->
<#include "/view/jrnl/day/tag/_jrnl_day_tag_ctgr_map.ftlh">

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
                const ajaxData = { "yy": $("#jrnl_aside #yy").val(), "mnth": mnth };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    const rsltList = res.rsltList;
                    // 정렬 처리
                    const sortStr = $("#jrnl_aside #sort").val();
                    if (sortStr === "ASC") {
                        $("#jrnl_aside #sortIcon").removeClass("bi-sort-numeric-up-alt").addClass("bi-sort-numeric-down");
                    } else {
                        $("#jrnl_aside #sortIcon").removeClass("bi-sort-numeric-down").addClass("bi-sort-numeric-up-alt");
                        if (commons.util.isNotEmpty(rsltList)) rsltList.reverse();
                    }
                    $("#jrnl_dream_list_div").empty();
                    commons.util.closeModal();
                    commons.util.handlebarsTemplate(rsltList, "jrnl_day_list");
                }, "block");
            },
            /** 등록 화면 호출 */
            regModal: function() {
                commons.util.handlebarsTemplate({}, "jrnl_day_reg", "show");
                /* jquery :: validation */
                JrnlDay.initValidateForm();
                // 일자 datepicker 날짜 검색 init : 현재 조회중인 yyyy-MM으로 처리
                commons.datepicker.singleDatePicker("#jrnlDt", "yyyy-MM-DD", JrnlDay.validDt());
                // 날짜미상 datepicker 날짜 검색 init
                commons.datepicker.singleDatePicker("#aprxmtDt", "yyyy-MM-DD")
                // 날짜미상 checkbox init
                commons.util.chckboxLabel("dtUnknownYn", "날짜미상//날짜미상", "blue//gray", function() {
                    $("#jrnlDayRegForm #jrnlDtDiv").addClass("d-none");
                    $("#jrnlDayRegForm #aprxmtDtDiv").removeClass("d-none");
                    $("#jrnlDayRegForm #aprxmtDt").val($("#jrnlDayRegForm #jrnlDt").val());
                }, function() {
                    $("#jrnlDayRegForm #jrnlDtDiv").removeClass("d-none");
                    $("#jrnlDayRegForm #aprxmtDtDiv").addClass("d-none");
                    $("#jrnlDayRegForm #jrnlDt").val($("#jrnlDayRegForm #aprxmtDt").val());
                });
                /* tagify */
                commons.tagify.initWithCtgr("#jrnlDayRegForm #tagListStr", TagCtgrMap.jrnlDay);
            },
            /** 사이드바 기준으로 등록 모달 날짜 계산:: 메소드 분리 */
            validDt: function() {
                const year = parseInt($("#jrnl_aside #yy").val(), 10);
                let month = parseInt($("#jrnl_aside #mnth").val(), 10);
                if (month === 99) month = 1;
                let day = parseInt(commons.date.getCurrDayStr(2), 10);

                // 만약 day가 해당 월의 마지막 날을 초과하면 마지막 날로 설정
                const lastDay = new Date(year, month, 0).getDate();
                if (day > lastDay) day = lastDay;

                // 결과를 yyyy-mm-dd 형식으로 반환
                const yyyyStr = year.toString();
                const mmStr = month.toString().padStart(2, '0'); // 월을 두 자리로 포맷팅
                const ddStr = day.toString().padStart(2, '0');   // 일을 두 자리로 포맷팅

                return yyyyStr + '-' + mmStr + '-' + ddStr;
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
                    const ajaxData = new FormData($("#jrnlDayRegForm")[0]);
                    commons.util.blockUIMultipartAjax(url, ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) {
                                    JrnlDay.yyMnthListAjax();
                                    // 태그 조회
                                    JrnlDayTag.listAjax();
                                }
                            });
                    }, "block");
                });
            },
            /** 등록 화면 호출 */
            mdfModal: function(postNo) {
                const url = "${Url.JRNL_DAY_DTL_AJAX!}";
                const ajaxData = { "postNo" : postNo };
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
                        $("#jrnlDayRegForm #jrnlDtDiv").addClass("d-none");
                        $("#jrnlDayRegForm #aprxmtDtDiv").removeClass("d-none");
                        $("#jrnlDayRegForm #aprxmtDt").val($("#jrnlDayRegForm #jrnlDt").val());
                    }, function() {
                        $("#jrnlDayRegForm #jrnlDtDiv").removeClass("d-none");
                        $("#jrnlDayRegForm #aprxmtDtDiv").addClass("d-none");
                        $("#jrnlDayRegForm #jrnlDt").val($("#jrnlDayRegForm #aprxmtDt").val());
                    });
                    /* tagify */
                    commons.tagify.initWithCtgr("#jrnlDayRegForm #tagListStr", TagCtgrMap.jrnlDay);
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
                    const ajaxData = { "postNo": postNo };
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) {
                                    JrnlDay.yyMnthListAjax();
                                    // 태그 조회
                                    JrnlDayTag.listAjax();
                                }
                            });
                    }, "block");
                });
            },
        }
    })();
</script>