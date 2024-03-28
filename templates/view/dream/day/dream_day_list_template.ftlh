<#--
-- 분류 코드(CL_CD) > 상세 코드(DTL_CD) 등록/수정 Template
  -- @included in : cl_cd_list
  -->
<script type="text/x-handlebars-template" id="dream_day_list_template">
    {{#each this}}
        <div class="row dreamday">
            <div class="row dreamday-header">
                <div class="col-1 d-flex align-items-center">
                    <i class="bi bi-calendar"></i> {{dreamtDt}}
                </div>
                <div class="col d-flex align-items-center">
                    <button type="button" class="btn btn-sm btn-primary"
                            onclick="DreamPiece.regModal({{dreamDayNo}}, '{{dreamtDt}}');"
                            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="꿈 조각 <@spring.message "bs.tooltip.modal.reg"/>">
                        <i class="fas fa-user-circle fs-8 text-white"></i>
                        꿈 조각 등록
                    </button>
                </div>
            </div>
            <div class="row dreampieces">
                {{#each dreamPieces}}
                    <div class="row dreampiece">
                        <div>!!!!</div>
                    </div>
                {{else}}
                    <div class="row dreampiece">
                        <div>꿈 조각 정보가 없습니다.</div>
                    </div>
                {{/each}}
            </div>
        </div>
    {{/each}}
</script>
