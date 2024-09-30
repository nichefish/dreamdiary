<#--
  -- 분류 코드(cl_cd) : 스크립트 모듈 분리
  -->

<script>
    const ClCd = (function() {
        return {
            /** form validation init */
            initValidateForm: function() {
                /* jquery :: validation */
                commons.validate.validateForm("#clCdRegForm", ClCd.regAjax);
            },
            /** Draggable 컴포넌트 init */
            initDraggable: function() {
                const keyExtractor = (item) => ({ "clCd": $(item).attr("id") });
                const url = "${Url.CL_CD_SORT_ORDR_AJAX!}";
                ClCd.swappable = commons.util.initDraggable(keyExtractor, url);
            },
            /** 목록 검색 */
            search: function() {
                $("#listForm #pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${Url.CL_CD_LIST!}?actionTyCd=SEARCH");
            },
            /** 등록 팝업 호출 */
            regModal: function() {
                commons.util.handlebarsTemplate({}, "cl_cd_reg", "show");
                /* jquery :: validation */
                ClCd.initValidateForm();
                commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                commons.validate.replaceBlankIfMatches("#clCdRegForm #clCd", commons.validate.nonCdRegex);
                commons.validate.toUpperCase("#clCdRegForm #clCd");
            },
            /** submit */
            submit: function() {
                $("#clCdRegForm").submit();
            },
            /** 등록 또는 수정 처리(Ajax) */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    $("#clCdRegForm #regYn").val("Y");
                    const url = "${Url.CL_CD_REG_AJAX!}";
                    let ajaxData = $("#clCdRegForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            },
            /** 상세 화면 이동 */
            dtl: function(clCd) {
                commons.util.blockUIRequest();
                $("#procForm #clCd").val(clCd);
                commons.util.blockUISubmit("#procForm", "${Url.CL_CD_DTL!}");
            },
            /** 상세 모달 팝업 호출 */
            dtlAjax: function(clCd) {
                event.stopPropagation();
                const url = "${Url.CL_CD_DTL_AJAX!}";
                const ajaxData = { "clCd": clCd };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltObj, "cl_cd_dtl", "show");
                    ClCd.key = clCd;
                });
            },
            /** 수정 모달 호출 */
            mdfModal: function(clCd) {
                const url = "${Url.CL_CD_DTL_AJAX!}";
                const ajaxData = { "clCd": clCd };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    let rsltObj = res.rsltObj;
                    rsltObj.isMdf = true;
                    commons.util.handlebarsTemplate(rsltObj, "cl_cd_reg", "show");
                    $('#cl_cd_dtl_modal').modal('hide');
                    /* jquery :: validation */
                    ClCd.initValidateForm();
                    commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                });
            },
            /** 사용으로 변경 (Ajax) */
            useAjax: function(clCd) {
                event.stopPropagation();
                Swal.fire({
                    text: <@spring.message "view.cnfm.use"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = '${Url.CL_CD_USE_AJAX!}';
                    const ajaxData = { "clCd": clCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            },
            /** 미사용으로 변경 (Ajax) */
            unuseAjax: function(clCd) {
                event.stopPropagation();
                Swal.fire({
                    text: <@spring.message "view.cnfm.unuse"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = '${Url.CL_CD_UNUSE_AJAX!}';
                    const ajaxData = { "clCd": clCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            },
            /** 삭제 처리 (ajax) */
            delAjax: function(clCd) {
                event.stopPropagation();
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.CL_CD_DEL_AJAX!}";
                    const ajaxData = { "clCd" : clCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            },
            /** 목록 화면 이동 */
            list: function() {
                const listUrl = "${Url.CL_CD_LIST!}?isBackToList=Y";
                commons.util.blockUIReplace(listUrl);
            },
        }
    })();
</script>