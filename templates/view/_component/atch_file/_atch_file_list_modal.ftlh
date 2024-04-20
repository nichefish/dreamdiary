<#--
 -- 첨부파일 목록 조회 Modal
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
                let ajaxData = { "atchFileNo": atchFileNo };
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
    $(document).ready(function () {
        AtchFileList.init();
    });
</script>

<div class="modal fade" id="atch_file_list_modal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
        <div class="modal-content">
            <!-- 영역:: 모달 헤더 -->
            <@component.modal_header "첨부파일 목록 조회" />
            <!-- 영역:: 모달 내용 -->
            <div class="modal-body modal-mbl-body my-5">
                <div class="table-responsive-sm">
                    <div class="row" id="atch_file_list_div">
                        <!-- contents -->
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <!-- 버튼 영역 -->
                <div class="d-flex justify-content-end">
                    <!-- 버튼:: 모달 닫기 -->
                    <@component.btn_modal_close />
                </div>
            </div>
        </div>
    </div>
</div>

<!-- include:: Handlebars template -->
<#include "/view/_component/atch_file/_atch_file_list_template.hbs">