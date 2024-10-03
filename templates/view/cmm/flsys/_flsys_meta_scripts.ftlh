<#--
 * 파일시스템 메타(flsys_meta) : 스크립트 모듈 분리
 -->

<script>
    const FlsysMeta = (function() {
        return {
            /**
             * FlsysMeta 객체 초기화
             */
            init: function() {
                /** jquery :: validation */
                commons.validate.validateForm("#flsysMetaRegForm", FlsysMeta.regAjax);
            },
            /** 등록 모달 조회 */
            regModal: function(filePath) {
                const obj = { "filePath": filePath, "upperFilePath": Flsys.currPath };
                commons.util.handlebarsTemplate(obj, "flsys_meta_reg", "show");

                /** tagify */
                commons.tagify.init("#tagListStr");
            },
            /** 메타정보 등록 (Ajax) */
            submit: function() {
                $("#flsysMetaRegForm").submit();
            },
            /** 메타정보 등록 (Ajax) */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.FLSYS_META_REG_AJAX!}";
                    const ajaxData = $("#flsysMetaRegForm").serializeArray();
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) Flsys.flsysRefreshAjax();
                            });
                    });
                });
            },

            /**
             * 상세 모달 호출
             * @param {string|number} postNo - 조회할 글 번호.
             */
            dtlModal: function(postNo) {
                event.stopPropagation();
                if (isNaN(postNo)) return;

                const url = "${Url.FLSYS_META_DTL_AJAX!}";
                const ajaxData = { "postNo": postNo };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltObj, "flsys_meta_dtl", "show");
                });
            },
            /** 등록 모달 조회 */
            mdfModal: function(postNo) {
                const url = "${Url.FLSYS_META_DTL_AJAX!}";
                const ajaxData = { "postNo": postNo };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    const rsltObj = res.rsltObj;
                    $("#flsys_meta_dtl_modal").modal("hide");
                    commons.util.handlebarsTemplate(rsltObj, "flsys_meta_reg", "show");
                    FlsysMeta.key = postNo;

                    /** tagify */
                    commons.tagify.init("#tagListStr");
                });
            },
            /** 메타정보 삭제 (Ajax) */
            delAjax: function(postNo) {
                if (isNaN(postNo)) return;

                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.FLSYS_META_DEL_AJAX!}";
                    const ajaxData = { "postNo": postNo };
                    commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        if (res.rslt) Flsys.flsysListAjax(Flsys.currPath);
                    });
                });
            },
        }
    })();
</script>