<#--
  -- 상세 코드(dtl_cd) : 스크립트 모듈 분리
  -->

<script>
    const DtlCd = (function() {
        return {
            /** form validation init */
            initValidateForm: function() {
                /* jquery :: validation */
                commons.validate.validateForm("#dtlCdRegForm", DtlCd.regAjax);
            },
            /** Draggable 컴포넌트 init */
            initDraggable: function() {
                const keyExtractor = (item) => ({ "clCd": $("#clCd").val(), "dtlCd": $(item).attr("id") });
                const url = "${Url.CL_CD_SORT_ORDR_AJAX!}";
                DtlCd.swappable = commons.util.initDraggable(keyExtractor, url);
            },
            /** 상세코드 등록 팝업 호출 */
            regModal: function() {
                commons.util.handlebarsTemplate({ "clCd": $("#clCd").val() }, "dtl_cd_reg", "show");
                /* jquery :: validation */
                DtlCd.initValidateForm();
                commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                commons.validate.replaceBlankIfMatches("#dtlCdRegForm #dtlCd", commons.validate.nonCdRegex);
                commons.validate.toUpperCase("#dtlCdRegForm #dtlCd");
            },
            /** submit */
            submit: function() {
                $("#dtlCdRegForm").submit();
            },
            /** 상세코드 등록 (Ajax) */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    $("#dtlCdRegForm #regYn").val("Y");
                    const url = "${Url.DTL_CD_REG_AJAX!}";
                    const ajaxData = $("#dtlCdRegForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            },
            /** 수정 화면 이동 */
            mdfModal: function(dtlCd) {
                const url = "${Url.DTL_CD_DTL_AJAX!}";
                const ajaxData = { "clCd": $("#clCd").val(), "dtlCd": dtlCd };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    let rsltObj = res.rsltObj;
                    rsltObj.isMdf = true;
                    commons.util.handlebarsTemplate(rsltObj, "dtl_cd_reg", "show");
                    /* jquery :: validation */
                    DtlCd.initValidateForm();
                    commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                });
            },
            /** 사용으로 변경 (Ajax) */
            useAjax: function(dtlCd) {
                event.stopPropagation();
                Swal.fire({
                    text: <@spring.message "view.cnfm.use"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = '${Url.DTL_CD_USE_AJAX!}';
                    const ajaxData = { "clCd": $("#clCd").val(), "dtlCd": dtlCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            },
            /** 미사용으로 변경 (Ajax) */
            unuseAjax: function(dtlCd) {
                event.stopPropagation();
                Swal.fire({
                    text: <@spring.message "view.cnfm.unuse"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = '${Url.DTL_CD_UNUSE_AJAX!}';
                    const ajaxData = { "clCd": $("#clCd").val(), "dtlCd": dtlCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            },
            /** 삭제 처리 (ajax) */
            delAjax: function(dtlCd) {
                event.stopPropagation();
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.DTL_CD_DEL_AJAX!}";
                    const ajaxData = { "clCd": $("#clCd").val(), "dtlCd": dtlCd };
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