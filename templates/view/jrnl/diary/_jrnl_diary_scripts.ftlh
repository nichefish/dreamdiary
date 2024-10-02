<#--
 * 저널 일기(jrnl_diary) : 스크립트 모듈 분리
 -->

<#--include::script modules-->
<#include "/view/jrnl/diary/tag/_jrnl_diary_tag_ctgr_map.ftlh">

<script>
    const JrnlDiary = (function() {
        return {
            /** form validation init */
            initValidateForm: function() {
                /* jquery :: validation */
                commons.validate.validateForm("#jrnlDiaryRegForm", JrnlDiary.regAjax);
            },
            /** 목록 조회 */
            keywordListAjax: function() {
                const keyword = $("#jrnl_aside #diaryKeyword").val();
                if (commons.util.isEmpty(keyword)) return;
                const url = "${Url.JRNL_DIARY_LIST_AJAX!}";
                const ajaxData = { "diaryKeyword": $("#jrnl_aside #diaryKeyword").val() };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    $("#jrnl_aside #yy").val("");
                    $("#jrnl_aside #mnth").val("");
                    // 목록 영역 클리어
                    $("#jrnl_aside #dreamKeyword").val("");
                    $("#jrnl_day_list_div").empty();
                    $("#jrnl_dream_list_div").empty();
                    // 태그 헤더 클리어
                    $("#jrnl_day_tag_list_div").empty();
                    $("#jrnl_diary_tag_list_div").empty();
                    $("#jrnl_dream_tag_list_div").empty();
                    commons.util.closeModal();
                    commons.util.handlebarsTemplate(res.rsltList, "jrnl_diary_list");
                    $("[data-bs-toggle='tooltip']").tooltip();
                    JrnlDiary.inKeywordSearchMode = true;
                }, "block");
            },
            /** 저널 일기 등록 모달 팝업 호출 */
            regModal: function(jrnlDayNo, stdrdDt) {
                const obj = { "jrnlDayNo": jrnlDayNo, "stdrdDt": stdrdDt };
                commons.util.handlebarsTemplate(obj, "jrnl_diary_reg_header");
                commons.util.handlebarsTemplate(obj, "jrnl_diary_reg", "show");

                /* jquery :: validation */
                JrnlDiary.initValidateForm();
                /* tagify */
                commons.tagify.initWithCtgr("#jrnlDiaryRegForm #tagListStr");

                // checkbox init
                commons.util.chckboxLabel("imprtcYn", "중요//해당없음", "red//gray");
                // tinymce editor reset
                commons.tinymce.init('#tinymce_jrnlDiaryCn', commons.tinymce.imgRegFunc);
                commons.tinymce.setContentWhenReady("tinymce_jrnlDiaryCn", "");
            },
            /** 이관 submit */
            submit: function() {
                tinymce.get("tinymce_jrnlDiaryCn").save();
                $("#jrnlDiaryRegForm").submit();
            },
            /** 등록 */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.JRNL_DIARY_REG_AJAX!}";
                    const ajaxData = new FormData($("#jrnlDiaryRegForm")[0]);
                    commons.util.blockUIMultipartAjax(url, ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (!res.rslt) return;
                                if (JrnlDiary.inKeywordSearchMode) {
                                    JrnlDiary.keywordListAjax();
                                } else {
                                    JrnlDay.yyMnthListAjax();
                                    JrnlDiaryTag.listAjax();
                                }
                                // TODO: 결산 페이지에서 처리시도 처리해 줘야 한다.
                                commons.util.unblockUI();
                            });
                    }, "block");
                });
            },
            /** 저널 꿈 수정 모달 팝업 호출 */
            mdfModal: function(postNo) {
                const url = "${Url.JRNL_DIARY_DTL_AJAX!}";
                const ajaxData = { "postNo" : postNo };
                commons.util.blockUIAjax(url, "get", ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    const rsltObj = res.rsltObj;

                    commons.util.handlebarsTemplate(rsltObj, "jrnl_diary_reg_header");
                    commons.util.handlebarsTemplate(rsltObj, "jrnl_diary_reg", "show");

                    /* jquery :: validation */
                    JrnlDiary.initValidateForm();
                    /* tagify */
                    commons.tagify.initWithCtgr("#jrnlDiaryRegForm #tagListStr");

                    // checkbox init
                    commons.util.chckboxLabel("imprtcYn", "중요//해당없음", "red//gray");
                    // tinymce editor reset
                    commons.tinymce.init('#tinymce_jrnlDiaryCn', commons.tinymce.imgRegFunc);
                    commons.tinymce.setContentWhenReady("tinymce_jrnlDiaryCn", rsltObj.cn);
                });
            },
            /** 삭제 (ajax) */
            delAjax: function(postNo) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.JRNL_DIARY_DEL_AJAX!}";
                    const ajaxData = { "postNo": postNo };
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) {
                                    JrnlDay.yyMnthListAjax();
                                    JrnlDiaryTag.listAjax();
                                }
                            });
                    }, "block");
                });
            },
        }
    })();
</script>