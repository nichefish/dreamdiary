<#--
-- 분류 코드(CL_CD) > 상세 코드(DTL_CD) 등록/수정 Template
  -- @included in : cl_cd_list
  -->
<script type="text/x-handlebars-template" id="dtl_cd_reg_template">
    <input type="hidden" name="clCd" value="{{clCd}}">
    <input type="hidden" name="regYn" id="regYn" value="{{#if (notExists dtlCd)}}Y{{/if}}{{#if (exists dtlCd)}}N{{/if}}">

    <div class="row">
        <div class="col-3 col-form-label text-center">
            <span class="cursor-help"
                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                  title="코드를 특정할 때 내부적으로&#10;사용되는 고유 코드를&#10;입력합니다.&#10;(등록 후 수정할 수 없습니다.)">
                    상세코드
            </span>
            <em class="text-danger">*</em>
        </div>
        <div class="col-4">
            {{#if (exists dtlCd)}}
            <div class="col-form-label">
                <span class="text-noti fw-bold ps-1">{{dtlCd}}</span>
                <input type="hidden" name="dtlCd" value="{{dtlCd}}">
            </div>
            {{/if}}
            {{#if (notExists dtlCd)}}
                <input type="text" name="dtlCd" id="dtlCd" class="form-control form-control-solid required" value="{{dtlCd}}" maxlength="10">
            {{/if}}
        </div>
    </div>
    <div class="row mb-4">
        <div class="col-3">
        </div>
        <div class="col-9">
            {{#if (notExists dtlCd)}}
            <div class="text-noti">※영문 대문자 및 숫자만 가능, 1~20자</div>
            <div id="dtlCdSpan"></div>
            {{/if}}
        </div>
    </div>
    <div class="row">
        <div class="col-3 col-form-label text-center">
            <span class="cursor-help"
                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                  title="외부적으로 사용되는&#10;코드 이름을 입력합니다.">
                상세 코드명
            </span>
            <em class="text-danger">*</em>
        </div>
        <div class="col-9">
            <input type="text" name="dtlCdNm" id="dtlCdNm" class="form-control form-control-solid required" value="{{dtlCdNm}}" maxlength="20">
        </div>
    </div>
    <div class="row mb-4">
        <div class="col-3"></div>
        <div class="col-9">
            <div id="dtlCdNmSpan"></div>
        </div>
    </div>
    <div class="row mb-4">
        <div class="col-3 col-form-label text-center">
            상세 코드 설명
        </div>
        <div class="col-9">
            <input type="text" name="dtlCdDc" id="dtlCdDc" class="form-control form-control-solid" value="{{dtlCdDc}}" maxlength="200">
        </div>
    </div>
    <div class="row mb-4">
        <div class="col-3 col-form-label text-center">
            <span class="cursor-help"
                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                  title="'사용'으로 체크된 코드만&#10;실제 코드 사용시 조회됩니다.">
                사용여부
            </span>
        </div>
        <div class="col-9 text-center d-flex align-items-center">
            <div class="form-check form-switch form-check-custom form-check-solid">
                <input class="form-check-input" type="checkbox" value="Y" name="useYn" id="useYn" {{checkedIf (trueFalse useYn "Y//N")}} />
                <label class="form-check-label ms-3" for="useYn" id="useYnLabel" {{checkedStyle (trueFalse useYn "Y//N") "color" "blue//gray"}}>
                    {{#if (trueFalse useYn "N//Y")}}미{{/if}}사용
                </label>
            </div>
        </div>
    </div>
    <div class="row mb-4">
        <div class="col-3 col-form-label text-center">
            <span class="cursor-help"
                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                  title="표시될 순서를 입력합니다.&#10;순서가 겹칠 경우&#10;내부 로직에 따라 정렬됩니다.">
                정렬 순서
            </span>
        </div>
        <div class="col-3">
            <input type="text" name="sortOrdr" id="sortOrdr" class="form-control form-control-solid" value="{{sortOrdr}}">
        </div>
    </div>
</script>