<#--
  -- 활동 로그 목록 화면
  -->
<#import "/layout/layout.ftlh" as layout >

<#-- 툴바 버튼 추가 -->
<#assign toolbar>
    <#include "/view/admin/menu/_menu_page_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<link rel="stylesheet" href="/lib/jstree/css/style.min.css" />
<script type="text/javascript" src="/lib/jstree/js/jstree.min.js"></script>
<script type="text/javascript" src="/js/commons/commons-jstree.js?20230918"></script>

<style>
    ul {
        padding-left: 1.5em !important;
    }
</style>
<script>
    const Page = function() {
        return {
            colorArray: ['#185ADB','#FFC107','#00ADB5','#7952B3','#FFD93D','#4D96FF','#8479E1','#332FD0','#9254C8','#E15FED','#6EDCD9','#4D77FF','#5463FF','#533E85','#FFD32D','#008E89','#03045E','#1C658C','#E15FED','#A97155'],
            menuContainer: "#total_menu_board",
            init: function () {
                Page.updt();
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
                    let mainMenuList = res.resultList;
                    console.log(mainMenuList);
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
                console.log("upperMenu: ", upperMenu);
                // 서브메뉴 돌면서 재귀호출
                let subMenuList = upperMenu.subMenuList;
                console.log("subMenuList: ", subMenuList);
                if (!Array.isArray(subMenuList) || subMenuList.length <1) return;
                let menuId = upperMenu.menuId;
                subMenuList.forEach(function(subMenu) {
                    commons.util.handlebarsAppendTo(subMenu, "menu_sub", "menu_sub_"+menuId);
                    Page.drawSubMenu(subMenu);
                });
            },
            // drawSubMenu: function(treeSelector, rslt, idx) {
            //     let menuId = rslt.menuId, menuNm = rslt.menuNm, menuTyCd = rslt.menuTyCd;
            //     commons.util.handlebarsAppend(rslt, "menu_sub");
            //     Page.drawSubMenu();
            //     let isDir = rslt.dir;
            //     let subMenuContainer = upperElmtSelector + " > ul.sub-menu-list";
            //     $(subMenuContainer).append(menuTemplateHtml);
            //     if (!rslt.dir) $("#addBtn_"+menuId).remove();       // 소메뉴는 추가버튼 없앰
            //     // 그 하위메뉴 그리기
            //     let menuSubList = rslt.menuSubList;
            //     for (let i = 0; i < menuSubList.length; i++) Page.drawMenu("#"+menuId, menuSubList[i], i);
            // },
            drawTree: function() {
                let jstreeContainer = Page.jstreeContainer;
                commons.jstree.reset(jstreeContainer);
                commons.jstree.init(jstreeContainer);     // 조직도 트리구조 세팅
                // 부서 조회
                // let deptUrl = "/manage/user/totalOrgnztChartAjax";
                // commons.util.blockUIAjax(deptUrl, "get", {}, function(res) {
                //     if (!res.result) return;
                //     $(jstreeContainer).jstree(true).settings.core.data = res.resultList;
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
            regModal: function(menuTyCd, upperMenuId) {
                event.stopPropagation();
                let obj = { "menuTyCd": menuTyCd, "upperMenuId": upperMenuId };
                commons.util.handlebarsTemplate(obj, "menu_reg", "show");
            },
            /** 상세 모달 팝업 호출 */
            refreshIcon: function() {
                const $iconClassElmt = $("#menuRegForm #icon");
                if (!$iconClassElmt.length) return;
                $("#menuRegForm #menu_icon_div").html($iconClassElmt.val());
            },
            /** 사용자별 로그 페이지로 이동 */
            mdfForm: function(menuId) {
                const url = "${SiteUrl.MENU_DTL_AJAX!}";
                let ajaxData = { "menuId": menuId };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.resultObj, "menu_reg", "show");
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
                    commons.util.handlebarsTemplate(res.resultObj, "log_actvty_dtl", "show");
                });
            },
            /** 메뉴 정보 등록/수정 */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.save"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    let isReg = $("#menuRegForm #menuId").val() === "";
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
            delAjax: function(menuId) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.MENU_DEL_AJAX!}";
                    let ajaxData = { "menuId": menuId };
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
    $(document).ready(function() {
        Page.init();
        Menu.init();
    });
</script>

<div class="card post">
    <form name="procForm" id="procForm" method="get">
        <input type="hidden" name="logActvtyNo" id="logActvtyNo" />
    </form>

    <div class="row mt-2 mb-6" id="menu_main_card_div">
        <!-- handlebars template goes in here -->
    </div>
</div>
<!--end::Card-->

<#include "/view/admin/menu/_menu_main_card_template.ftlh">
<#include "/view/admin/menu/_menu_sub_template.ftlh">
<#include "/view/admin/menu/_menu_reg_modal.ftlh">
<#include "/view/admin/menu/_menu_reg_template.ftlh">

</@layout.layout>