<#--
 * 저널 꿈(jrnl_dream) : 스크립트 모듈 분리
 -->

<#--include::script modules-->
<#include "/view/jrnl/dream/tag/_jrnl_dream_tag_ctgr_map.ftlh">

<script>
    const JrnlDream = (function() {
        return {
            /** form validation init */
            initValidateForm: function() {
                /* jquery :: validation */
                $("#jrnlDreamRegForm").validate({
                    submitHandler: function() {
                        JrnlDream.regAjax();
                    },
                    errorPlacement : function(error, element) {
                        commons.validate.errorSpan(error, element);        // 공통 함수로 분리
                    },
                    rules: {
                        elseDreamerNm: {
                            required: function() {
                                return $("#jrnlDreamRegForm #elseDreamYn").prop(":checked", false);
                            }
                        },
                    },
                });
                $("#elseDreamYn").change(function() {
                    $("#elseDreamerNm").valid(); // 체크박스 상태 변경시 details 필드 재검증
                });
            },
            /** 목록 조회 */
            keywordListAjax: function() {
                const keyword = $("#jrnl_aside #dreamKeyword").val();
                if (commons.util.isEmpty(keyword)) return;
                const url = "${Url.JRNL_DREAM_LIST_AJAX!}";
                const ajaxData = { "dreamKeyword": $("#jrnl_aside #dreamKeyword").val() };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    $("#jrnl_aside #yy").val("");
                    $("#jrnl_aside #mnth").val("");
                    // 목록 영역 클리어
                    $("#jrnl_aside #diaryKeyword").val("");
                    $("#jrnl_day_list_div").empty();
                    $("#jrnl_diary_list_div").empty();
                    // 태그 헤더 클리어
                    $("#jrnl_day_tag_list_div").empty();
                    $("#jrnl_diary_tag_list_div").empty();
                    $("#jrnl_dream_tag_list_div").empty();
                    commons.util.closeModal();
                    commons.util.handlebarsTemplate(res.rsltList, "jrnl_dream_list");
                    $("[data-bs-toggle='tooltip']").tooltip();
                    JrnlDream.inKeywordSearchMode = true;
                }, "block");
            },
            /** 저널 꿈 등록 모달 팝업 호출 */
            regModal: function(jrnlDayNo, stdrdDt) {
                const obj = { "jrnlDayNo": jrnlDayNo, "stdrdDt": stdrdDt };
                commons.util.handlebarsTemplate(obj, "jrnl_dream_reg_header");
                commons.util.handlebarsTemplate(obj, "jrnl_dream_reg", "show");

                /* jquery :: validation */
                JrnlDream.initValidateForm();
                /* tagify */
                commons.tagify.initWithCtgr("#jrnlDreamRegForm #tagListStr", TagCtgrMap.jrnlDream);

                // checkbox init
                commons.util.chckboxLabel("imprtcYn", "중요//해당없음", "red//gray");
                // checkbox init
                commons.util.chckboxLabel("nhtmrYn", "악몽//해당없음", "red//gray");
                // checkbox init
                commons.util.chckboxLabel("hallucYn", "입면환각//해당없음", "blue//gray");
                // checkbox init
                commons.util.chckboxLabel("elseDreamYn", "해당//미해당", "blue//gray", function() {
                    $("#elseDreamerNmDiv").removeClass("d-none");
                }, function() {
                    $("#elseDreamerNmDiv").addClass("d-none");
                });
                // tinymce editor init
                commons.tinymce.init('#tinymce_jrnlDreamCn', commons.tinymce.imgRegFunc);
                commons.tinymce.setContentWhenReady("tinymce_jrnlDreamCn", "");
            },
            /** 이관 submit */
            submit: function() {
                tinymce.get("tinymce_jrnlDreamCn").save();
                $("#jrnlDreamRegForm").submit();
            },
            /** 등록 */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.JRNL_DREAM_REG_AJAX!}";
                    const ajaxData = new FormData($("#jrnlDreamRegForm")[0]);
                    commons.util.blockUIMultipartAjax(url, ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (!res.rslt) return;
                                if (JrnlDream.inKeywordSearchMode) {
                                    JrnlDream.keywordListAjax();
                                } else {
                                    JrnlDay.yyMnthListAjax();
                                    JrnlDreamTag.listAjax();
                                }
                                // TODO: 결산 페이지에서 처리시도 처리해 줘야 한다.
                                commons.util.unblockUI();
                            });
                    }, "block");
                });
            },
            /** 저널 꿈 수정 모달 팝업 호출 */
            mdfModal: function(postNo) {
                const url = "${Url.JRNL_DREAM_DTL_AJAX!}";
                const ajaxData = { "postNo" : postNo };
                commons.util.blockUIAjax(url, "get", ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    const rsltObj = res.rsltObj;

                    commons.util.handlebarsTemplate(rsltObj, "jrnl_dream_reg_header");
                    commons.util.handlebarsTemplate(rsltObj, "jrnl_dream_reg", "show");
                    /* jquery :: validation */
                    JrnlDream.initValidateForm();
                    /* tagify */
                    commons.tagify.initWithCtgr("#jrnlDreamRegForm #tagListStr", TagCtgrMap.jrnlDream);

                    // checkbox init
                    commons.util.chckboxLabel("imprtcYn", "중요//해당없음", "red//gray");
                    // checkbox init
                    commons.util.chckboxLabel("nhtmrYn", "악몽//해당없음", "red//gray");
                    // checkbox init
                    commons.util.chckboxLabel("hallucYn", "입면환각//해당없음", "blue//gray");
                    // checkbox init
                    commons.util.chckboxLabel("elseDreamYn", "해당//미해당", "blue//gray", function() {
                        $("#elseDreamerNmDiv").removeClass("d-none");
                    }, function() {
                        $("#elseDreamerNmDiv").addClass("d-none");
                    });
                    // tinymce editor reset
                    commons.tinymce.init('#tinymce_jrnlDreamCn', commons.tinymce.imgRegFunc);
                    commons.tinymce.setContentWhenReady("tinymce_jrnlDreamCn", rsltObj.cn);
                });
            },
            /** 삭제 (ajax) */
            delAjax: function(postNo) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.JRNL_DREAM_DEL_AJAX!}";
                    const ajaxData = { "postNo": postNo };
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (!res.rslt) return;
                                if (JrnlDream.inKeywordSearchMode) {
                                    JrnlDream.keywordListAjax();
                                } else {
                                    JrnlDay.yyMnthListAjax();
                                    JrnlDreamTag.listAjax();
                                }
                                commons.util.unblockUI();
                            });
                    }, "block");
                });
            },
        }
    })();
</script>