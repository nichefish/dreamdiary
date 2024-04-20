<#--
 -- 분류 코드(cl_cd) > 등록 Modal
 -- @included_in : cl_cd_list
 -->

<div class="modal fade" id="cl_cd_reg_modal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
        <div class="modal-content">
            <!-- 영역:: 모달 헤더 -->
            <@component.modal_header "분류코드 저장" />
            <!-- 영역:: 모달 내용 -->
            <div class="modal-body modal-mbl-body my-5">
                <div class="table-responsive-sm">
                    <form name="clCdRegForm" id="clCdRegForm">
                        <div id="cl_cd_reg_div">
                            <!-- contents -->
                        </div>
                    </form>
                </div>
            </div>
            <div class="modal-footer">
                <!-- 버튼 영역 -->
                <div class="d-flex justify-content-end">
                    <button type="button" class="btn btn-sm btn-primary me-2"
                            onclick="ClCd.regAjax();"
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
<#include "/view/admin/cd/_cl_cd_reg_template.hbs">