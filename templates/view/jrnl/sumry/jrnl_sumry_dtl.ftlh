<#--
 * 저널 결산(jrnl_sumry) : 페이지
 -->

<#import "/layout/layout.ftlh" as layout >

<@layout.layout toolbar=toolbar!"">

<script type="text/javascript" src="/metronic/assets/plugins/custom/tinymce/tinymce.bundle.js"></script>
<script type="text/javascript" src="/js/commons/commons-tinymce.js?20230908"></script>
<link rel="stylesheet" type="text/css" href="/css/dream.css?${releaseDate!}" />

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
            <div class="col-2 d-flex align-items-center fs-5">
                <div class="text-gray-700 d-flex-center me-5">
                    <span class="fw-bold me-2">
                        꿈
                    </span>
                    <i class="bi bi-moon-stars fs-4 me-2"></i>
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
            <div class="col-1 d-flex align-items-center">
                <button type="button" class="btn btn-sm btn-light-primary btn-outlined py-2 px-3 cursor-pointer"
                        onclick="JrnlSumryCn.regModal(${post.postNo!}, '${post.yy!}');"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="저널 일자 <@spring.message "bs.tooltip.modal.mdf"/>">
                    <i class="bi bi-book fs-4 pe-1"></i>
                    저널 결산 내용 등록
                </button>
            </div>
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
                        <#if sumryCn.title??><div class="d-inline-block fw-bold">${sumryCn.title!}</div></#if>
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
                        <#if sumryCn.title??><div class="d-inline-block fw-bold">${sumryCn.title!}</div></#if>
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

<hr class="my-6 text-muted" />
<div class="d-flex align-items-center mb-6 ms-4">
    <div class="col-1 ms-4 me-6 text-center fs-6">
        <b>태그 : </b>
    </div>
    <div class="col" id="jrnl_dream_tag_list_div">
        <#list tagList![] as tag>
            <span class="py-2 me-3 cursor-pointer opacity-hover"
                  onclick="JrnlTag.tagDtlAjax(${tag.tagNo!}, '${tag.tagNm!}');"
                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="해당 태그로 게시물 목록을&#10;필터링합니다.">
                <span class="${tag.tagClass!}">
                    ${tag.tagNm!}
                </span>
                <span class="fs-9 text-noti fw-normal" style="margin-left:-0.25em;">${tag.dreamSize!}</span>
            </span>
        </#list>
    </div>
    <div class="col-1 ms-4 me-6 text-center fs-6 w-50px">

    </div>
</div>

<!--begin::Card-->
<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <div class="row p-5">
            <label class="d-flex align-items-center my-2" for="title">
                <span class="text-gray-700 fs-6 ps-5 fw-bolder">
                    중요한 꿈
                    <i class="bi bi-moon-stars fs-4 pe-1"></i>
                </span>
            </label>
            <#list imprtcDreamList![] as dream>
                <div class="row jrnl-item my-2">
                    <div class="col-1 py-3 jrnl-dream-cn d-flex justify-content-between border-2 border-gray-300 border-end ps-10 me-4 h-75" style="width:135px;">
                        <div>
                            <i class="bi bi-calendar3"></i> ${dream.stdrdDt!}
                        </div>
                    </div>
                    <div class="col py-3 jrnl-sumry-cn bg-secondary">
                        <#if (dream.nhtmrYn == 'Y')><div class="ctgr-span ctgr-imprtc w-auto">!악몽</div></#if>
                        <#if (dream.hallucYn == 'Y')><div class="ctgr-span ctgr-gray w-auto">!입면환각</div></#if>
                        <#if dream.title??><div class="d-inline-block fw-bold">${dream.title!}</div></#if>
                        ${(dream.markdownCn!"-")?no_esc}
                        <#if dream.tag.list??>
                            <div class="ms-2 mt-3">
                                <#list dream.tag.list as tag>
                                <span class="text-muted cursor-pointer pe-1" onclick="Tag.dtlAjax(${tag.refTagNo!});"
                                      data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                                      title="해당 태그가 등록된 게시물 목록 팝업을 호출합니다.">
                                        #<span class="border-bottom text-primary fw-lighter opacity-hover">${tag.tagNm!}</span>
                                    </span>
                                </#list>
                            </div>
                        </#if>
                    </div>
                    <div class="col-1 py-3 jrnl-dream-cn d-flex justify-content-between w-50px gap-1">
                        <button type="button" class="btn btn-sm btn-light-primary btn-outlined ms-2 py-1 px-2 cursor-pointer"
                                onclick="Comment.regModal(${dream.postNo!}, 'JRNL_DREAM');"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="댓글 <@spring.message "bs.tooltip.modal.reg"/>">
                            <i class="bi bi-chat-left-dots p-0"></i>
                        </button>
                    </div>
                    <div class="col-1 ms-4 pe-0 border-2 border-gray-300 border-end h-75 w-10px">&nbsp;</div>
                    <div class="col-1 py-3 jrnl-dream-cn d-flex justify-content-between w-50px gap-1">
                        <button type="button" class="btn btn-sm btn-light-primary btn-outlined ms-2 py-1 px-2 cursor-pointer"
                                onclick="JrnlDream.mdfModal(${dream.postNo!}, '${dream.stdrdDt!}');"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="저널 꿈 <@spring.message "bs.tooltip.modal.mdf"/>">
                            <i class="bi bi-pencil-square p-0"></i>
                        </button>
                        <button type="button" class="btn btn-sm btn-light-danger btn-outlined ms-2 py-1 px-2 cursor-pointer"
                                onclick="JrnlDream.delAjax(${dream.postNo!});"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="저널 꿈 <@spring.message "bs.tooltip.del"/>">
                            <i class="bi bi-trash p-0"></i>
                        </button>
                    </div>
                </div>
                <#if dream.comment.list??>
                    <div class="row py-2">
                        <div class="col-1 jrnl-dream-cn" style="width:135px;">
                            &nbsp;
                        </div>
                        <div class="col ms-10 text-noti">
                            <#list dream.comment.list![] as comment>
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

</@layout.layout>

<#--include::modals-->
<#include "/view/jrnl/sumry_cn/_jrnl_sumry_cn_reg_modal.ftlh">
<#include "/view/jrnl/dream/_jrnl_dream_reg_modal.ftlh">
<#include "/view/jrnl/tag/_jrnl_tag_dtl_modal.ftlh">
<#include "/view/jrnl/tag/_jrnl_tag_list_modal.ftlh">
<#include "/view/_component/modal/comment/_comment_reg_modal.ftlh">
