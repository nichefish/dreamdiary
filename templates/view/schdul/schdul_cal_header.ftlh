<#--
  -- 일정 > 전체 일정 화면 (달력) 헤더
  -- @included in : schdul_cal
  -->
<style>
    @media (max-width: 768px) {
        .card-header {
            padding-left: 1rem !important;
            padding-right: 1rem !important;
        }
    }
</style>
<!--begin::Card header-->
<div class="card-header d-flex justify-content-between">
    <div class="card-toolbar">
        <div class="d-flex">
            <div class="position-relative me-5">
                <label class="col-form-label text-center fw-bold text-gray-700">조회일시: </label>
            </div>
            <div class="position-relative">
                <span class="menu-icon me-md-2 vertical-middle">
                    <label for="calDt"><i class="bi bi-calendar3 fs-2 cursor-pointer"></i></label>
                </span>
                <input type="text" class="form-control form-control-solid w-sm-150px w-90px fs-7 me-md-2 fw-bolder d-inline" name="calDt" id="calDt" placeholder="Pick date" readonly/>
            </div>
        </div>
        <div class="d-flex">
            <div class="position-relative mx-6 text-muted d-none d-lg-inline">
                |
            </div>
            <div class="position-relative me-5">
                <label class="col-form-label text-center fw-bold text-gray-700">키워드: </label>
            </div>
            <div class="position-relative me-2">
                <!--begin::Svg Icon | path: icons/duotune/general/gen021.svg-->
                <span class="svg-icon svg-icon-3 svg-icon-gray-500 d-none d-sm-inline position-absolute top-50 translate-middle ms-6">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <rect opacity="0.5" x="17.0365" y="15.1223" width="8.15546" height="2" rx="1" transform="rotate(45 17.0365 15.1223)" fill="black"/>
                        <path d="M11 19C6.55556 19 3 15.4444 3 11C3 6.55556 6.55556 3 11 3C15.4444 3 19 6.55556 19 11C19 15.4444 15.4444 19 11 19ZM11 5C7.53333 5 5 7.53333 5 11C5 14.4667 7.53333 17 11 17C14.4667 17 17 14.4667 17 11C17 7.53333 14.4667 5 11 5Z" fill="black"/>
                    </svg>
                </span>
                <!--end::Svg Icon-->
                <input type="text" class="form-control form-control-solid ps-sm-10 ps-3 w-75px w-lg-150px form-control-sm py-3"
                       name="searchKeyword" id="searchKeyword" placeholder="검색.." maxlength="30"/>
                <!--end::Input group-->
            </div>
            <button type="submit" class="btn btn-sm btn-primary position-relative" onclick="Page.search();">
                <i class="bi bi-search"></i>검색
            </button>
            <div class="cursor-pointer ms-1 me-5" data-bs-toggle="collapse" href="#schdul_advanced_search_div">
                <a class="btn btn-sm btn-light-primary h-100 d-flex align-items-center text-gray-700 fs-7 fw-bold">
                <span class="svg-icon svg-icon-muted svg-icon-2 svg-icon-gray-700"
                      data-bs-toggle="tooltip" data-bs-placement="top" title="고급 필터 영역을 열고 닫습니다.">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M19.0759 3H4.72777C3.95892 3 3.47768 3.83148 3.86067 4.49814L8.56967 12.6949C9.17923 13.7559 9.5 14.9582 9.5 16.1819V19.5072C9.5 20.2189 10.2223 20.7028 10.8805 20.432L13.8805 19.1977C14.2553 19.0435 14.5 18.6783 14.5 18.273V13.8372C14.5 12.8089 14.8171 11.8056 15.408 10.964L19.8943 4.57465C20.3596 3.912 19.8856 3 19.0759 3Z" fill="rgba(0, 158, 247, 0.5)"/>
                    </svg>
                </span>
                    <!--end::Svg Icon-->
                    <label class="blink-slow cursor-pointer"
                           data-bs-toggle="tooltip" data-bs-placement="top" title="고급 필터 영역을 열고 닫습니다.">
                        고급필터
                    </label>
                </a>
            </div>
        </div>
    </div>
    <div class="card-toolbar">
        <a href="${SiteUrl.VCATN_PAPR_REG_FORM!}" class="btn btn-sm btn-primary mx-1 btn-sm py-3"
           data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="휴가계획서 등록 화면으로&#10;이동합니다.">
            <i class="fas fa-plus"></i>휴가 등록
        </a>
        <a href="javascript:void(0);" class="btn btn-sm btn-primary mx-2 btn-sm py-3" onclick="Schdul.regForm();"
           data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="일정 등록 팝업을 호출합니다.">
            <i class="fas fa-plus"></i>일정 등록
        </a>
        <a href="javascript:void(0);" class="btn btn-sm btn-light-primary btn-outlined mx-2 btn-sm py-3" onclick="Schdul.prvtRegForm();"
           data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="개인용 일정 등록 팝업을&#10;호출합니다.&#10;(다른 사람에겐 보이지 않는&#10;일정입니다.)&#10;(민감정보는 등록하지 마세요.)">
            <i class="fas fa-plus"></i>개인용 일정<span class="d-none d-lg-inline">등록</span>
        </a>
    </div>
