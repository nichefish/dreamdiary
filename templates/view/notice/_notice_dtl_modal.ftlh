<#--
  -- 공지사항(notice) > 상세 Modal
  -- @included_in : notice_list.ftlh
  -->

<#import "/layout/modal_layout.ftlh" as modal >

<@modal.layout id="notice_dtl" size="xl">
    <!--Begin::Modal Header-->
    <@component.modal_header "공지사항 조회" />
    <!--Begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <div id="notice_dtl_div">
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
<#include "/view/notice/_notice_dtl_template.hbs">