<#--
  -- 일정 > 전체 일정 > 상세 조회 Template
  -- @included_in : schdul_cal
  -->
<script id="schdul_dtl_template" type="text/x-handlebars-template">
    <div class="row mb-3">
        <div class="col-xl-2 col-3 col-form-label">
            <label class="fs-6 fw-bold mb-2">제목</label>
        </div>
        <div class="col-xl-8 col-9 col-form-label">
            [{{schdulTyNm}}] {{schdulNm}}
        </div>
    </div>
    <!--begin::Input group-->
    <div class="row mb-3">
        <div class="col-xl-2 col-3 col-form-label">
            <label class="fs-6 fw-bold mb-2">참여자</label>
        </div>
        <div class="col-xl-10 col-9 col-form-label">
            {{prtcpnt}}
        </div>
    </div>
    <!--begin::Input group-->
    <div class="row mb-3">
        <div class="col-xl-2 col-3 col-form-label">
            <label class="fs-6 fw-bold mb-2">설명</label>
        </div>
        <div class="col-xl-10 col-9 col-form-label">
            <div class="div-textarea-smp h-auto min-h-100px">{{schdulResn}}</div>
        </div>
    </div>
    <!--begin::Input group-->
    <div class="row row-cols-lg-2 mb-3">
        <div class="col-xl-2 col-3 col-form-label">
            <label class="fs-6 fw-bold mb-2">시작일</label>
        </div>
        <div class="col-xl-4 col-3 pe-0 col-form-label">
            <span id="bgnDtDtlSpan">{{bgnDt}}</span>
        </div>
        <div class="col-xl-2 col-3 col-form-label">
            <label class="fs-6 fw-bold mb-2">종료일</label>
        </div>
        <div class="col-xl-4 col-3 pe-0 col-form-label">
            <span id="endDtDtlSpan">{{endDt}}</span>
        </div>
    </div>
</script>