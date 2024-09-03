<#--
 * 저널 결산(jrnl_sumry) : 페이지
 -->

<#import "/layout/layout.ftlh" as layout >

<@layout.layout toolbar=toolbar!"">

<script type="text/javascript" src="/metronic/assets/plugins/custom/tinymce/tinymce.bundle.js"></script>
<script type="text/javascript" src="/js/commons/commons-tinymce.js?20230908"></script>
<link rel="stylesheet" type="text/css" href="/css/jrnl.css?${releaseDate!}" />

<#--include::script modules-->
<#include "/view/jrnl/sumry/_jrnl_sumry_scripts.ftlh">
<#include "/view/jrnl/sumry_cn/_jrnl_sumry_cn_scripts.ftlh">
<#include "/view/jrnl/dream/_jrnl_dream_scripts.ftlh">
<#include "/view/jrnl/tag/_jrnl_tag_scripts.ftlh">
<#include "/view/_component/modal/comment/_comment_scripts.ftlh">

<script>
    const Page = (function() {
        return {
            init: function () {
                //
            },
        }
    })();
    $(document).ready(function() {
        Page.init();
    })
</script>

<!--begin::Card-->
<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <div class="row">
            <div class="col-2 d-flex align-items-center fs-5">
                <i class="bi bi-calendar3 fs-6 me-2"></i>
                <#if post.title??>
                    <span class="fs-5 me-1">${post.title!}</span>
                <#else>
                    <span class="fs-5 fw-bold me-1">${post.yy!}</span>
                    년 결산
                </#if>
            </div>
            <div class="col d-flex align-items-center fs-5">
                <div class="text-gray-700 d-flex-center me-5">
                    <span class="fw-bold me-2">
                        꿈
                    </span>
                    <#if post.dreamComptYn! == "Y">
                        <span class="cursor-help"
                              data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="꿈 기록을 완료했습니다.">
                            <i class="bi bi-moon-stars-fill fs-4 me-2 text-success"></i>
                            <i class="bi bi-check text-success" style="margin-left:-0.8rem"></i>
                        </span>
                    <#else>
                        <span>
                            <i class="bi bi-moon-stars fs-4 me-2"></i>
                        </span>
                    </#if>
                    (<span class="text-underline-dotted ms-1">${post.dreamDayCnt!}</span>일 / <span class="text-underline-dotted ms-1">${post.dreamCnt!}</span>개)
                </div>
                <div class="text-gray-700 d-flex-center">
                    <span class="fw-bold me-2">
                        일기
                    </span>
                    <i class="bi bi-book fs-4 me-2"></i>
                    (<span class="text-underline-dotted ms-1">${post.diaryDayCnt!}</span>일)
                </div>
            </div>
            <div class="col-1 d-flex flex-column align-items-center">
                <button type="button" class="btn btn-sm btn-light-primary btn-outlined py-2 px-3 cursor-pointer"
                        onclick="JrnlSumryCn.regModal(${post.postNo!}, '${post.yy!}');"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="저널 일자 <@spring.message "bs.tooltip.modal.mdf"/>">
                    <i class="bi bi-book fs-4 pe-1"></i>
                    저널 결산 내용 등록
                </button>
                <button type="button" class="btn btn-sm btn-light-primary btn-outlined py-2 px-3 cursor-pointer"
                        onclick="JrnlSumry.comptAjax(${post.postNo!});"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="저널 일자 <@spring.message "bs.tooltip.modal.mdf"/>">
                    <i class="bi bi-book fs-4 pe-1"></i>
                    꿈 기록 완료 처리
                </button>
            </div>
        </div>
    </div>
    <#--begin::Card footer-->
    <div class="card-footer">
        <#--버튼 영역-->
        <div class="d-flex justify-content-end gap-3">
            <div class="col-1 w-auto">
                <select class="form-select form-select-solid text-end" id="yy"
                        onchange="JrnlSumry.dtlByYy(this.value)"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="조회 년도를 변경합니다.">>
                    <#assign currYy = DateUtils.getCurrYy()?number />
                    <#assign startYy = 2011 />
                    <#list currYy..startYy as year>
                        <option value="${year}" ${fn.selected(post.yy!, year)}>${year}년 결산</option>
                    </#list>
                    <option value="2010">2010년 이전 결산</option>
                </select>
            </div>
            <#--페이지:: 버튼: 목록으로 가기-->
            <@component.post_btn_toList "JrnlSumry.list();" />
        </div>
    </div>
