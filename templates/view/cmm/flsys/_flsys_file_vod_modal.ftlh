<#--
  -- 활동 로그 > 상세 Modal
  -- @included_in : log_actvty_list
  -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="flsys_file_vod" size="xl">
    <!--Begin::Modal Header-->
    <@component.modal_header "파일시스템 메타 정보 조회" />
    <!--Begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <div id="flsys_file_vod_div">
            <!-- contents -->
        </div>
    </div>
    <!--begin::Modal Footer-->
    <div class="modal-footer">
        <!-- 버튼 영역 -->
        <div class="d-flex justify-content-end">
            <!-- 버튼:: 모달 닫기 -->
            <@component.btn_modal_close />
        </div>
    </div>
</@modal.layout>

<!-- include:: Handlebars template -->
<#include "/view/cmm/flsys/_flsys_file_vod_template.hbs">