<!--
 -- clsf(댓글, 태그, 조치, 열람) 모듈 관련 컴포넌트
 -- @included_in : lib/components.ftl
 -->

<#macro viewer_area post >
    <#if isMngr>
        <div class="row mb-5">
            <div class="col-xl-2">
                <div class="col-form-label fs-6 fw-bold mx-5">열람자</div>
            </div>
            <div class="col-xl-10 col-form-label">
                <#if post.viewer??>
                    <#list post.viewer.viewerList![] as viewer>
                        ${viewer.regstrNm!"-"} <#if !viewer?is_last>/</#if>
                    </#list>
                </#if>
            </div>
        </div>
        <!--end:Form-->
        <!--end::Card body-->
        <!--begin::Card footer-->
        <div class="separator separator-dashed border-gray-300 my-8"></div>
    </#if>
</#macro>

<#macro managtr_area post >
    <!--end::Foot-->
    <#if post.managt?? && post.managt.list?? && post.managt.list?size gt 1>
        <div class="separator separator-dashed border-gray-300 mb-8"></div>
        <div class="d-flex flex-stack flex-wrap justify-content-start ">
            <!--begin::Author-->
            <div class="d-flex align-items-center me-5">
                <span class="text-gray-400 fs-6 fw-bold lh-1">
                    작업자:
                </span>
            </div>
            <div class="d-flex align-items-center">
                <#list post.managt.list![] as managtr>
                    <!--begin::Name-->
                    <div class="text-gray-600 fs-6 me-4">
                        <#if (managtr.regstrInfo.proflImgUrl)??>
                            <div class="btn btn-icon btn-active-light-primary position-relative w-15px h-15px w-md-20px h-md-20px me-1">
                                <img src="${(managtr.regstrInfo.proflImgUrl)!}" class="img-thumbnail p-0 w-100" />
                            </div>
                        </#if>
                        <span class="cursor-help"
                              data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="최종 작업일시: &#10;${managtr.regDt!}">
                                ${managtr.regstrNm!"-"}
                            </span>
                        <#if managtr?has_next>, </#if>
                    </div>
                    <!--end::Name-->
                </#list>
            </div>
            <!--end::Author-->
        </div>
    </#if>
</#macro>


