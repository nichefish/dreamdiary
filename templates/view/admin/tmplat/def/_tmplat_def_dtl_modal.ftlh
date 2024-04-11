<#--
 -- 템플릿 관리 > 상세 Modal
 -- @included_in : notice_list
 -->
<div class="modal fade" id="templat_def_dtl_modal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <!-- 영역:: 모달 헤더 -->
            <@component.modal_header "공지사항" />
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <div id="templat_def_dtl_div">
                        <!-- contents -->
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- include:: Handlebars template -->
<#include "/view/admin/tmplat/def/_tmplat_def_dtl_template.hbs">