</div>

<hr class="my-6 text-muted" />
<div class="d-flex align-items-center mb-6 ms-4">
    <div class="col-1 ms-4 me-6 text-center fs-6">
        <b>일자 태그 : </b>
    </div>
    <div class="col" id="jrnl_diary_tag_list_div">
        <#list dayTagList![] as tag>
            <span class="py-2 me-3 cursor-pointer opacity-hover"
                  onclick="JrnlTag.dtlAjax(${tag.tagNo!}, '${tag.tagNm!}');"
                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="해당 태그로 게시물 목록을&#10;필터링합니다.">
                <span class="${tag.tagClass!}">
                    <#if tag.ctgr?? && tag.ctgr != ""><span class="fs-7 text-noti">[${tag.ctgr!}]</span></#if>
                    ${tag.tagNm!}
                </span>
                <span class="fs-9 text-noti fw-normal" style="margin-left:-0.25em;">${tag.contentSize!}</span>
            </span>
        </#list>
    </div>
    <div class="col-1 ms-4 me-6 text-center fs-6 w-50px">
        <!-- -->
    </div>
</div>

<!--begin::Card-->
<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <div class="row p-5">
            <label class="d-flex align-items-center my-2" for="title">
                <span class="text-gray-700 fs-6 ps-5 fw-bolder">
                    일기
                    <i class="bi bi-book fs-4 pe-1"></i>
                </span>
            </label>
            <#list post.sumryCnDiaryList![] as sumryCn>
                <div class="row jrnl-item">
                    <div class="col-1 py-3 jrnl-dream-cn d-flex justify-content-between border-2 border-gray-300 border-end ps-10 me-4 h-75" style="width:85px;">
                        <div>
                            # ${sumryCn.idx!}
                        </div>
                    </div>
                    <div class="col py-3 jrnl-sumry-cn">
                        <#if (sumryCn.title?? && sumryCn.title != "")><div class="d-inline-block fw-bold">${sumryCn.title!}</div></#if>
                        ${(sumryCn.markdownCn!"-")?no_esc}
                    </div>
                    <div class="col-1 ms-4 pe-0 border-2 border-gray-300 border-end h-75 w-10px">&nbsp;</div>
                    <div class="col-1 py-3 jrnl-dream-cn d-flex justify-content-between w-50px gap-1">
                        <button type="button" class="btn btn-sm btn-light-primary btn-outlined ms-2 py-1 px-2 cursor-pointer"
                                onclick="JrnlSumryCn.mdfModal(${sumryCn.postNo!});"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="저널 꿈 <@spring.message "bs.tooltip.modal.mdf"/>">
                            <i class="bi bi-pencil-square p-0"></i>
                        </button>
                        <button type="button" class="btn btn-sm btn-light-danger btn-outlined ms-2 py-1 px-2 cursor-pointer"
                                onclick="JrnlSumryCn.delAjax(${sumryCn.postNo!});"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="저널 꿈 <@spring.message "bs.tooltip.del"/>">
                            <i class="bi bi-trash p-0"></i>
                        </button>
                    </div>
                </div>
            </#list>
        </div>
    </div>
</div>

