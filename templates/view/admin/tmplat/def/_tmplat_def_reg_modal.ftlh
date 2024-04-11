<#--
  -- 템플릿 관리 > 등록 Modal
  -- @included_in : cl_cd_list
  -->
<div class="modal fade" id="tmplat_def_reg_modal" tabindex="-1" role="dialog" aria-labelledby="tmplatDefRegModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="tmplatDefRegModalLongTitle">[템플릿 정의 저장]</h5>
                <div class="btn btn-icon btn-sm btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <form name="tmplatDefRegForm" id="tmplatDefRegForm">
                        <div id="tmplat_def_reg_div">
                            <!-- contents -->
                        </div>
                    </form>
                </div>
            </div>
            <div class="modal-footer">
                <!-- 버튼 영역 -->
                <div class="d-flex justify-content-end">
                    <button type="button" class="btn btn-sm btn-primary me-2"
                            onclick="Page.regAjax();"
                            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.save"/>">
                        <i class="bi bi-pencil-square"></i>저장
                    </button>
                    <!-- 버튼:: 모달 닫기 -->
                    <@component.btn_modal_close />
                </div>
            </div>
        </div>
    </div>
</div>
<!-- include:: Handlebars template -->
<#include "/view/admin/tmplat/def/_tmplat_def_reg_template.hbs">