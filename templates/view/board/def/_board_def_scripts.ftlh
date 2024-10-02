<#--
 * 게시판 정의(board_def) : 스크립트 모듈 분리
 -->

<script>
    const BoardDef = (function() {
        return {
            /** form validation init */
            initValidateForm: function() {
                /* jquery :: validation */
                commons.validate.validateForm("#boardDefRegForm", BoardDef.regAjax);
            },
            /** Draggable 컴포넌트 init */
            initDraggable: function() {
                const keyExtractor = (item) => ({ "boardCd": $(item).attr("id") });
                const url = "${Url.BOARD_DEF_SORT_ORDR_AJAX!}";
                BoardDef.swappable = commons.util.initDraggable(keyExtractor, url);
            },
            /** 등록 모달 호출 */
            regModal: function() {
                commons.util.handlebarsTemplate({}, "board_def_reg", "show");
                /* jquery :: validation */
                BoardDef.initValidateForm();
                commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                commons.validate.replaceBlankIfMatches("#boardDefRegForm .cddata", commons.validate.nonCdRegex);
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
                    const ajaxData = $("#boardDefRegForm").serializeArray();
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            },
            /** 수정 모달 호출 */
            mdfModal: function(boardCd) {
                const url = "${Url.BOARD_DEF_DTL_AJAX!}";
                const ajaxData = { "boardCd": boardCd };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    let rsltObj = res.rsltObj;
                    rsltObj.isMdf = true;
                    commons.util.handlebarsTemplate(rsltObj, "board_def_reg", "show");
                    /* jquery :: validation */
                    BoardDef.initValidateForm();
                    commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
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
                    const ajaxData = { "boardCd": key };
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
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
                    const ajaxData = { "boardCd": key };
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
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
                    const ajaxData = { "boardCd": key };
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
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