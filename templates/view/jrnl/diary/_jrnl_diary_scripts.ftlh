<#--
 * 저널 일기(jrnl_diary) : 스크립트 모듈 분리
 -->

<#--include::script modules-->
<#include "/view/jrnl/diary/tag/_jrnl_diary_tag_ctgr_map.ftlh">

<script>
    const JrnlDiary = (function() {
        return {
            /**
             * form init
             * @param {Object} obj - 폼에 바인딩할 데이터
             */
            initForm: function(obj = {}) {
                /* show modal */
                commons.util.handlebarsTemplate(obj, "jrnl_diary_reg_header");
                commons.util.handlebarsTemplate(obj, "jrnl_diary_reg", "show");

                /* jquery validation */
                commons.validate.validateForm("#jrnlDiaryRegForm", JrnlDiary.regAjax);
                /* tagify */
                commons.tagify.initWithCtgr("#jrnlDiaryRegForm #tagListStr");
                // checkbox init
                commons.util.chckboxLabel("imprtcYn", "중요//해당없음", "red//gray");
                /* tinymce editor reset */
                commons.tinymce.init('#tinymce_jrnlDiaryCn');
                commons.tinymce.setContentWhenReady("tinymce_jrnlDiaryCn", obj.cn || "");
            },

            /**
             * 목록 조회 (Ajax)
             */
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
                    JrnlDiary.inKeywordSearchMode = true;
                }, "block");
            },

            /**
             * 등록 모달 호출
             * @param {string|number} jrnlDayNo - 저널 일자 번호.
             * @param {string} stdrdDt - 기준 날짜.
             */
            regModal: function(jrnlDayNo, stdrdDt) {
                if (isNaN(jrnlDayNo)) return;

                const obj = { "jrnlDayNo": jrnlDayNo, "stdrdDt": stdrdDt };
                /* initialize form. */
                JrnlDiary.initForm(obj);
            },

            /**
             * form submit
             */
            submit: function() {
                tinymce.get("tinymce_jrnlDiaryCn").save();
                $("#jrnlDiaryRegForm").submit();
            },

            /**
             * 등록 처리 (Ajax)
             */
            regAjax: function() {
                const isReg = $("#jrnlDiaryRegForm #postNo").val() === "";
                Swal.fire({
                    text: isReg ? <@spring.message "view.cnfm.reg"/> : <@spring.message "view.cnfm.mdf"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = isReg ? "${Url.JRNL_DIARY_REG_AJAX!}" : "${Url.JRNL_DIARY_MDF_AJAX!}";
                    const ajaxData = new FormData(document.getElementById("jrnlDiaryRegForm"));
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

            /**
             * 수정 모달 호출
             * @param {string|number} postNo - 글 번호.
             */
            mdfModal: function(postNo) {
                if (isNaN(postNo)) return;

                const url = "${Url.JRNL_DIARY_DTL_AJAX!}";
                const ajaxData = { "postNo" : postNo };
                commons.util.blockUIAjax(url, "get", ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    const { rsltObj } = res;
                    /* initialize form. */
                    JrnlDiary.initForm(rsltObj);
                });
            },

            /**
             * 삭제 처리 (Ajax)
             * @param {string|number} postNo - 글 번호.
             */
            delAjax: function(postNo) {
                if (isNaN(postNo)) return;

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