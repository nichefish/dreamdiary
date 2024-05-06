<#--
 * 저널 태그(jrnl_tag) : 스크립트 모듈 분리
 -->

<script>
    const JrnlTag = (function() {
        return {
            allTagListAjax: function() {
                const url = "${Url.TAG_LIST_AJAX!}";
                let ajaxData = { "contentType": "JRNL_DREAM" };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    console.log(res.rsltList);
                    commons.util.handlebarsTemplate(res.rsltList, "jrnl_tag_list", "show");
                    $("#jrnl_tag_dtl_modal").modal("hide");
                });
            },
            tagDtlAjax: function(tagNo, tagNm) {
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
        }
    })();
</script>