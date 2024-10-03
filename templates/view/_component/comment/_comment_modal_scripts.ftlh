<#--
 * 레이아웃 > 댓글 모듈
 * @included_in : 댓글 기능이 있는 페이지들
 -->

<script>
    if (typeof Comment === 'undefined') { let Comment = {}; }
    Comment.modal = (function() {
        return {
            /**
             * Comment.modal 객체 초기화
             */
            init: function({ refreshFunc } = {}) {
                if (refreshFunc !== undefined) Comment.modal.refreshFunc = refreshFunc;
            },

            /**
             * form init
             */
            initForm: function(obj = {}) {
                commons.util.handlebarsTemplate(obj, "comment_reg", "show");

                /* jquery validation */
                commons.validate.validateForm("#commentRegForm", Comment.modal.regAjax);
            },

            /**
             * 등록 모달 호출
             */
            regModal: function(refPostNo, refContentType) {
                const obj = { "refPostNo": refPostNo, "refContentType": refContentType };
                /* init form */
                Comment.modal.initForm(obj);
            },

            /**
             * form submit
             */
            submit: function() {
                $("#commentRegForm").submit();
            },

            /**
             * 댓글 입력(등록/수정) 처리 (ajax)
             */
            regAjax: function() {
                const isReg = $("#commentRegForm #postNo").val() === "";
                Swal.fire({
                    text: <@spring.message "view.cnfm.save"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = isReg ? "${Url.COMMENT_REG_AJAX!}" : "${Url.COMMENT_MDF_AJAX!}";
                    const ajaxData = new FormData(document.getElementById("commentRegForm"));
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

            /**
             * 수정 모달 호출
             * @param {string|number} postNo - 댓글 번호.
             */
            mdfModal: function(postNo) {
                if (isNaN(postNo)) return;

                const url = "${Url.COMMENT_DTL_AJAX!}";
                const ajaxData = { "postNo" : postNo };
                commons.util.blockUIAjax(url, "get", ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    const rsltObj = res.rsltObj;
                    /* init form */
                    Comment.modal.initForm(rsltObj);
                });
            },

            /**
             * 댓글 삭제 처리 (ajax)
             * @param {string|number} postNo - 댓글 번호.
             */
            delAjax: function(postNo) {
                if (isNaN(postNo)) return;

                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
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
