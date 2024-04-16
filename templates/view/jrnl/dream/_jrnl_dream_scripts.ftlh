<#--
  -- 저널 꿈(jrnl_dream) : 모듈 스크립트 분리
  -- @included_in : jrnl_day_page.ftlh
  -->

<script>
    const JrnlDream = (function() {
        return {
            init: function () {
                /** jquery :: validation */
                commons.validate.validateForm("#jrnlDreamRegForm", JrnlDream.regAjax);
            },
            /** 저널 꿈 등록 모달 팝업 호출 */
            regModal: function(jrnlDayNo, jrnlDt) {
                let obj = { "jrnlDayNo": jrnlDayNo, "jrnlDt": jrnlDt };
                commons.util.handlebarsTemplate(obj, "jrnl_dream_reg_header");
                commons.util.handlebarsTemplate(obj, "jrnl_dream_reg", "show");

                // tinymce editor reset
                tinymce.EditorManager.editors.splice(2, 2);     //removing second element in array.
                delete tinymce.EditorManager.editors['tinymce_jrnlDreamCn'];     //deleting respective textarea id from array
                if (commons.util.isNotEmpty(tinyMCE.activeEditor)) tinyMCE.activeEditor.setContent("");
                commons.tinymce.init('#tinymce_jrnlDreamCn', commons.tinymce.imgRegFunc);
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
            mdfModal: function(jrnlDayNo, jrnlDt) {
                let obj = { "jrnlDayNo": jrnlDayNo, "jrnlDt": jrnlDt };
                commons.util.handlebarsTemplate(obj, "jrnl_dream_reg_header");
                commons.util.handlebarsTemplate(obj, "jrnl_dream_reg", "show");

                // tinymce editor reset
                tinymce.EditorManager.editors.splice(2, 2);     //removing second element in array.
                delete tinymce.EditorManager.editors['tinymce_jrnlDreamCn'];     //deleting respective textarea id from array
                if (commons.util.isNotEmpty(tinyMCE.activeEditor)) tinyMCE.activeEditor.setContent("");
                commons.tinymce.init('#tinymce_jrnlDreamCn', commons.tinymce.imgRegFunc);
            },
        }
    })();
</script>