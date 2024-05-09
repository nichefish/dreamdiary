<#--
 * 저널 태그(jrnl_tag) : 스크립트 모듈 분리
 -->

<script>
    const JrnlTag = (function() {
        return {
            /** 목록에 따른 꿈 태그 조회 */
            dreamTagListAjax: function() {
                const url = "${Url.JRNL_DREAM_TAG_LIST_AJAX!}";
                let ajaxData = { "yy": $("#jrnl_aside #yy").val(), "mnth": $("#jrnl_aside #mnth").val(), "contentType": "JRNL_DREAM" };
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