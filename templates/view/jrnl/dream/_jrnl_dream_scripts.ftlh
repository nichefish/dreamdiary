<#--
  -- 저널 꿈(jrnl_dream) : 모듈 스크립트 분리
  -->

<script>
    const JrnlDream = (function() {
        return {
            /** form validation init */
            initValidateForm: function() {
                /* jquery :: validation */
                commons.validate.validateForm("#jrnlDreamRegForm", JrnlDream.regAjax);
            },
            /** 저널 꿈 등록 모달 팝업 호출 */
            regModal: function(jrnlDayNo, jrnlDt) {
                let obj = { "jrnlDayNo": jrnlDayNo, "jrnlDt": jrnlDt };
                commons.util.handlebarsTemplate(obj, "jrnl_dream_reg_header");
                commons.util.handlebarsTemplate(obj, "jrnl_dream_reg", "show");

                /* jquery :: validation */
                JrnlDream.initValidateForm();

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
                    let ajaxData = new FormData($("#jrnlDreamRegForm")[0]);
                    commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();;
                            });
                    });
                });
            },
            /** 저널 꿈 수정 모달 팝업 호출 */
            mdfModal: function(postNo) {
                const url = "${Url.JRNL_DREAM_DTL_AJAX!}";
                let ajaxData = { "postNo" : postNo };
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

                    // tinymce editor reset
                    commons.tinymce.init('#tinymce_jrnlDreamCn', commons.tinymce.imgRegFunc);
                    commons.tinymce.setContentWhenReady("tinymce_jrnlDreamCn", rsltObj.cn);
                });
            },
        }
    })();
</script>