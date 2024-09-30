<#--
 * 태그 관련파일 묶음
 * @included_in : board_post_list
 -->
<script>
    const Tag = (function() {
        return {
            init: function () {
                //
            },
            dtlAjax: function(tagNo) {
                event.stopPropagation();
                const url = "${Url.TAG_DTL_AJAX!}";
                const ajaxData = { "tagNo": tagNo };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
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
            /** 태그로 글 목록 검색 */
            search: function(tagNo) {
                $("#listForm #pageNo").val(1);
                $("#listForm #tags").val(tagNo);
                let url = "${listUrl!}?actionTyCd=SEARCH";
                commons.util.blockUISubmit("#listForm", url);
            },
            /** 전체 태그 목록 조회 */
            listAjax: function() {
                const url = "${Url.TAG_LIST_AJAX!}";
                const ajaxData = { "contentType": $("#contentType").val() };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
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
    $(document).ready(function() {
        Tag.init();
    });
</script>

<#include "/view/_component/tag/_tag_dtl_modal.ftlh">
<#include "/view/_component/tag/_tag_dtl_post_list_template.hbs">
<#include "/view/_component/tag/_tag_list_modal.ftlh">