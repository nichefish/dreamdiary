<#--
  -- 사용자 관리 > 계정 및 권한 관리 > 등록 > 사용자(직원) 정보 추가 Template
  -- @included_in : user_reg_form
  -->
<!--begin::Menu separator-->
<div class="separator my-2"></div>
<!--end::Menu separator-->

<div class="card-body p-9">
    <!--begin::Row-->
    <div class="row mb-4">
        <div class="col-xl-2 col-4">
            <div class="col-form-label text-center fw-bold">주소</div>
        </div>
        <div class="col-xl-2 col-8 col-form-label">
            ${user.profl.addr!"-"} / ${user.profl.zipcode!"-"}
        </div>
    </div>
    <!--begin::Row-->
    <div class="row mb-4">
        <div class="col-xl-2 col-4">
            <div class="col-form-label text-center fw-bold">생년월일</div>
        </div>
        <div class="col-xl-2 col-8 col-form-label">
            ${(user.profl.brthdy)!"-"}
            <#if user.profl.lunarYn! == "Y">
                <span class="badge badge-primary ms-5">음력</span>
            </#if>
        </div>
    </div>
    <!--begin::Row-->
    <div class="row mb-4">
        <div class="col-xl-2 col-4">
            <div class="col-form-label text-center fw-bold">시용자 프로필</div>
        </div>
        <div class="col-xl-9 col-8 col-form-label">
            <div class="div-textarea h-auto min-h-125px">${user.profl.proflCn!}</div>
        </div>
    </div>
</div>