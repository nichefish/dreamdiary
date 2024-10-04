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
            /**
             * Page 객체 초기화
             */
            init: function() {
                // 태그 조회
                Page.tagListAjax('${refContentType!}');
            },

            /** 전체 태그 목록 조회 */
            tagListAjax: function(refContentType) {
                const url = "${Url.TAG_LIST_AJAX!}";
                const ajaxData = { "contentType": refContentType };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltList, "tag_list");
                });
            },

            filterAll: function(obj) {
                const isActivate = !$(obj).hasClass("active");
                if (isActivate) {
                    $("span.ctgr").show();
                    $("i.ctgr").show();
                    $("div.ctgr").addClass("active").addClass("btn-outlined").removeClass("btn-light");
                } else {
                    $("span.ctgr").hide();
                    $("i.ctgr").hide();
                    $("div.ctgr").removeClass("active").removeClass("btn-outlined").addClass("btn-light");
                }
            },

            /**
             * 태그 카테고리로 글 목록 필터링
             * @param {string|number} tagCtgr - 필터링할 태그 카테고리.
             */
            filter: function(tagCtgr) {
                if (commons.util.isEmpty(tagCtgr)) return;
                $("span."+tagCtgr).toggle();
                $("i."+tagCtgr).toggle();
                $("div."+tagCtgr).toggleClass("active").toggleClass("btn-outlined").toggleClass("btn-light");
            },
        }
    })();
    document.addEventListener("DOMContentLoaded", function() {
        Page.init();
    });
</script>


<#--include:: Aside-->
<#include "/view/admin/tag/_tag_aside_base.ftlh">

<!-- 컨텐츠 타입 -->
<div class="card post">
    <div id="jrnl_tag_ctgr_div">
        <div class="btn btn-sm btn-outlined btn-light-secondary text-muted ctgr active"
             onclick="Page.reloadWithParam('refContentType', '');">
            전체
            <i class="bi bi-check ctgr"></i>
        </div>

        <#list contentTypeList![] as contentType>
            <div class="btn btn-sm btn-outlined btn-light-secondary text-muted"
                 onclick="commons.util.reloadWithParam('refContentType', '${contentType.key!}');">
                ${contentType.desc!}
                <#if refContentType! == contentType.key!>
                    <i class="bi bi-check"></i>
                </#if>
            </div>
        </#list>
    </div>
</div>

<!-- 태그 카테고리 -->
<#include "/view/admin/tag/_tag_ctgr_header.ftlh">

<!-- 태그 목록 -->
<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <div id="tag_list_div">
            <!--Handlebars Template goes in here-->
        </div>
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