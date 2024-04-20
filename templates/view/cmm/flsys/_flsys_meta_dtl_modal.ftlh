<#--
  -- 파일시스템 메타(flsys_meta) > 상세 Modal
  -- @included_in : log_actvty_list
  -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="flsys_meta_dtl" size="xl">
    <!--Begin::Modal Header-->
    <@component.modal_header "파일시스템 메타 정보 조회" />
    <!--Begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <div class="table-responsive-sm">
            <div id="flsys_meta_dtl_div">
                <!-- contents -->
            </div>
        </div>
    </div>
    <!--begin::Modal Footer-->
    <div class="modal-footer">
        <div id="flsys_meta_dtl_btn_div">
            <!-- handlebars template goes in here -->
        </div>
    </div>
</@modal.layout>

<!-- include:: Handlebars template -->
<#include "/view/cmm/flsys/_flsys_meta_dtl_template.hbs">
<#include "/view/cmm/flsys/_flsys_meta_dtl_btn_template.hbs">