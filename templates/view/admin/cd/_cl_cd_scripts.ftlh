<#--
  -- 분류 코드(cl_cd) : 모듈 스크립트 분리
  -->

<script>
    const ClCd = (function() {
        return {
            /** 목록 검색 */
            search: function() {
                $("#listForm #pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${Url.CL_CD_LIST!}?actionTyCd=SEARCH");
            },
            /** 등록 팝업 호출 */
            regModal: function() {
                commons.util.handlebarsTemplate({}, "cl_cd_reg", "show");
                commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                commons.validate.replaceBlankRegex("#clCdRegForm #clCd");
                commons.validate.toUpperCase("#clCdRegForm #clCd");
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
                    });
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
                let ajaxData = { "clCd": clCd };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltObj, "cl_cd_dtl", "show");
                });
            },
            /** 수정 화면 이동 */
            mdfModal: function() {
                const url = "${Url.CL_CD_DTL_AJAX!}";
                let ajaxData = { "clCd": $("#clCd").val() };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    let rsltObj = res.rsltObj;
                    rsltObj.isMdf = true;
                    commons.util.handlebarsTemplate(rsltObj, "cl_cd_reg", "show");
                    commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                    commons.validate.replaceBlankRegex("#clCdRegForm #clCd");
                    $('[data-bs-toggle="tooltip"]').tooltip();      // template에 있는 tooltip들 활성화
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
                    let ajaxData = { "clCd": clCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    });
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
                    let ajaxData = { "clCd": clCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 정렬순서 저장 */
            sortOrdr: function() {
                let orderData = [];
                document.querySelectorAll('.sortable-item').forEach((item, index) => {
                    orderData.push({ "clCd": $(item).attr("id"), "state": { "sortOrdr": index }});
                });
                const url = "${Url.CL_CD_SORT_ORDR_AJAX!}";
                let ajaxData = { "sortOrdr": orderData };
                commons.util.blockUIJsonAjax(url, 'post', JSON.stringify(ajaxData), function(res) {
                    if (res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) {
                            Swal.fire({ text: res.message }).then(function() { commons.util.blockUIReload(); });
                        } else {
                            commons.util.blockUIReload();
                        }
                    }
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
                    let ajaxData = { "clCd" : clCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    });
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