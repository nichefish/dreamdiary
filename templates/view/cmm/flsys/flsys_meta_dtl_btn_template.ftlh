<#--
  -- 분류 코드(CL_CD) > 상세 Template
  -- @included_in : cl_cd_list
  -->
<script type="text/x-handlebars-template" id="flsys_meta_dtl_btn_template">
    <button type="button" class="btn btn-light btn-active-primary me-2 btn-sm"
            onclick="Meta.mdfForm('{{postNo}}');"
            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.mdf"/>">
        <i class="bi bi-pencil-square"></i>수정
    </button>
    <button type="button" class="btn btn-sm btn-light btn-active-danger me-2"
            onclick="Meta.delAjax('{{postNo}}');"
            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.del"/>">
        <i class="bi bi-trash"></i>삭제
    </button>
    <button type="button" class="btn btn-sm btn-light" data-bs-dismiss="modal">닫기</button>
</script>