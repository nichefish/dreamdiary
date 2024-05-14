<#--
 * 레이아웃 > 댓글 모듈
 * @included_in : 댓글 기능이 있는 페이지들
 -->

<script>
    const Comment = (function() {
        return {
            init: function (refreshFunc) {
                /* jquery :: validation */
                Comment.initValidateForm();
                commons.util.handlebarsTemplate({}, "comment_reg");
                if (refreshFunc !== undefined) {
                    Comment.refreshFunc = refreshFunc;
                }
            },
            /** form validation init */
            initValidateForm: function() {
                /* jquery :: validation */
                commons.validate.validateForm("#commentRegForm", Comment.regAjax);
            },
            /** 등록 모달 호출 */
            regModal: function(refPostNo, refContentType) {
                const obj = { "refPostNo": refPostNo, "refContentType": refContentType };
                commons.util.handlebarsTemplate(obj, "comment_reg", "show");
                /* jquery :: validation */
                Comment.initValidateForm();
            },
            /** submit */
            submit: function() {
                $("#commentRegForm").submit();
            },
            /** 댓글 입력 처리 (ajax) */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.COMMENT_REG_AJAX!}";
                    let ajaxData = new FormData($("#commentRegForm")[0]);
                    commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (!res.rslt) return;
                                if (Comment.refreshFunc !== undefined) {
                                    Comment.refreshFunc;
                                } else {
                                    commons.util.blockUIReload();
                                }
                            });
                    });
                });
            },
            /** 댓글 수정 폼 생성 */
            mdfForm: function(postNo) {
                $("#commentDtlSpan" + postNo).hide();
                let str = $("#commentMdfTemplate").html().replace(/__INDEX__/g, postNo);
                $("#commentSpan" + postNo).append(str);
                $("#commentMdfCn" + postNo).html($("#commentCnSpanDiv" + postNo).html());
                $("#showMdfBtnDiv" + postNo).hide();
                $("#mdfSaveBtnDiv" + postNo).show();
                commons.validate.validateForm("#commentMdfForm", Comment.mdfAjax);
            },
            /** 댓글 수정 폼 닫기 */
            closeMdfForm: function(postNo) {
                $("#commentDtlSpan" + postNo).show();
                $("#commentSpan" + postNo).empty();
                $("#commentMdfCn" + postNo).empty();
                $("#showMdfBtnDiv" + postNo).show();
                $("#mdfSaveBtnDiv" + postNo).hide();
            },
            /** 댓글 수정 처리 (ajax) */
            mdfAjax: function(postNo) {
                if (commons.util.isEmpty($("#commentMdfCn" + postNo), "value")) {
                    Swal.fire("댓글 내용을 입력해주세요.");
                    return false;
                }
                Swal.fire({
                    text: <@spring.message "view.cnfm.mdf"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.COMMENT_MDF_AJAX!}";
                    let ajaxData = $("#commentMdfForm" + postNo).serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (!res.rslt) return;
                                if (Comment.refreshFunc !== undefined) {
                                    Comment.refreshFunc;
                                } else {
                                    commons.util.blockUIReload();
                                }
                            });
                    });
                });
            },
            /** 댓글 삭제 처리 (ajax) */
            delAjax: function(postNo) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function (result) {
                    if (!result.value) return;
                    const url = "${Url.COMMENT_DEL_AJAX!}";
                    let ajaxData = { "postNo": postNo, "actvtyCtgrCd": "${actvtyCtgrCd!}" };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (!res.rslt) return;
                                if (Comment.refreshFunc !== undefined) {
                                    Comment.refreshFunc;
                                } else {
                                    commons.util.blockUIReload();
                                }
                            });
                    });
                });
            }
        }
    })();
    $(document).ready(function() {
        Comment.init();
    });
</script>
