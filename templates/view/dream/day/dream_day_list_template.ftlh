<#--
-- 분류 코드(CL_CD) > 상세 코드(DTL_CD) 등록/수정 Template
  -- @included in : cl_cd_list
  -->
<script type="text/x-handlebars-template" id="dream_day_list_template">
    {{#each this}}
        <div class="row">
            <div class="d-flex align-items-center">
                <i class="bi bi-calendar"></i>{{dreamtDt}}
            </div>
            <div>
                <button type="button" class="btn btn-sm btn-primary"
                        onclick="DreamPiece.regModal({{dreamNo}});"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="꿈 조각 <@spring.message "bs.tooltip.modal.reg"/>">
                    <i class="fas fa-user-circle fs-8 text-white"></i>
                    내 정보
                </button>
            </div>
        </div>
    {{/each}}
</script>