</div>
<!--end::Card header-->
<!--begin::Advance form-->
<div class="collapse px-10 py-4 bg-light w-100" id="schdul_advanced_search_div">
    <div class="d-flex flex-lg-row flex-column align-items-lg-center align-items-start justify-content-end text-muted">
        <div class="mx-3"
             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="내 일정 정보만 조회합니다.&#10;(해당 정보는 브라우저 캐시에&#10;저장됩니다.)">
            <label for="myPaprChk" class="me-2 text-muted cursor-help">내 일정만 보기</label>
            <input type="checkbox" name="myPaprChk" id="schdul_chk_myPaprChk" class="form-check-input" value="Y" onclick="Page.chkbxProp(this);">
        </div>

        <div class="mx-3"
             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="휴가 정보 표시여부를&#10;켜고 끕니다.&#10;(해당 정보는 브라우저 캐시에&#10;저장됩니다.)">
            <label for="outdtChk" class="me-2 text-muted cursor-help">휴가</label>
            <input type="checkbox" name="vcatnChk" id="schdul_chk_vcatnChk" class="form-check-input" value="Y" onclick="Page.chkbxProp(this);">
        </div>
        <div class="mx-3"
             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="내부일정 정보 표시여부를&#10;켜고 끕니다.&#10;(해당 정보는 브라우저 캐시에&#10;저장됩니다.)">
            <label for="outdtChk" class="me-2 text-muted cursor-help">내부일정</label>
            <input type="checkbox" name="indtChk" id="schdul_chk_indtChk" class="form-check-input" value="Y" onclick="Page.chkbxProp(this);">
        </div>
        <div class="mx-3"
             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="외근 정보 표시여부를&#10;켜고 끕니다.&#10;(해당 정보는 브라우저 캐시에&#10;저장됩니다.)">
            <label for="outdtChk" class="me-2 text-muted cursor-help">외근</label>
            <input type="checkbox" name="outdtChk" id="schdul_chk_outdtChk" class="form-check-input" value="Y" onclick="Page.chkbxProp(this);">
        </div>
        <div class="mx-3"
             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="재택근무 정보 표시여부를&#10;켜고 끕니다.&#10;(해당 정보는 브라우저 캐시에&#10;저장됩니다.)">
            <label for="tlcmmtChk" class="me-2 text-muted cursor-help">재택근무</label>
            <input type="checkbox" name="tlcmmtChk" id="schdul_chk_tlcmmtChk" class="form-check-input" value="Y" onclick="Page.chkbxProp(this);">
        </div>
        <div class="mx-3"
             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="개인용 일정 표시여부를&#10;켜고 끕니다.&#10;(해당 정보는 브라우저 캐시에&#10;저장됩니다.)">
            <label for="tlcmmtChk" class="me-2 text-muted cursor-help">개인용 일정</label>
            <input type="checkbox" name="prvtChk" id="schdul_chk_prvtChk" class="form-check-input" value="Y" onclick="Page.chkbxProp(this);">
        </div>
    </div>
</div>
<!--end::Advance form-->
