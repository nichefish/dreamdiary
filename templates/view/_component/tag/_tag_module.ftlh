<#--
 * 태그 관련파일 묶음
 * @included_in : board_post_list
 -->
<script>
    const Tag = (function() {
        return {
            /**
             * Tag 객체 초기화
             */
            init: function() {
                //
            },

            /**
             * 태그 상세 정보 조회 (ajax)
             * @param {string|number} tagNo - 조회할 태그 번호.
             */
            dtlAjax: function(tagNo) {
                event.stopPropagation();
                if (isNaN(tagNo)) return;

                const url = "${Url.TAG_DTL_AJAX!}";
                const ajaxData = { "tagNo": tagNo };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    // 태그 상세 정보
                    commons.util.handlebarsTemplate(res.rsltObj, "tag_dtl", "show");
                    // 태그 글 목록
                    commons.util.handlebarsTemplate(res.rsltList, "tag_dtl_post_list");
                    $("#board_tag_list_modal").modal("hide");
                });
            },

            /**
             * 태그로 글 목록 필터링
             * @param {string|number} tagNo - 필터링할 태그 번호.
             */
            filter: function(tagNo) {
                if (isNaN(tagNo)) return;

                document.querySelector("#listForm #pageNo").value = 1;
                document.querySelector("#listForm #tags").value = tagNo;
                const url = "${listUrl!}?actionTyCd=SEARCH";
                commons.util.blockUISubmit("#listForm", url);
            },

            /**
             * 글 목록 태그 필터링 리셋
             */
            resetFilter: function() {
                Tag.filter("");
            },

            /**
             * 전체 태그 목록 조회
             */
            listAjax: function() {
                const url = "${Url.TAG_LIST_AJAX!}";
                const ajaxData = { "contentType": $("#contentType").val() };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    // 태그 상세 정보
                    commons.util.handlebarsTemplate(res.rsltList, "board_tag_list", "show");
                    $("#board_tag_dtl_modal").modal("hide");
                });
            },
        }
    })();
    document.addEventListener("DOMContentLoaded", function() {
        Tag.init();
    });
</script>

<#include "/view/_component/tag/_tag_dtl_modal.ftlh">
<#include "/view/_component/tag/_tag_dtl_post_list_template.hbs">
<#include "/view/_component/tag/_tag_list_modal.ftlh">