<#--
 * 레이아웃 > 댓글 모듈
 * @included_in : 댓글 기능이 있는 페이지들
 -->

<script>
    if (typeof Comment === 'undefined') { let  Comment = {}; }
    Comment.modal = (function() {
        return {
            init: function ({ refreshFunc } = {}) {
                /* jquery :: validation */
                Comment.modal.initValidateForm();
                commons.util.handlebarsTemplate({}, "comment_reg");
                if (refreshFunc !== undefined) Comment.modal.refreshFunc = refreshFunc;
            },
            /** form validation init */
            initValidateForm: function() {
                /* jquery :: validation */
                commons.validate.validateForm("#commentRegForm", Comment.modal.regAjax);
            },
            /** 등록 모달 호출 */
            regModal: function(refPostNo, refContentType) {
                const obj = { "refPostNo": refPostNo, "refContentType": refContentType };
                commons.util.handlebarsTemplate(obj, "comment_reg", "show");
                /* jquery :: validation */
                Comment.modal.initValidateForm();
            },
            /** submit */
            submit: function() {
                $("#commentRegForm").submit();
            },
            /** 댓글 입력 처리 (ajax) */
            regAjax: function() {
                const isReg = $("#commentRegForm #postNo").val() === "";
                Swal.fire({
                    text: isReg ? <@spring.message "view.cnfm.reg"/> : <@spring.message "view.cnfm.mdf"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = isReg ? "${Url.COMMENT_REG_AJAX!}" : "${Url.COMMENT_MDF_AJAX!}";
                    const ajaxData = new FormData($("#commentRegForm")[0]);
                    commons.util.blockUIMultipartAjax(url, ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (!res.rslt) return;
                                if (Comment.modal.refreshFunc !== undefined) {
                                    Comment.modal.refreshFunc();
                                } else {
                                    commons.util.blockUIReload();
                                }
                            });
                    });
                });
            },
            /** 수정 모달 호출 */
            mdfModal: function(postNo) {
                const url = "${Url.COMMENT_DTL_AJAX!}";
                const ajaxData = { "postNo" : postNo };
                commons.util.blockUIAjax(url, "get", ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    const rsltObj = res.rsltObj;

                    commons.util.handlebarsTemplate(rsltObj, "comment_reg", "show");
                    /* jquery :: validation */
                    Comment.modal.initValidateForm();
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
                    const ajaxData = { "postNo": postNo, "actvtyCtgrCd": "${actvtyCtgrCd!}" };
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (!res.rslt) return;
                                if (Comment.modal.refreshFunc !== undefined) {
                                    Comment.modal.refreshFunc();
                                } else {
                                    commons.util.blockUIReload();
                                }
                            });
                    });
                });
            }
        }
    })();
    document.addEventListener("DOMContentLoaded", function() {
        Comment.modal.init();
    });
</script>
