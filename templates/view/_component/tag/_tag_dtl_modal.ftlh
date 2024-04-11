<#--
  -- 게시판 > 태그 상세 Modal
  -- @included_in : board_post_list
  -- @included_in : board_post_dtl
  -->


<div class="modal fade" id="tag_dtl_modal" tabindex="-1" role="dialog" aria-labelledby="tagDtlModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <!-- 영역:: 모달 헤더 -->
            <@component.modal_header "태그" />
            <!-- 영역:: 모달 내용 -->
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <div class="row" id="tag_dtl_div">
                        <!-- contents -->
                    </div>
                    <div class="separator separator-dashed border-gray-300 my-2"></div>
                    <div class="row" id="tag_dtl_post_list_div">
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
<#include "/view/_component/tag/_tag_dtl_template.hbs">