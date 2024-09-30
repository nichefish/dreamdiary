<#--
 * 캐시 : 스크립트 모듈 분리
 -->

<script>
    const Cache = (function() {
        return {
            /** url, data 받아서 ajax 호출 */
            activeListModal: function() {
                const url = "${Url.CACHE_ACTIVE_LIST_AJAX!}";
                commons.util.blockUIAjax(url, 'get', null, function(res) {
                    commons.util.handlebarsTemplate(res.rsltMap, "cache_list", "show");
                });
            },
            /** url, data 받아서 ajax 호출 */
            evictAjax: function(cacheName, key) {
                const url = "${Url.CACHE_EVICT_AJAX!}";
                const ajaxData = { "cacheName": cacheName, "key": key };
                commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                    if (res.rslt) Swal.fire(JSON.stringify(res));
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rsltList) Swal.fire(JSON.stringify(res.rsltList));
                            if (res.rsltObj) Swal.fire(JSON.stringify(res.rsltObj));
                        });
                });
            },
            /** url, data 받아서 ajax 호출 */
            clearAllAjax: function() {
                const url = "${Url.CACHE_CLEAR_AJAX!}";
                commons.util.blockUIAjax(url, 'post', null, function(res) {
                    if (res.rslt) Swal.fire(JSON.stringify(res));
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rsltList) Swal.fire(JSON.stringify(res.rsltList));
                            if (res.rsltObj) Swal.fire(JSON.stringify(res.rsltObj));
                        });
                });
            },
        }
    })();
</script>