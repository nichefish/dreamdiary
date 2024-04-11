<#--
 -- 분류 코드(CL_CD) > 상세 Modal
 -- @included_in : cl_cd_list.ftlh
 -->
<div class="modal fade" id="cl_cd_dtl_modal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <!-- 영역:: 모달 헤더 -->
            <@component.modal_header "분류코드 정보" />
            <!-- 영역:: 모달 내용 -->
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <div id="cl_cd_dtl_div">
                        <!-- contents -->
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- include:: Handlebars template -->
<#include "/view/admin/cd/_cl_cd_dtl_template.hbs">