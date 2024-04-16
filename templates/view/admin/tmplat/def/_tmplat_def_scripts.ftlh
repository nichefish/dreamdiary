<#--
  -- 템플릿 관리(tmplat) : 모듈 스크립트 분리
  -->

<script>
    const TmplatDef = (function() {
        return {
            /** form validation init */
            initValidateForm: function() {
                /* jquery :: validation */
                commons.validate.validateForm("#menuRegForm", Menu.regAjax);
            },
            /** Draggable 컴포넌트 init */
            initDraggable: function() {
                const mainContainers = document.querySelectorAll(".draggable-zone-main");
                if (mainContainers.length === 0) return false;
                Menu.mainSwappable = new Draggable.Sortable(mainContainers, {
                    draggable: ".draggable-main",
                    handle: ".draggable-main .draggable-handle-main",
                    mirror: {
                        //appendTo: selector,
                        appendTo: "body",
                        constrainDimensions: true
                    },
                }).on('drag:start', (event) => {
                    const $card = $(event.data.source).find(".card");
                    $card.addClass('dragging');
                }).on('drag:stop', (event) => {
                    const $card = $(event.data.source).find(".card");
                    $card.removeClass('dragging');
                });

                const subContainers = document.querySelectorAll(".draggable-zone-sub");
                if (subContainers.length === 0) return false;
                Menu.subSwappable = new Draggable.Sortable(subContainers, {
                    draggable: ".draggable-sub",
                    handle: ".draggable-sub .draggable-handle-sub",
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
                        const $newLi = $("li#"+id);
                        const $newParent = $newLi.parents("li.sortable-item");
                        const newParentId = $newParent.closest("li.sortable-item").attr('id');
                        $newLi.attr('upper-menu-no', newParentId);
                        $newLi.removeClass('dragging').addClass("draggable-modified");
                    }, 0); // 지연 시간을 0으로 설정하여 다음 이벤트 루프에서 실행되도록 함
                });
            },
            /** 등록 모달 팝업 호출 */
            regModal: function(menuTyCd, upperMenuNo, upperMenuNm) {
                event.stopPropagation();
                let obj = { "menuTyCd": menuTyCd, "upperMenuNo": upperMenuNo, "upperMenuNm": upperMenuNm };
                commons.util.handlebarsTemplate(obj, "menu_reg", "show");
            },
            /** 아이콘 새로고침 */
            refreshIcon: function() {
                const $iconClassElmt = $("#menuRegForm #icon");
                if (!$iconClassElmt.length) return;
                $("#menuRegForm #menu_icon_div").html($iconClassElmt.val());
            },
            /** 메뉴 정보 등록/수정 */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.save"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    let isReg = $("#menuRegForm #menuNo").val() === "";
                    const url = isReg ? "${Url.MENU_REG_AJAX!}" : "${Url.MENU_MDF_AJAX!}";
                    let ajaxData = $("#menuRegForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 수정 모달 팝업 호출 */
            mdfModal: function(menuNo) {
                const url = "${Url.MENU_DTL_AJAX!}";
                let ajaxData = { "menuNo": menuNo };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltObj, "menu_reg", "show");
                });
            },
            /** 삭제 처리 (ajax) */
            delAjax: function(menuNo) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.MENU_DEL_AJAX!}";
                    let ajaxData = { "menuNo": menuNo };
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
                    orderData.push({ "menuNo": Number($(item).attr("id")), "upperMenuNo": Number($(item).attr("upper-menu-no")), "state": { "sortOrdr": index }});
                });
                const url = "${Url.MENU_SORT_ORDR_AJAX!}";
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
            /** 목록 검색 */
            search: function() {
                $("#listForm #pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${Url.LOG_ACTVTY_LIST!}?actionTyCd=SEARCH");
            },
            /** 엑셀 다운로드 */
            xlsxDownload: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.download"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    commons.util.blockUIFileDownload();
                    $("#listForm").attr("action", "${Url.LOG_ACTVTY_LIST_XLSX_DOWNLOAD!}").submit();
                });
            },
        }
    })();
</script>