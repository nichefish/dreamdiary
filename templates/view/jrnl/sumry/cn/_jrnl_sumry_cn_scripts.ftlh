<#--
 * 저널 결산 내용(jrnl_sumry_cn) : 스크립트 모듈 분리
 -->

<script>
    const JrnlSumryCn = (function() {
        return {
            /** form validation init */
            initValidateForm: function() {
                /* jquery :: validation */
                commons.validate.validateForm("#jrnlSumryCnRegForm", JrnlSumryCn.regAjax);
            },
            /** 저널 일기 등록 모달 팝업 호출 */
            regModal: function(jrnlSumryNo, yy) {
                let obj = { "jrnlSumryNo": jrnlSumryNo, "yy": yy };
                commons.util.handlebarsTemplate(obj, "jrnl_sumry_cn_reg_header");
                commons.util.handlebarsTemplate(obj, "jrnl_sumry_cn_reg", "show");

                /* jquery :: validation */
                JrnlSumryCn.initValidateForm();
                // checkbox init
                commons.util.chckboxLabel("imprtcYn", "중요//해당없음", "red//gray");
                // tinymce editor reset
                commons.tinymce.init('#tinymce_jrnlSumryCnCn', commons.tinymce.imgRegFunc);
                commons.tinymce.setContentWhenReady("tinymce_jrnlSumryCnCn", "");
            },
            /** 이관 submit */
            submit: function() {
                tinymce.get("tinymce_jrnlSumryCnCn").save();
                $("#jrnlSumryCnRegForm").submit();
            },
            /** 등록 */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.JRNL_SUMRY_CN_REG_AJAX!}";
                    let ajaxData = new FormData($("#jrnlSumryCnRegForm")[0]);
                    commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            },
            /** 저널 꿈 수정 모달 팝업 호출 */
            mdfModal: function(postNo) {
                const url = "${Url.JRNL_SUMRY_CN_DTL_AJAX!}";
                let ajaxData = { "postNo" : postNo };
                commons.util.blockUIAjax(url, "get", ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    const rsltObj = res.rsltObj;

                    commons.util.handlebarsTemplate(rsltObj, "jrnl_sumry_cn_reg_header");
                    commons.util.handlebarsTemplate(rsltObj, "jrnl_sumry_cn_reg", "show");

                    /* jquery :: validation */
                    JrnlSumryCn.initValidateForm();
                    // checkbox init
                    commons.util.chckboxLabel("imprtcYn", "중요//해당없음", "red//gray");
                    // tinymce editor reset
                    commons.tinymce.init('#tinymce_jrnlSumryCnCn', commons.tinymce.imgRegFunc);
                    commons.tinymce.setContentWhenReady("tinymce_jrnlSumryCnCn", rsltObj.cn);
                });
            },
            /** 삭제 (ajax) */
            delAjax: function(postNo) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.JRNL_SUMRY_CN_DEL_AJAX!}";
                    let ajaxData = { "postNo": postNo };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            },
        }
    })();
</script>