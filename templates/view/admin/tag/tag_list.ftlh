<#--
 * 태그 관리 > 목록 화면
 -->
<#import "/layout/layout_with_aside.ftlh" as layout >

<#--toolbar::버튼 추가-->
<#assign toolbar>
    <#include "/view/admin/tag/_tag_list_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<script type="text/javascript" src="/metronic/assets/plugins/custom/draggable/draggable.bundle.js?${releaseDate!}"></script>

<#--include::script modules-->
<#include "/view/admin/cd/_cl_cd_scripts.ftlh">
<#include "/view/jrnl/tag/_jrnl_tag_scripts.ftlh">

<script>
    const Page = (function() {
        return {
            init: function() {
                /* 모든 table 헤더에 클릭 이벤트를 설정한다. */
                commons.util.initSortTable();
                /* init : Draggable */
                ClCd.initDraggable();
                // 태그 조회
                Page.tagListAjax();
                // Page.listAjax();
                // Page.listAjax();
            },
            /** 전체 태그 목록 조회 */
            tagListAjax: function(tagCtgr) {
                const url = "${Url.TAG_LIST_AJAX!}";
                const ajaxData = { "tagCtgr": tagCtgr };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltList, "tag_list");
                });
            },

/*            /!** 목록에 따른 일기 태그 조회 *!/
            listAjax: function() {
                const url = "${Url.JRNL_DIARY_TAG_LIST_AJAX!}";
                const ajaxData = { "yy": 9999, "mnth": 99 };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltList, "jrnl_diary_tag_list");
                });
            },
            /!** 목록에 따른 일기 태그 조회 *!/
            listAjax: function() {
                const url = "${Url.JRNL_DIARY_TAG_LIST_AJAX!}";
                const ajaxData = { "yy": 9999, "mnth": 99 };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltList, "jrnl_diary_tag_list");
                });
            },
            /!** 목록에 따른 꿈 태그 조회 *!/
            listAjax: function() {
                const url = "${Url.JRNL_DREAM_TAG_LIST_AJAX!}";
                const ajaxData = { "yy": 9999, "mnth": 99 };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltList, "jrnl_dream_tag_list");
                });
            },*/
        }
    })();
    $(document).ready(function() {
        Page.init();
    });
</script>


<#--include:: Aside-->
<#include "/view/admin/tag/_tag_aside_base.ftlh">

<!-- 태그 카테고리 -->
<div class="card post">
    <div id="jrnl_tag_ctgr_div">
        <div class="btn btn-sm btn-outlined btn-light-secondary text-muted ctgr active" onclick="JrnlTag.filterAll(this);">
            전체
            <i class="bi bi-check ctgr"></i>
        </div>

        <div class="btn btn-sm btn-outlined btn-light-secondary text-muted ctgr defaultCtgr active" onclick="JrnlTag.filter('defaultCtgr');">
            카테고리 없음
            <i class="bi bi-check ctgr defaultCtgr"></i>
        </div>
        <#list tagCtgrList![] as ctgr>
            <div class="btn btn-sm btn-outlined btn-light-secondary text-muted ctgr {{.}} active" onclick="JrnlTag.filter('{{.}}');">
                ${ctgr}
                <i class="bi bi-check ctgr ${ctgr}"></i>
            </div>
        </#list>
    </div>
</div>

<!-- 태그 카테고리 -->
<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <!--begin::Card Body-->
        <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
            <#--begin::Table head-->
            <thead>
                <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 fw-bolder text-muted">
                    <th class="col-1 text-center wb-keepall cursor-help w-8 hidden-table">
                        카테고리
                    </th>
                    <th class="col-3 text-center wb-keepall">태그</th>
                    <th class="col-4 text-center wb-keepall">분류코드 설명</th>
                    <th class="col-1 text-center wb-keepall">하위 상세코드 수</th>
                    <th class="col-1 text-center wb-keepall">사용 여부</th>
                    <th class="col-1 text-center wb-keepall">관리</th>
                </tr>
            </thead>
            <#--begin::Table body-->
            <tbody id="tag_list_div">
                <#--begin::목록-->
                <#list clCdList![] as clCd>

                <#else>
                    <tr>
                        <td colspan=8" class="text-center"><@spring.message "view.list.empty"/></td>
                    </tr>
                </#list>
            </tbody>
        </table>
    </div>
    <#--begin::Card footer-->
    <div class="card-footer">
        <#--pagination::begin-->
        <#include "/view/_component/_pagination.ftlh">
        <#--pagination::end-->
    </div>
</div>

<form name="procForm" id="procForm" method="get">
    <input type="hidden" id="clCd" name="clCd" />
</form>

<#--include::modals-->
<#include "/view/admin/tag/_tag_list_template.hbs">

</@layout.layout>