<#--
  -- 게시판 > 태그 전체 목록 Modal
  -- @included_in : board_post_list
  -- @included_in : board_post_dtl
  -->
<div class="modal fade" id="board_tag_list_modal" tabindex="-1" role="dialog" aria-labelledby="tagModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <!-- 영역:: 모달 헤더 -->
            <@component.modal_header "태그" />
            <!-- 영역:: 모달 내용 -->
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <div class="row" id="board_tag_list_div">
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
<#include "/view/_component/tag/_tag_list_template.hbs">