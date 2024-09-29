<#--
 * 레이아웃 > 단락(section) 모듈
 * @included_in : 단락 기능이 있는 페이지들
 -->

<script>
    const Sectn = (function() {
        return {
            init: function (refreshFunc) {
                if (refreshFunc !== undefined) Sectn.refreshFunc = refreshFunc;
            },
            /** form validation init */
            initValidateForm: function() {
                /* jquery :: validation */
                commons.validate.validateForm("#sectnRegForm", Sectn.regAjax);
            },
            /** 등록 모달 호출 */
            regModal: function(refPostNo, refContentType) {
                const obj = { "refPostNo": refPostNo, "refContentType": refContentType };
                commons.util.handlebarsTemplate(obj, "sectn_reg_header");
                commons.util.handlebarsTemplate(obj, "sectn_reg", "show");
                /* jquery :: validation */
                Sectn.initValidateForm();
                // checkbox init
                commons.util.chckboxLabel("deprcYn", "만료//해당없음", "red//gray");
                /* tagify */
                commons.tagify.initWithCtgr("#sectnRegForm #tagListStr", undefined);
                // tinymce editor reset
                commons.tinymce.init('#tinymce_sectnCn', commons.tinymce.imgRegFunc);
                commons.tinymce.setContentWhenReady("tinymce_sectnCn", "");
            },
            /** submit */
            submit: function() {
                tinymce.activeEditor.save();
                $("#sectnRegForm").submit();
            },
            /** 댓글 입력 처리 (ajax) */
            regAjax: function() {
                const isReg = $("#sectnRegForm #postNo").val() === "";
                Swal.fire({
                    text: isReg ? <@spring.message "view.cnfm.reg"/> : <@spring.message "view.cnfm.mdf"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = isReg ? "${Url.SECTN_REG_AJAX!}" : "${Url.SECTN_MDF_AJAX!}";
                    let ajaxData = new FormData($("#sectnRegForm")[0]);
                    commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (!res.rslt) return;
                                if (Sectn.refreshFunc !== undefined) {
                                    Sectn.refreshFunc();
                                } else {
                                    commons.util.blockUIReload();
                                }
                            });
                    });
                });
            },
            /** 수정 모달 호출 */
            mdfModal: function(postNo) {
                const url = "${Url.SECTN_DTL_AJAX!}";
                let ajaxData = { "postNo" : postNo };
                commons.util.blockUIAjax(url, "get", ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    const rsltObj = res.rsltObj;

                    commons.util.handlebarsTemplate(rsltObj, "sectn_reg_header");
                    commons.util.handlebarsTemplate(rsltObj, "sectn_reg", "show");
                    /* jquery :: validation */
                    Sectn.initValidateForm();
                    // checkbox init
                    commons.util.chckboxLabel("deprcYn", "만료//해당없음", "red//gray");
                    /* tagify */
                    commons.tagify.initWithCtgr("#sectnRegForm #tagListStr", undefined);
                    // tinymce editor reset
                    commons.tinymce.init('#tinymce_sectnCn', commons.tinymce.imgRegFunc);
                    commons.tinymce.setContentWhenReady("tinymce_sectnCn", rsltObj.cn);
                });
            },
            /** 댓글 삭제 처리 (ajax) */
            delAjax: function(postNo) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function (result) {
                    if (!result.value) return;
                    const url = "${Url.SECTN_DEL_AJAX!}";
                    let ajaxData = { "postNo": postNo, "actvtyCtgrCd": "${actvtyCtgrCd!}" };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (!res.rslt) return;
                                if (Sectn.refreshFunc !== undefined) {
                                    Sectn.refreshFunc();
                                } else {
                                    commons.util.blockUIReload();
                                }
                            });
                    });
                });
            },
        }
    })();
    $(document).ready(function() {
        Sectn.init();
    });
</script>
