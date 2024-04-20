<#--
 * 게시판 정의(board_def) : 스크립트 모듈 분리
 -->

<script>
    const BoardDef = (function() {
        return {
            /** Draggable 컴포넌트 init */
            initDraggable: function() {
                const containers = document.querySelectorAll(".draggable-zone");
                if (containers.length === 0) return false;
                BoardDef.swappable = new Draggable.Sortable(containers, {
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
            /** 등록 모달 호출 */
            regModal: function() {
                commons.util.handlebarsTemplate({}, "board_def_reg", "show");
                $('[data-bs-toggle="tooltip"]').tooltip();      // template에 있는 tooltip들 활성화
                commons.validate.validateForm("#boardDefRegForm");
                commons.validate.replaceBlankIfMatches("#boardDefRegForm .cddata", commons.validate.nonCdRegex);
                commons.validate.onlyEngNum("#boardDefRegForm .cddata");
                // commons.validate.toUpperCase("#boardDefRegForm .cddata");
                commons.validate.onlyNum(".number");
            },
            /** submit */
            submit: function() {
                $("#boardDefRegForm").submit();
            },
            /** 등록 (ajax) */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = '${Url.BOARD_DEF_REG_AJAX!}';
                    let ajaxData = $("#boardDefRegForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 항목 수정 모달 호출 */
            mdfItemForm: function(boardCd, mdfItem, mdfItemNm, mdfItemVal) {
                event.stopPropagation();
                // 태그 상세 정보
                let data = { "boardCd": boardCd, "mdfItem": mdfItem, "mdfItemNm": mdfItemNm, "mdfItemVal": mdfItemVal };
                commons.util.handlebarsTemplate(data, "board_def_mdf_item", "show");
            },
            /** 항목 수정 (Ajax) */
            mdfItemAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.mdf"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = '${Url.BOARD_DEF_MDF_ITEM_AJAX!}';
                    let ajaxData = $("#boardDefMdfItemForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 사용으로 변경 (Ajax) */
            useAjax: function(key) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.use"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = '${Url.BOARD_DEF_USE_AJAX!}';
                    let ajaxData = { "boardCd": key };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 미사용으로 변경 (Ajax) */
            unuseAjax: function(key) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.unuse"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = '${Url.BOARD_DEF_UNUSE_AJAX!}';
                    let ajaxData = { "boardCd": key };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 삭제 처리 (ajax) */
            delAjax: function(key) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.BOARD_DEF_DEL_AJAX!}";
                    let ajaxData = { "boardCd": key };
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
                    orderData.push({ "boardCd": $(item).attr("id"), "state": { "sortOrdr": index }});
                });
                const url = "${Url.BOARD_DEF_SORT_ORDR_AJAX!}";
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
            }
        }
    })();
</script>