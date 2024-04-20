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
                const containers = document.querySelectorAll(".draggable-zone");
                if (containers.length === 0) return false;
                ClCd.swappable = new Draggable.Sortable(containers, {
                    draggable: ".draggable",
                    handle: ".draggable .draggable-handle",
                    mirror: {
                        //appendTo: selector,
                        appendTo: "body",
                        constrainDimensions: true
                    },
                }).on('drag:start', (event) => {
                    $(event.data.source).addClass('dragging');
                }).on('drag:stop', (event) => {
                    const id = $(event.data.source).attr("id");
                    setTimeout(() => {
                        const $newTr = $("tr#"+id);
                        $newTr.removeClass('dragging').addClass("draggable-modified");
                    }, 0); // 지연 시간을 0으로 설정하여 다음 이벤트 루프에서 실행되도록 함
                });
            },
            /** 목록 검색 */
            search: function() {
                $("#listForm #pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${Url.CL_CD_LIST!}?actionTyCd=SEARCH");
            },
            /** 등록 팝업 호출 */
            regModal: function() {
                /* jquery :: validation */
                ClCd.initValidateForm();
                commons.util.handlebarsTemplate({}, "cl_cd_reg", "show");
                commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                commons.validate.replaceBlankIfMatches("#clCdRegForm #clCd", commons.validate.nonCdRegex);
                commons.validate.toUpperCase("#clCdRegForm #clCd");
                // template에 있는 tooltip들 활성화
                $('[data-bs-toggle="tooltip"]').tooltip();
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
                    ClCd.key = clCd;
                });
            },
            /** 수정 모달 호출 */
            mdfModal: function(clCd) {
                const url = "${Url.CL_CD_DTL_AJAX!}";
                let ajaxData = { "clCd": clCd };
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
                    // template에 있는 tooltip들 활성화
                    $('[data-bs-toggle="tooltip"]').tooltip();
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