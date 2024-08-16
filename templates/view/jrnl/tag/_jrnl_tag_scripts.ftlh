<#--
 * 저널 태그(jrnl_tag) : 스크립트 모듈 분리
 -->

<script>
    const JrnlTag = (function() {
        return {
            /** 목록에 따른 일자 태그 조회 */
            dayTagListAjax: function() {
                const url = "${Url.JRNL_DAY_TAG_LIST_AJAX!}";
                let ajaxData = { "yy": $("#jrnl_aside #yy").val(), "mnth": $("#jrnl_aside #mnth").val() };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltList, "jrnl_day_tag_list");
                });
            },
            /** 목록에 따른 일기 태그 조회 */
            diaryTagListAjax: function() {
                const url = "${Url.JRNL_DIARY_TAG_LIST_AJAX!}";
                let ajaxData = { "yy": $("#jrnl_aside #yy").val(), "mnth": $("#jrnl_aside #mnth").val() };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltList, "jrnl_diary_tag_list");
                });
            },
            /** 목록에 따른 꿈 태그 조회 */
            dreamTagListAjax: function() {
                const url = "${Url.JRNL_DREAM_TAG_LIST_AJAX!}";
                let ajaxData = { "yy": $("#jrnl_aside #yy").val(), "mnth": $("#jrnl_aside #mnth").val() };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltList, "jrnl_dream_tag_list");
                });
            },
            /** 목록에 따른 꿈 태그 (전체) 조회 */
            dreamTagListAllAjax: function() {
                const url = "${Url.JRNL_DREAM_TAG_LIST_AJAX!}";
                let ajaxData = { "yy": 9999, "mnth":99 };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    const ctgrSet = new Set();
                    res.rsltList.forEach(item => {
                        if (item.ctgr) ctgrSet.add(item.ctgr);
                    });
                    commons.util.handlebarsTemplate(ctgrSet, "jrnl_tag_ctgr");
                    commons.util.handlebarsTemplate(res.rsltList, "jrnl_tag_list", "show");
                    $("#jrnl_tag_dtl_modal").modal("hide");
                });
            },
            /** 목록에 따른 꿈 태그 (전체) 조회 */
            dreamTagGroupListAllAjax: function() {
                const url = "${Url.JRNL_DREAM_TAG_LIST_AJAX!}";
                let ajaxData = { "yy": 9999, "mnth":99 };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    const groupedList = JrnlTag.groupTagsByCategory(res.rsltList);
                    for (const ctgr in groupedList) {
                        if (!groupedList.hasOwnProperty(ctgr)) continue;
                        const eachList = groupedList[ctgr];
                        commons.util.handlebarsAppend({ "ctgr": ctgr, "tagList": eachList }, "jrnl_tag_list");
                    }
                    $("#jrnl_tag_list_modal").modal("show");
                    $("#jrnl_tag_dtl_modal").modal("hide");
                });
            },
            groupTagsByCategory: function(tagList) {
                // 카테고리별로 그룹화
                const groupedTags = tagList.reduce((acc, tag) => {
                    const category = tag.ctgr || '';
                    if (!acc[category]) {
                        acc[category] = [];
                    }
                    acc[category].push(tag);
                    return acc;
                }, {});

                // 그룹을 순서대로 정렬 (Unknown을 마지막으로)
                const sortedGroupedTags = Object.keys(groupedTags)
                    .sort((a, b) => {
                        if (a === 'Unknown') return 1;
                        if (b === 'Unknown') return -1;
                        return a.localeCompare(b);
                    })
                    .reduce((acc, key) => {
                        acc[key] = groupedTags[key];
                        return acc;
                    }, {});

                return sortedGroupedTags;
            },
            dreamTagDtlAjax: function(tagNo, tagNm) {
                const url = "${Url.JRNL_DREAM_TAG_DTL_AJAX!}";
                let ajaxData = { "tagNo": tagNo };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltList, "jrnl_tag_dtl", "show");
                    $("#header_tag_nm").text(tagNm);
                    $("#jrnl_tag_list_modal").modal("hide");
                });
            },
            dreamTagFilterAll: function(obj) {
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
            dreamTagFilter: function(tagCtgr) {
                if (commons.util.isEmpty(tagCtgr)) return;
                $("span."+tagCtgr).toggle();
                $("i."+tagCtgr).toggle();
                $("div."+tagCtgr).toggleClass("active").toggleClass("btn-outlined").toggleClass("btn-light");
            },
            hideSingleTag(selectorDiv) {
                $(selectorDiv + " span.ts-1").parent().toggle();
            },
        }
    })();
</script>