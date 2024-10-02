<#--
 * 저널 태그(jrnl_tag) : 스크립트 모듈 분리
 -->

<script>
    const JrnlTag = (function() {
        return {
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
                commons.util.blockUIAjax(url, 'POST', {}, function(res) {
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