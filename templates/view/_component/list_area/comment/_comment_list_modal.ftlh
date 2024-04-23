<#--
 * 댓글 목록 Modal
 * @included_in : 댓글 미리보기 기능이 있는 페이지들
 -->

<script>
    const CommentList = (function() {
        return {
            /** 댓글 목록 모달 호출 */
            modal: function(postNo) {
                event.stopPropagation();
                $("#commentProcForm #refPostNo").val(postNo);
                const url = "${Url.COMMENT_LIST_AJAX!}";
                let ajaxData = $("#commentProcForm").serializeArray();
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    let rsltList = res.rsltList;
                    if (commons.util.isEmpty(res.rsltList)) rsltList = [];
                    commons.util.handlebarsTemplate(rsltList, "comment_list", "show");
                });
            },
        }
    })();
</script>

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="comment_list" size="xl">
    <#--begin::Modal Header-->
    <@component.modal_header "댓글 목록 조회" />
    <#--begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <div class="row" id="comment_list_div">
            <#--handlebars template goes in here-->
        </div>
    </div>
    <#--begin::Modal Footer-->
    <div class="modal-footer">
        <#--버튼 영역-->
        <div class="d-flex justify-content-end">
            <#--모달:: 버튼: 모달 닫기-->
            <@component.modal_btn_close />
        </div>
    </div>
</@modal.layout>

<form name="procForm" id="commentProcForm">
    <input type="hidden" name="refPostNo" id="refPostNo" value="" />
    <input type="hidden" name="refContentType" value="${contentType!}" />
    <input type="hidden" name="actvtyCtgrCd" value="${actvtyCtgrCd!}" />
</form>

<#--include:: Handlebars template-->
<#include "/view/_component/list_area/comment/_comment_list_template.hbs">