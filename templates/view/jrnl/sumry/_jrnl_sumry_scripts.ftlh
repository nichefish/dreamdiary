<#--
 * 저널 결산(jrnl_sumry) : 스크립트 모듈 분리
 -->

<script>
    const JrnlSumry = (function() {
        return {
            /**
             * form init
             */
            initForm: function(obj = {}) {
                commons.util.handlebarsTemplate(obj, "jrnl_sumry_reg_header");
                commons.util.handlebarsTemplate(obj, "jrnl_sumry_reg", "show");

                /* jquery validation */
                commons.validate.validateForm("#jrnlSumryRegForm", JrnlSumry.regAjax);
                /* tagify */
                commons.tagify.initWithCtgr("#jrnlSumryRegForm #tagListStr", undefined);
                // tinymce editor reset
                commons.tinymce.init('#tinymce_jrnlSumryCn');
                commons.tinymce.setContentWhenReady("tinymce_jrnlSumryCn", obj.cn);
            },

            /**
             * 상세 화면으로 이동
             * @param {string|number} postNo - 조회할 글 번호.
             */
            dtl: function(postNo) {
                if (isNaN(postNo)) return;

                $("#procForm #postNo").val(postNo);
                commons.util.blockUISubmit("#procForm", "${Url.JRNL_SUMRY_DTL!}");
            },

            /**
             * 상세 화면으로 이동
             */
            dtlByYy: function(yy) {
                $("#procForm #yy").val(yy);
                commons.util.blockUISubmit("#procForm", "${Url.JRNL_SUMRY_DTL!}");
            },

            /**
             * 목록 화면으로 이동
             */
            list: function() {
                commons.util.blockUIReplace("${Url.JRNL_SUMRY_LIST!}");
            },

            /**
             * 특정 년도 결산 생성
             */
            makeYySumryAjax: function(yy) {
                if (yy === undefined) yy = $("#listForm #yy").val();
                if (commons.util.isEmpty(yy)) {
                    alert("yy는 필수 항목입니다.");
                    return false;
                }
                const url = "${Url.JRNL_SUMRY_MAKE_AJAX!}";
                const ajaxData = { "yy": yy };
                commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rslt) commons.util.blockUIReload();
                        });
                }, "block");
            },

            /**
             * 전체 년도 결산 갱신
             */
            makeTotalSumryAjax: function() {
                const url = "${Url.JRNL_SUMRY_MAKE_TOTAL_AJAX!}";
                commons.util.blockUIAjax(url, 'POST', null, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rslt) commons.util.blockUIReload();
                        });
                }, "block");
            },

            /**
             * 꿈 기록 완료 처리
             */
            comptAjax: function(postNo) {
                const url = "${Url.JRNL_SUMRY_DREAM_COMPT_AJAX!}";
                const ajaxData = { "postNo": postNo };
                commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rslt) commons.util.blockUIReload();
                        });
                }, "block");
            },

            /**
             * submit
             */
            submit: function() {
                tinymce.get("tinymce_jrnlSumryCn").save();
                $("#jrnlSumryRegForm").submit();
            },

            /**
             * 등록 모달 호출
             */
            regModal: function(postNo) {
                const url = "${Url.JRNL_SUMRY_DTL_AJAX!}";
                const ajaxData = { "postNo": postNo };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    const rsltObj = res.rsltObj;
                    /* init form */
                    JrnlSumry.initForm(rsltObj);
                });
            },

            /**
             * 등록 처리 (ajax)
             */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.save"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.JRNL_SUMRY_REG_AJAX!}";
                    const ajaxData = new FormData($("#jrnlSumryRegForm")[0]);
                    commons.util.blockUIMultipartAjax(url, ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (!res.rslt) return;
                                commons.util.blockUIReload();
                            });
                    }, "block");
                });
            }
        }
    })();
</script>