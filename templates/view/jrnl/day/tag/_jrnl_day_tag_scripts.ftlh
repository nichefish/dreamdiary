<#--
 * 저널 일자(jrnl_day) : 태그 스크립트 모듈 분리
 -->

<script>
    const JrnlDayTag = (function() {
        return {
            /** 목록에 따른 일자 태그 조회 */
            listAjax: function() {
                const url = "${Url.JRNL_DAY_TAG_LIST_AJAX!}";
                const ajaxData = { "yy": $("#jrnl_aside #yy").val(), "mnth": $("#jrnl_aside #mnth").val() };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltList, "jrnl_day_tag_list");
                });
            },
            /** 목록에 따른 일자 태그 (전체) 조회 */
            listAllAjax: function() {
                const url = "${Url.JRNL_DAY_TAG_LIST_AJAX!}";
                const ajaxData = { "yy": 9999, "mnth":99 };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
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

            /**
             * 상세 모달 호출
             * @param {string|number} tagNo - 조회할 태그 번호.
             */
            dtlModal: function(tagNo, tagNm) {
                event.stopPropagation();
                if (isNaN(tagNo)) return;

                const url = "${Url.JRNL_DAY_TAG_DTL_AJAX!}";
                const ajaxData = { "tagNo": tagNo };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltList, "jrnl_day_tag_dtl", "show");
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