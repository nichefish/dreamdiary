<#--
 * 메뉴(menu) : 스크립트 모듈 분리
 -->

<script>
    const Menu = (function() {
        return {
            /**
             * form init
             * @param {Object} obj - 폼에 바인딩할 데이터
             */
            initForm: function(obj = {}) {
                /* show modal */
                commons.util.handlebarsTemplate(obj, "menu_reg", "show");

                /* jquery validation */
                commons.validate.validateForm("#menuRegForm", Menu.regAjax);
            },

            /**
             * Draggable 컴포넌트 init
             */
            initDraggable: function() {
                const mainContainers = document.querySelectorAll(".draggable-zone-main");
                if (mainContainers.length === 0) return false;

                // 드래그 전 초기 정렬 순서 저장
                const initOrdr = Array.from(containers[0].querySelectorAll('.draggable'))
                    .map(draggable => $(draggable).attr("id"));

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

                    // 드래그 후 정렬 순서 가져오기
                    const newOrdr = Array.from(containers[0].querySelectorAll('.draggable'))
                        .map(draggable => $(draggable).attr("id"));
                    const isOrdrChanged = JSON.stringify(initOrdr) !== JSON.stringify(newOrdr);

                    // 정렬 순서 ajax 저장
                    const keyExtractor = (item) => ({ "menuNo": Number($(item).attr("id")), "upperMenuNo": Number($(item).attr("upper-menu-no")) });
                    const url = "${Url.MENU_SORT_ORDR_AJAX!}";
                    if (isOrdrChanged) commons.util.sortOrdr(keyExtractor, url);
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

            /**
             * 등록 모달 호출
             */
            regModal: function(menuTyCd, upperMenuNo, upperMenuNm) {
                event.stopPropagation();

                const obj = { "menuTyCd": menuTyCd, "upperMenuNo": upperMenuNo, "upperMenuNm": upperMenuNm };
                /* initialize form. */
                Menu.initForm(obj);
            },

            /**
             * 아이콘 새로고침
             */
            refreshIcon: function() {
                const $iconClassElmt = $("#menuRegForm #icon");
                if (!$iconClassElmt.length) return;
                $("#menuRegForm #menu_icon_div").html($iconClassElmt.val());
            },

            /**
             * 하위메뉴 존재여부에 따라 url 영역 표시
             */
            toggleUrlSpan: function(obj) {
                const menuSubExtendTyCd = $(obj).val();
                if (menuSubExtendTyCd !== "NO_SUB") {
                    $("#url_div").addClass("d-none");
                } else {
                    $("#url_div").removeClass("d-none");
                }
            },

            /**
             * form submit
             */
            submit: function() {
                $("#menuRegForm").submit();
            },

            /**
             * 메뉴 정보 등록/수정 (Ajax)
             */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.save"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    let isReg = $("#menuRegForm #menuNo").val() === "";
                    const url = isReg ? "${Url.MENU_REG_AJAX!}" : "${Url.MENU_MDF_AJAX!}";
                    const ajaxData = $("#menuRegForm").serializeArray();
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            },

            /**
             * 수정 모달 호출
             */
            mdfModal: function(menuNo) {
                if (isNaN(menuNo)) return;

                const url = "${Url.MENU_DTL_AJAX!}";
                const ajaxData = { "menuNo": menuNo };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    const { rsltObj } = res;
                    /* initialize form. */
                    Menu.initForm(rsltObj);
                });
            },

            /**
             * 삭제 (Ajax)
             * @param {string|number} menuNo - 메뉴 번호.
             */
            delAjax: function(menuNo) {
                if (isNaN(menuNo)) return;

                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.MENU_DEL_AJAX!}";
                    const ajaxData = { "menuNo": menuNo };
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            },

            /**
             * 목록 검색
             */
            search: function() {
                $("#listForm #pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${Url.LOG_ACTVTY_LIST!}?actionTyCd=SEARCH");
            },

            /**
             * 엑셀 다운로드
             */
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