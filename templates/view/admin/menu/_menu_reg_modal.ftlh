<#--
  -- 분류 코드(CL_CD) > 상세 Modal
  -- @included_in : menu_page.ftlh
  -->
<div class="modal fade" id="menu_reg_modal" tabindex="-1" role="dialog" aria-labelledby="menuRegModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
        <div class="modal-content">
            <!-- 영역:: 모달 헤더 -->
            <@component.modal_header "메뉴 저장" />
            <!-- 영역:: 모달 내용 -->
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <form name="menuRegForm" id="menuRegForm" class="form-horizontal">
                        <div id="menu_reg_div">
                            <!-- contents -->
                        </div>
                    </form>
                </div>
            </div>
            <!--end::Modal body-->
            <!--begin::Modal footer-->
            <div class="modal-footer flex-end">
                <button type="button" class="btn btn-primary" onclick="commons.util.submit('#menuRegForm');"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.save"/>">
                    <i class="bi bi-pencil-square"></i>
                    <span class="indicator-label">저장</span>
                    <span class="indicator-progress">
                        Please wait...
                        <span class="spinner-border spinner-border-sm align-middle ms-2"></span>
                    </span>
                </button>
                <button type="reset" class="btn btn-light me-3" data-bs-dismiss="modal" aria-label="Close">
                    닫기
                </button>
            </div>
            <!--end::Modal footer-->
        </div>
    </div>
</div>
<!-- include:: Handlebars template -->
<#include "/view/admin/menu/_menu_reg_template.hbs">