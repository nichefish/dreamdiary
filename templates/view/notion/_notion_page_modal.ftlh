<#--
  -- 활동 로그 > 상세 Modal
  -- @included_in : log_actvty_list
  -->
<#--<script defer="defer" src="/static/react/static/js/main.668f159c.js"></script>
<link rel="stylesheet" type="text/css" href="/static/react/static/css/main.4fb4fba2.css">-->

<div class="modal fade" id="notion_page_modal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <!-- 영역:: 모달 헤더 -->
            <@component.modal_header "NOTION 페이지" />
            <!-- 영역:: 모달 내용 -->
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
