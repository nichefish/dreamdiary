<#--
 * 목록 > 첨부파일 조회 Modal
 -->

<script>
    const AtchFileList = (function() {
        return {
            /**
             * 첨부파일 추가추가
             * (reqstItemIdx는 어차피 한 페이지 내에서 고유하므로 따로 처리해줄 필요 없다.)
             */
            modal: function(atchFileNo) {
                event.stopPropagation();
                const url = "${Url.FILE_INFO_LIST_AJAX!}";
                const ajaxData = { "atchFileNo": atchFileNo };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    let rsltList = res.rsltList;
                    if (commons.util.isEmpty(res.rsltList)) rsltList = [];
                    commons.util.handlebarsTemplate(rsltList, "atch_file_list", "show");
                });
            },
        }
    })();
</script>

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="atch_file_list" size="lg">
    <#--begin::Modal Header-->
    <@component.modal_header "첨부파일 목록 조회" />
    <#--begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <div class="row" id="atch_file_list_div">
            <#--Handlebars template goes in here-->
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

<#--include:: Handlebars template-->
<#include "/view/_component/atch_file/_atch_file_list_template.hbs">