<#--
 * 캐시 : 스크립트 모듈 분리
 -->

<script>
    const Tag = (function() {
        return {
            /**
             * Page 객체 초기화
             */
            init: function() {
                // 태그 조회
                Page.tagListAjax('${refContentType!}');
            },

            /**
             * 전체 태그 목록 조회 (ajax)
             * @param {string} refContentType - 조회할 태그의 참조 콘텐츠 유형.
             */
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