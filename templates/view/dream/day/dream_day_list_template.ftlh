<#--
-- 분류 코드(CL_CD) > 상세 코드(DTL_CD) 등록/수정 Template
  -- @included_in : cl_cd_list
  -->
<script type="text/x-handlebars-template" id="dream_day_list_template">
    {{#each this}}
        <div class="row dream-day">
            <div class="row dreamday-header">
                <div class="col-1 d-flex align-items-center fs-6 fw-bold">
                    <i class="bi bi-calendar3 pe-1"></i> {{dreamtDt}}
                </div>
                <div class="col d-flex align-items-center">
                    <button type="button" class="btn btn-sm btn-light-primary btn-outlined ps-4 pe-3 me-2 py-2 cursor-pointer"
                            onclick="DreamPiece.regModal({{dreamDayNo}}, '{{dreamtDt}}');"
                            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="꿈 조각 <@spring.message "bs.tooltip.modal.reg"/>">
                        <i class="bi bi-cloud-plus fs-4 pe-1"></i>
                        꿈 조각 등록
                    </button>
                    <button type="button" class="btn btn-sm btn-light-primary btn-outlined py-2 px-3 cursor-pointer"
                            onclick="DreamDay.mdfModal({{dreamDayNo}});"
                            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="꿈 조각 <@spring.message "bs.tooltip.modal.mdf"/>">
                        <i class="bi bi-pencil-square p-0"></i>
                    </button>
                </div>
            </div>
            <div class="row dreampieces p-5">
                {{#each dreamPieceList}}
                    <div class="row dream-piece">
                        <div class="col-1 py-3 dream-piece-cn d-flex justify-content-between" style="width:85px;">
                            <div>
                                # {{idx}}
                            </div>
                            <button type="button" class="btn btn-sm btn-light-primary btn-outlined ms-2 py-1 px-2 cursor-pointer"
                                    onclick="DreamPiece.mdfModal({{dreamDayNo}}, '{{dreamtDt}}');"
                                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="꿈 조각 <@spring.message "bs.tooltip.modal.mdf"/>">
                                <i class="bi bi-pencil-square p-0"></i>
                            </button>
                        </div>
                        <div class="col-1 ms-4 pe-0 border-2 border-gray-300 border-start h-75" style="width:10px;">&nbsp;</div>
                        <div class="col py-3 dream-piece-cn">
                            {{{cn}}}
                        </div>
                    </div>
                {{else}}
                    <div class="row dream-piece">
                        <div class="col-1 text-center" style="width:85px;">-</div>
                        <div class="col-1 ms-4 pe-0 border-2 border-gray-300 border-start h-75" style="width:10px;">&nbsp;</div>
                        <div class="col-1 py-3 dream-piece-cn">
                            꿈 조각 정보가 없습니다.
                        </div>
                    </div>
                {{/each}}
            </div>
        </div>
        <div class="separator separator-dashed border-gray-300 my-8"></div>
    {{/each}}
</script>