<!--begin::Card-->
<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <div class="row p-5">
            <label class="d-flex align-items-center my-2" for="title">
                <span class="text-gray-700 fs-6 ps-5 fw-bolder">
                    꿈
                    <i class="bi bi-moon-stars fs-4 pe-1"></i>
                </span>
            </label>
            <#list post.sumryCnDreamList![] as sumryCn>
                <div class="row jrnl-item">
                    <div class="col-1 py-3 jrnl-dream-cn d-flex justify-content-between border-2 border-gray-300 border-end ps-10 me-4 h-75" style="width:85px;">
                        <div>
                            # ${sumryCn.idx!}
                        </div>
                    </div>
                    <div class="col py-3 jrnl-sumry-cn">
                        <#if (sumryCn.title?? && sumryCn.title != "")><div class="d-inline-block fw-bold">${sumryCn.title!}</div></#if>
                        ${(sumryCn.markdownCn!"-")?no_esc}
                    </div>
                    <div class="col-1 py-3 jrnl-dream-cn d-flex justify-content-between w-50px gap-1">
                        <button type="button" class="btn btn-sm btn-light-primary btn-outlined ms-2 py-1 px-2 cursor-pointer"
                                onclick="Comment.regModal(${sumryCn.postNo!}, 'JRNL_SUMRY_CN');"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="댓글 <@spring.message "bs.tooltip.modal.reg"/>">
                            <i class="bi bi-chat-left-dots p-0"></i>
                        </button>
                    </div>
                    <div class="col-1 ms-4 pe-0 border-2 border-gray-300 border-end h-75 w-10px">&nbsp;</div>
                    <div class="col-1 py-3 jrnl-dream-cn d-flex justify-content-between w-50px gap-1">
                        <button type="button" class="btn btn-sm btn-light-primary btn-outlined ms-2 py-1 px-2 cursor-pointer"
                                onclick="JrnlSumryCn.mdfModal(${sumryCn.postNo!});"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="저널 꿈 <@spring.message "bs.tooltip.modal.mdf"/>">
                            <i class="bi bi-pencil-square p-0"></i>
                        </button>
                        <button type="button" class="btn btn-sm btn-light-danger btn-outlined ms-2 py-1 px-2 cursor-pointer"
                                onclick="JrnlSumryCn.delAjax(${sumryCn.postNo!});"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="저널 꿈 <@spring.message "bs.tooltip.del"/>">
                            <i class="bi bi-trash p-0"></i>
                        </button>
                    </div>
                </div>
                <#if sumryCn.comment.list??>
                    <div class="row py-2">
                        <div class="col-1 py-3 jrnl-dream-cn d-flex justify-content-between ps-10 me-4 h-75" style="width:85px;"></div>
                        <div class="col ms-10 text-noti">
                            <#list sumryCn.comment.list![] as comment>
                                <div class="row pb-1">
                                    ${(comment.cn!)?no_esc}
                                </div>
                            </#list>
                        </div>
                    </div>
                </#if>
            </#list>
        </div>
    </div>
</div>

<!-- 중요 일기 -->
<#include "/view/jrnl/sumry/jrnl_sumry_dtl_imprtc_diary.ftlh">

<!-- 중요 꿈 -->
<#include "/view/jrnl/sumry/jrnl_sumry_dtl_imprtc_dream.ftlh">

<form name="procForm" id="procForm">
    <input type="hidden" name="postNo" id="postNo" value="" />
    <input type="hidden" name="yy" id="yy" value="" />
    <input type="hidden" name="mnth" id="mnth" value="99" />
</form>

<#--include::modals-->
<#include "/view/jrnl/sumry_cn/_jrnl_sumry_cn_reg_modal.ftlh">
<#include "/view/jrnl/dream/_jrnl_dream_reg_modal.ftlh">
<#include "/view/jrnl/dream/tag/_jrnl_dream/tag_dtl_modal.ftlh">
<#include "/view/jrnl/tag/_jrnl_tag_list_modal.ftlh">
<#include "/view/_component/modal/comment/_comment_reg_modal.ftlh">

</@layout.layout>
