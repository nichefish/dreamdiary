<#--
  -- 활동 로그 > 상세 Modal
  -- @included_in : log_actvty_list
  -->
<div class="modal fade" id="flsys_meta_dtl_modal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <!-- 영역:: 모달 헤더 -->
            <@component.modal_header "파일시스템 메타 정보 조회" />
            <!-- 영역:: 모달 내용 -->
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <div id="flsys_meta_dtl_div">
                        <!-- contents -->
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <div id="flsys_meta_dtl_btn_div">
                    <!-- handlebars template goes in here -->
                </div>
            </div>
        </div>
    </div>
</div>
<!-- include:: Handlebars template -->
<#include "/view/cmm/flsys/_flsys_meta_dtl_template.hbs">
<#include "/view/cmm/flsys/_flsys_meta_dtl_btn_template.hbs">