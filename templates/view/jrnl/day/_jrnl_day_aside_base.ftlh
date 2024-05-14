<#--
 * 저널 일자(jrnl_day) : ASIDE (우측 사이드바)
 * @included_in : jrnl_day_page.ftlh
 -->

<script>
    const JrnlDayAside = (function() {
        return {
            yy: function () {
                $("#jrnl_aside #mnth").val("");
                commons.util.handlebarsTemplate(null, "jrnl_day_list");
                commons.util.handlebarsTemplate([], "jrnl_dream_tag_list");
                JrnlDream.inSearchMode = false;
            },
            mnth: function () {
                // 쿠키 설정하기
                let cookieOptions = {
                    path: "/jrnl/day/",
                    expires: commons.date.getCurrDateAddDay(36135)
                };
                $.cookie("jrnl_yy", $("#jrnl_aside #yy").val(), cookieOptions);
                if ($("#jrnl_aside #mnth").val() === "") return;
                $.cookie("jrnl_mnth", $("#jrnl_aside #mnth").val(), cookieOptions);
                // 목록 조회
                JrnlDay.yyMnthListAjax();
                JrnlTag.dreamTagListAjax();
                //
                JrnlDream.inSearchMode = false;
                // template에 있는 tooltip들 활성화
                $("[data-bs-toggle='tooltip']").tooltip();
            },
        }
    })();
</script>

<#--begin::aside-->
<div id="kt_app_aside" class="app-aside flex-column mt-7"
     data-kt-drawer="true"
     data-kt-drawer-name="app-aside"
     data-kt-drawer-activate="{default: true, lg: false}"
     data-kt-drawer-overlay="true"
     data-kt-drawer-width="auto"
     data-kt-drawer-direction="end"
     data-kt-drawer-toggle="#kt_app_aside_toggle">

    <#--begin::Aside wrapper-->
    <div id="kt_app_aside_wrapper" class="hover-scroll-y px-5 mx-5 my-5"
         data-kt-scroll="true"
         data-kt-scroll-activate="true"
         data-kt-scroll-height="auto"
         data-kt-scroll-dependencies="#kt_app_header"
         data-kt-scroll-wrappers="#kt_app_aside"
         data-kt-scroll-offset="5px"
         data-kt-scroll-save-state="true">

        <#--begin::Aside-->
        <div class="card card-reset card-p-0">
            <#--begin::Card header-->
            <div class="card-header min-h-auto mb-5">
                <h3 class="card-title text-gray-900 fw-bold fs-3">Filters</h3>
                <#--begin::Toolbar-->
                <div class="card-toolbar">
                    <a href="#" class="btn btn-sm btn-icon btn-color-gray-500 btn-light">
                        <i class="ki-duotone ki-magnifier fs-3">
                            <span class="path1"></span>
                            <span class="path2"></span>
                        </i>
                    </a>
                </div>
            </div>
            <!--end::Header-->
            <#--begin::Body-->
            <div class="card-body" id="jrnl_aside">
                <input type="hidden" name="sort" id="sort">
                <#--begin::row-->
                <div class="mb-7 d-flex justify-content-between gap-4">
                    <div class="col">
                        <span class="text-gray-900 fs-h6 fw-bold d-inline-block mb-1">년도</span>
                        <select name="yy" id="yy" class="form-select" aria-label="Select example"
                                onchange="JrnlDayAside.yy();">
                            <option value="">----</option>
                            <#assign currYy = DateUtils.getCurrYy()?number />
                            <#assign startYy = 2011 />
                            <#list currYy..startYy as year>
                                <option value="${year}">${year}</option>
                            </#list>
                        </select>
                    </div>
                    <div class="col">
                        <span class="text-gray-900 fs-h6 fw-bold d-inline-block mb-1">월</span>
                        <select name="mnth" id="mnth" class="form-select" aria-label="Select example"
                                onchange="JrnlDayAside.mnth();">
                            <option value="">--</option>
                            <#list 1..12 as mnth>
                                <option value="${mnth}">${mnth}</option>
                            </#list>
                            <option value="99">전체 월</option>
                        </select>
                    </div>
                </div>
                <#--begin::row-->
                <div class="mb-6">
                    <span class="text-gray-900 fs-h6 fw-bold d-inline-block mb-1">꿈 키워드 검색</span>
                    <div class="d-flex gap-1">
                        <input type="text" name="dreamKeyword" id="dreamKeyword" class="form-control" placeholder="꿈 키워드 검색" />
                        <button type="button" class="btn btn-sm btn-outline btn-light-primary px-4"
                                onclick="JrnlDream.keywordListAjax();"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="꿈을 키워드로 검색합니다.">
                            <i class="bi bi-search pe-0"></i>
                        </button>
                    </div>
                </div>
                <div class="mb-7">
                    <span class="text-gray-900 fs-h6 fw-bold d-inline-block mb-1">Groups</span>
                    <select class="form-select" aria-label="Select example">
                        <option>Any group</option>
                        <option value="1">Grace Green</option>
                        <option value="2">Nick LOgan</option>
                        <option value="3">Carles Nilson</option>
                        <option value="1">Alice Danchik</option>
                        <option value="2">Harris Bold</option>
                        <option value="3">Carles Nilson</option>
                    </select>
                </div>
                <div class="mb-6">
                    <span class="text-gray-900 fs-h6 fw-bold d-inline-block mb-1">Email</span>
                    <input type="text" class="form-control" placeholder="Your Email" />
                </div>
                <!--end::Form-->
                <#--begin::Action-->
                <div class="d-flex flex-column">
                    <a href="#" class="btn btn-primary mb-4 p-3">Default</a>
                    <a href="#" class="btn btn-secondary btn-color-gray-700 p-3">View more</a>
                </div>
                <!--end::Action-->
                <!--end::Body-->
            </div>
        </div>
        <!--end::Aside-->
    </div>
    <!--end::Aside wrapper-->
</div>
<!--end::aside-->