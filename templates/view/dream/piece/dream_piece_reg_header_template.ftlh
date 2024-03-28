<#--
-- 분류 코드(CL_CD) > 상세 코드(DTL_CD) 등록/수정 Template
  -- @included in : cl_cd_list
  -->
<script type="text/x-handlebars-template" id="dream_piece_reg_header_template">
    <input type="hidden" name="postNo" value="{{postNo}}">
    <input type="hidden" name="dreamDayNo" value="{{dreamDayNo}}">

    <!--begin::Input group-->
    <div class="row row-cols-lg-2 mb-3">
        <div class="col-xl-2 col-2">
            <label class="col-form-label text-center fs-6 fw-bold mb-2">꿈 일자</label>
        </div>
        <div class="col-xl-3 col-5 col-form-label">
            <i class="bi bi-calendar"></i> {{dreamtDt}}
        </div>
        <div class="col-xl-2 col-2">
            <label class="col-form-label text-center fs-6 fw-bold mb-2">인덱스</label>
        </div>
        <div class="col-xl-3 col-5 col-form-label">
            <input class="form-control form-control-solid" name="idx" id="idx" value="{{idx}}" placeholder="인덱스" />
        </div>
    </div>
    <!--begin::Input group-->
    <div class="row row-cols-lg-2 mb-3">
        <div class="col-xl-2 col-2">
            <label class="col-form-label text-center fs-6 fw-bold mb-2">제목</label>
        </div>
        <div class="col-xl-10 col-5 col-form-label">
            <input class="form-control form-control-solid" name="title" id="title"  value="{{title}}" placeholder="제목" />
        </div>
    </div>
</script>