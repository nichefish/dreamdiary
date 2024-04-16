<#--
 -- 메뉴(menu) > 목록 화면
 -->

<#import "/layout/layout.ftlh" as layout >

<#-- 툴바 버튼 추가 -->
<#assign toolbar>
    <#include "/view/admin/menu/_menu_page_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<!--include:: script modules -->
<#include "/view/admin/menu/_menu_scripts.ftlh">

<link rel="stylesheet" type="text/css" href="/lib/jstree/css/style.min.css?${releaseDate!}" />
<script type="text/javascript" src="/lib/jstree/js/jstree.min.js?${releaseDate!}"></script>
<script type="text/javascript" src="/js/commons/commons-jstree.js?${releaseDate!}"></script>
<script type="text/javascript" src="/metronic/assets/plugins/custom/draggable/draggable.bundle.js?${releaseDate!}"></script>

<!--include:: script modules -->
<#include "/view/admin/menu/_menu_scripts.ftlh">

<script>
    const Page = function() {
        return {
            colorArray: ['#185ADB','#FFC107','#00ADB5','#7952B3','#FFD93D','#4D96FF','#8479E1','#332FD0','#9254C8','#E15FED','#6EDCD9','#4D77FF','#5463FF','#533E85','#FFD32D','#008E89','#03045E','#1C658C','#E15FED','#A97155'],
            menuContainer: "#total_menu_board",
            initIdx: 0,
            init: function () {
                Menu.init();

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
                const url = "${Url.MENU_MAIN_LIST_AJAX!}";
                commons.util.blockUIAjax(url, 'get', null, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    let mainMenuList = res.rsltList;
                    if (!Array.isArray(mainMenuList) || mainMenuList.length < 1) return;
                    mainMenuList.forEach(function(mainMenu) {
                        commons.util.handlebarsAppend(mainMenu, "menu_main_card");
                        Menu.drawSubMenu(mainMenu);
                    });
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
            }
        }
    }();
    $(document).ready(function() {
        Page.init();
    });
</script>

<div class="row mt-2 mb-6 px-10 draggable-zone-main" id="menu_main_card_div">
    <!-- handlebars template goes in here -->
</div>
<!--end::Card-->

<form name="procForm" id="procForm" method="get">

</form>

<#include "/view/admin/menu/_menu_main_card_template.hbs">
<#include "/view/admin/menu/_menu_sub_template.hbs">
<#include "/view/admin/menu/_menu_reg_modal.ftlh">

</@layout.layout>