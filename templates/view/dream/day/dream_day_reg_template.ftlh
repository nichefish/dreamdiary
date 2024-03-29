<#--
-- 분류 코드(CL_CD) > 상세 코드(DTL_CD) 등록/수정 Template
  -- @included in : cl_cd_list
  -->
<script type="text/x-handlebars-template" id="dream_day_reg_template">
    <input type="hidden" name="dream_day_no" value="{{dream_day_no}}">

    <!--begin::Input group-->
    <div class="row row-cols-lg-2 mb-3">
        <div class="col-xl-2 col-2">
            <label class="col-form-label text-center fs-6 fw-bold mb-2" for="dreamtDt">날짜</label>
        </div>
        <div class="col-xl-3 col-5">
            <input class="form-control form-control-solid required" name="dreamtDt" id="dreamtDt" placeholder="꿈꾼 일자" value="{{dreamtDt}}" readonly/>
        </div>
        <div class="col-xl-2 col-5">
            <label class="col-form-label text-end fs-6 fw-bold mb-2" for="schdulResn">*날짜미상</label>
        </div>
        <div class="col-xl-3 col-5 d-flex align-items-center">
            <div class="form-check form-switch form-check-custom form-check-solid">
                <input class="form-check-input" type="checkbox" value="Y" name="useYn" id="useYn" {{checkedIf (trueFalse useYn "Y//N")}} />
                <label class="form-check-label ms-3" for="useYn" id="useYnLabel" {{checkedStyle (trueFalse useYn "Y//N") "color" "blue//gray"}}>
                    {{#if (trueFalse useYn "N//Y")}}미{{/if}}사용
                </label>
            </div>
        </div>
        <div class="col-xl-2 col-2 col-form-label"></div>
        <div class="col-xl-5 col-5">
            <span id="bgnDtSpan"></span>
        </div>
    </div>
</script>