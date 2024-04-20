<#--
  -- 상세 코드(dtl_cd) : 스크립트 모듈 분리
  -->

<script>
    const DtlCd = (function() {
        return {
            /** Draggable 컴포넌트 init */
            initDraggable: function() {
                const containers = document.querySelectorAll(".draggable-zone");
                if (containers.length === 0) return false;
                DtlCd.swappable = new Draggable.Sortable(containers, {
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
            /** 상세코드 등록 팝업 호출 */
            regModal: function() {
                commons.util.handlebarsTemplate({ "clCd": $("#clCd").val() }, "dtl_cd_reg", "show");
                commons.validate.replaceBlankRegex("#dtlCdRegForm dtlCd");
                commons.validate.toUpperCase("#dtlCdRegForm #dtlCd");
                commons.validate.onlyNum("#dtlCdRegForm #sortOrdr");
                commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                $('[data-bs-toggle="tooltip"]').tooltip();      // template에 있는 tooltip들 활성화
            },
            /** 상세코드 등록 (Ajax) */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    $("#clCdRegForm #regYn").val("Y");
                    const url = "${Url.DTL_CD_REG_AJAX!}";
                    let ajaxData = $("#dtlCdRegForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 수정 화면 이동 */
            mdfModal: function(dtlCd) {
                const url = "${Url.DTL_CD_DTL_AJAX!}";
                let ajaxData = { "clCd": $("#clCd").val(), "dtlCd": dtlCd };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    let rsltObj = res.rsltObj;
                    rsltObj.isMdf = true;
                    commons.util.handlebarsTemplate(rsltObj, "dtl_cd_reg", "show");
                    commons.validate.replaceBlankRegex("#dtlCdRegForm #dtlCd");
                    commons.validate.toUpperCase("#dtlCdRegForm #dtlCd");
                    commons.validate.onlyNum("#dtlCdRegForm #sortOrdr");
                    commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                    $('[data-bs-toggle="tooltip"]').tooltip();      // template에 있는 tooltip들 활성화
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
                    let ajaxData = { "clCd": $("#clCd").val(), "dtlCd": dtlCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
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
                    let ajaxData = { "clCd": $("#clCd").val(), "dtlCd": dtlCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 정렬순서 저장 */
            sortOrdr: function() {
                let orderData = [];
                document.querySelectorAll('.sortable-item').forEach((item, index) => {
                    orderData.push({ "clCd": $("#clCd").val(), "dtlCd": $(item).attr("id"), "state": { "sortOrdr": index }});
                });
                const url = "${Url.DTL_CD_SORT_ORDR_AJAX!}";
                let ajaxData = { "sortOrdr": orderData };
                commons.util.blockUIJsonAjax(url, 'post', JSON.stringify(ajaxData), function(res) {
                    if (res.result) {
                        if (commons.util.isNotEmpty(res.message)) {
                            Swal.fire({ text: res.message }).then(function() { commons.util.blockUIReload(); });
                        } else {
                            commons.util.blockUIReload();
                        }
                    }
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
                    let ajaxData = { "clCd": $("#clCd").val(), "dtlCd": dtlCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
        }
    })();
</script>