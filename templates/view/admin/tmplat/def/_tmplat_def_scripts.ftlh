<#--
  -- 템플릿 관리(tmplat) : 스크립트 모듈 분리
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
                const keyExtractor = document.querySelectorAll(".draggable-zone");
                if (mainContainers.length === 0) return false;
                TmplatDef.swappable = commons.util.initDraggable(keyExtractor, url);
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
            /** submit */
            submit: function() {
                $("#tmplatDefRegForm").submit();
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
                    let ajaxData = $("#tmplatDefRegForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            },
            /** 수정 모달 팝업 호출 */
            mdfModal: function(menuNo) {
                const url = "${Url.MENU_DTL_AJAX!}";
                let ajaxData = { "menuNo": menuNo };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
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
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
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