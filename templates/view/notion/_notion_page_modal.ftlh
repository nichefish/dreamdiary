<#--
  -- 활동 로그 > 상세 Modal
  -- @included_in : log_actvty_list
  -->
<#--<script defer="defer" src="/static/react/static/js/main.668f159c.js"></script>
<link rel="stylesheet" type="text/css" href="/static/react/static/css/main.4fb4fba2.css">-->

<div class="modal fade" id="notion_page_modal" tabindex="-1" role="dialog" aria-labelledby="notionPageModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="notionPageModalLongTitle">[NOTION 페이지]</h5>
                <div class="btn btn-sm btn-icon btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <div id="react_notion_modal_root">
                        <!-- contents -->
                    </div>
                    <div id="notion_page_div">
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
