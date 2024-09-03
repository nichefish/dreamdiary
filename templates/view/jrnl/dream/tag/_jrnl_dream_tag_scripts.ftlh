<#--
 * 저널 꿈(jrnl_dream) : 태그 스크립트 모듈 분리
 -->

<script>
    const JrnlDreamTag = (function() {
        return {
            /** 목록에 따른 꿈 태그 조회 */
            listAjax: function() {
                const url = "${Url.JRNL_DREAM_TAG_LIST_AJAX!}";
                const ajaxData = { "yy": $("#jrnl_aside #yy").val(), "mnth": $("#jrnl_aside #mnth").val() };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltList, "jrnl_dream_tag_list");
                });
            },
            /** 목록에 따른 꿈 태그 (전체) 조회 */
            listAllAjax: function() {
                const url = "${Url.JRNL_DREAM_TAG_LIST_AJAX!}";
                const ajaxData = { "yy": 9999, "mnth":99 };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    // 상단에 태그 카테고리 메뉴 생성
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
                const ajaxData = { "yy": 9999, "mnth":99 };
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
            dtlAjax: function(tagNo, tagNm) {
                const url = "${Url.JRNL_DREAM_TAG_DTL_AJAX!}";
                let ajaxData = { "tagNo": tagNo };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltList, "jrnl_dream_tag_dtl", "show");
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
            filter: function(tagCtgr) {
                if (commons.util.isEmpty(tagCtgr)) return;
                $("span."+tagCtgr).toggle();
                $("i."+tagCtgr).toggle();
                $("div."+tagCtgr).toggleClass("active").toggleClass("btn-outlined").toggleClass("btn-light");
            },
            hideSingleTag(selectorDiv) {
                $(selectorDiv + " span.ts-1").parent().toggle();
            },
            /**
             * 태그 카테고리 파일을 DB로부터 새로고침한다.
             */
            tagCtgrSyncAjax: function() {
                const url = "${Url.JRNL_TAG_CTGR_SYNC_AJAX!}";
                commons.util.blockUIAjax(url, 'post', {}, function(res) {
                    Swal.fire({ text: res.message });
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                });
            }
        }
    })();
</script>