<#--
 -- 활동 로그 목록 화면
 -->
<#import "/layout/layout.ftlh" as layout >

<#-- 툴바 버튼 추가 -->
<#assign toolbar>
    <#include "/view/admin/menu/_menu_page_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<link rel="stylesheet" type="text/css" href="/lib/jstree/css/style.min.css?${releaseDate!}" />
<script type="text/javascript" src="/lib/jstree/js/jstree.min.js?${releaseDate!}"></script>
<script type="text/javascript" src="/js/commons/commons-jstree.js?${releaseDate!}"></script>
<script type="text/javascript" src="/metronic/assets/plugins/custom/draggable/draggable.bundle.js?${releaseDate!}"></script>

<script>
    const Page = function() {
        return {
            colorArray: ['#185ADB','#FFC107','#00ADB5','#7952B3','#FFD93D','#4D96FF','#8479E1','#332FD0','#9254C8','#E15FED','#6EDCD9','#4D77FF','#5463FF','#533E85','#FFD32D','#008E89','#03045E','#1C658C','#E15FED','#A97155'],
            menuContainer: "#total_menu_board",
            initIdx: 0,
            init: function () {
                Page.updt();

                const mainContainers = document.querySelectorAll(".draggable-zone-main");
                if (mainContainers.length === 0) return false;
                Page.mainSwappable = new Draggable.Sortable(mainContainers, {
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
                Page.subSwappable = new Draggable.Sortable(subContainers, {
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
             * 트리 구조 데이터 리로드 및 새로 그리기
             */
            updt: function() {
                // 메인 메뉴 그리기
                const url = "${SiteUrl.MENU_MAIN_LIST_AJAX!}";
                commons.util.blockUIAjax(url, 'get', null, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    let mainMenuList = res.rsltList;
                    if (!Array.isArray(mainMenuList) || mainMenuList.length < 1) return;
                    mainMenuList.forEach(function(mainMenu) {
                        commons.util.handlebarsAppend(mainMenu, "menu_main_card");
                        Page.drawSubMenu(mainMenu);
                    });
                });
            },
            /**
             * 서브메뉴 처리 :: 자기 자신을 그리고 각각의 서브메뉴에 대해서 재귀호출
             */
            drawSubMenu: function(upperMenu) {
                // 서브메뉴 돌면서 재귀호출
                let subMenuList = upperMenu.subMenuList;
                if (!Array.isArray(subMenuList) || subMenuList.length <1) return;
                let menuNo = upperMenu.menuNo;
                subMenuList.forEach(function(subMenu) {
                    commons.util.handlebarsAppendTo(subMenu, "menu_sub", "menu_sub_"+menuNo);
                    Page.drawSubMenu(subMenu);
                });
            },
            drawTree: function() {
                let jstreeContainer = Page.jstreeContainer;
                commons.jstree.reset(jstreeContainer);
                commons.jstree.init(jstreeContainer);     // 조직도 트리구조 세팅
                // 부서 조회
                // let deptUrl = "/manage/user/totalOrgnztChartAjax";
                // commons.util.blockUIAjax(deptUrl, "get", {}, function(res) {
                //     if (!res.result) return;
                //     $(jstreeContainer).jstree(true).settings.core.data = res.rsltList;
                //     $(jstreeContainer).jstree(true).settings.core.expand_selected_onload = true;
                //     $(jstreeContainer).jstree(true).refresh(false, true);
                // });
            },
            /** 목록 검색 */
            search: function() {
                $("#listForm #pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${SiteUrl.LOG_ACTVTY_LIST!}?actionTyCd=SEARCH");
            },
            /** 엑셀 다운로드 */
            xlsxDownload: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.download"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    commons.util.blockUIFileDownload();
                    $("#listForm").attr("action", "${SiteUrl.LOG_ACTVTY_LIST_XLSX_DOWNLOAD!}").submit();
                });
            },
            /** 정렬순서 저장 */
            sortOrdr: function() {
                let orderData = [];
                document.querySelectorAll('.sortable-item').forEach((item, index) => {
                    orderData.push({ "menuNo": Number($(item).attr("id")), "upperMenuNo": Number($(item).attr("upper-menu-no")), "state": { "sortOrdr": index }});
                });
                const url = "${SiteUrl.MENU_SORT_ORDR_AJAX!}";
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
            }
        }
    }();
    const Menu = function() {
        return {
            init: function() {
                /** jquery :: validation */
                commons.validate.validateForm("#menuRegForm", Menu.regAjax);
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
            /** 수정 모달 팝업 호출 */
            mdfForm: function(menuNo) {
                const url = "${SiteUrl.MENU_DTL_AJAX!}";
                let ajaxData = { "menuNo": menuNo };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltObj, "menu_reg", "show");
                });
            },
            /** 상세 모달 팝업 호출 */
            dtlAjax: function(logActvtyNo) {
                event.stopPropagation();
                const url = "${SiteUrl.LOG_ACTVTY_DTL_AJAX!}";
                let ajaxData = { "logActvtyNo": logActvtyNo };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltObj, "log_actvty_dtl", "show");
                });
            },
            /** 메뉴 정보 등록/수정 */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.save"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    let isReg = $("#menuRegForm #menuNo").val() === "";
                    const url = isReg ? "${SiteUrl.MENU_REG_AJAX!}" : "${SiteUrl.MENU_MDF_AJAX!}";
                    let ajaxData = $("#menuRegForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 삭제 처리 (ajax) */
            delAjax: function(menuNo) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.MENU_DEL_AJAX!}";
                    let ajaxData = { "menuNo": menuNo };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
        }
    }();
    const MenuDrag = function() {
        return {
            /**
             * 드래그할 오브젝트에 설정 :: 유저가 드래그 시작시 발생한다. (단발)
             */
            dragStart: function(draggingObj, e) {
                $(".dragging").removeClass("dragging");     // dragging 클래스 리셋
                $(draggingObj).addClass("dragging").css("cursor", "default");
            },
            /**
             * 드래그 타겟에 설정 :: 자기 위에서 드래그가 일어나고 있을 때 계속 발생한다. (매 수백 밀리초마다 발생)
             */
            dragOver: function(targetObj, e) {
                e.preventDefault();
                e.stopPropagation();
                const $draggingObj = $(".dragging");
                const draggingId = $draggingObj.closest("li").attr("id");
                const draggingType = $draggingObj.closest("li").attr("data-type");

                const targetId = $(targetObj).closest("li").attr("id");
                const targetType = $(targetObj).closest("li").attr("data-type");

                let targetDiv = targetType === '${Constant.MENU_TY_MAIN!}' ? $(targetObj).closest(".drag-item") : $(targetObj).find(".drag-item");
                // 1. 자기자신은 타겟에서 제외시킨다. 2. 같은 요소(대-대, 중-중, 소-소)끼리만 순서바꾸기가 가능하다.
                // let targetDiv =  $(targetObj).find(".drag-item");
                // 1. 자기자신은 타겟에서 제외시킨다. 2. 같은 요소(대-대, 중-중, 소-소)끼리만 순서바꾸기가 가능하다.
                const isSameObj = draggingId === targetId;
                const isNotSameLevel = draggingType !== targetType;
                if (isSameObj || isNotSameLevel) {
                    // targetDiv.removeClass("drag-area").css("cursor", "not-allowed");
                    return false;
                }
                targetDiv.addClass("drag-area").css("cursor", "default");
            },
            /**
             * 드래그 타겟에 설정 :: 자신한테서 드래그 벗어날시 발생한다.
             */
            dragLeave: function(thisObj) {
                $(thisObj).removeClass("drag-area").css("cursor", "default");
            },
            /**
             * 드래그 타겟에 설정 :: 자신한테 드랍이 일어났을 시 발생한다.
             */
            drop: function(thisObj, e) {
                e.preventDefault();
                e.stopPropagation();
                if (!thisObj.hasClass("drag-area")) return false;
                $(thisObj).removeClass("drag-area").css("cursor", "default");

                // 순서 바꾸기
                let droppingLi = $(".dragging").closest("li");
                let droppingIdx = droppingLi.attr("data-index");
                let targetLi = $(thisObj).closest("li");
                let targetIdx = targetLi.attr("data-index");

                let sortOrdrAjaxData = [ { "menuId" : droppingLi.attr("id"), "sortOrdr" : targetIdx } ];
                let oldSortOrdr, newSortOrdr;
                droppingLi.siblings("li").each(function (idx, elmt) {
                    oldSortOrdr = Number($(elmt).attr("data-index"));
                    if (oldSortOrdr < Math.min(targetIdx, droppingIdx) || oldSortOrdr > Math.max(targetIdx, droppingIdx)) return true;
                    newSortOrdr = (droppingIdx > targetIdx) ? oldSortOrdr + 1 : oldSortOrdr - 1;
                    sortOrdrAjaxData.push({ "menuId" : $(elmt).attr("id"), "sortOrdr" : newSortOrdr });
                });
                MenuDrag.sortOrdr(sortOrdrAjaxData);       // 드래그앤드롭 메뉴 순서 저장 :: 메소드 분리
            },
        }
    }();
    $(document).ready(function() {
        Page.init();
        Menu.init();
    });
</script>

<form name="procForm" id="procForm" method="get">
</form>

<div class="row mt-2 mb-6 px-10 draggable-zone-main" id="menu_main_card_div">
    <!-- handlebars template goes in here -->
</div>
<!--end::Card-->

<#include "/view/admin/menu/_menu_main_card_template.hbs">
<#include "/view/admin/menu/_menu_sub_template.hbs">
<#include "/view/admin/menu/_menu_reg_modal.ftlh">

</@layout.layout>