<#--
 * 레이아웃 > 댓글 모듈
 * @included_in : 댓글 기능이 있는 페이지들
 -->

<#--include::script modules-->
<#include "/view/_component/page_area/comment/_comment_scripts.ftlh">

<div class="separator separator-dashed mt-8"></div>

<div id="comment_area">
    <a id="answers" data-kt-scroll-offset="{default: 100, lg: 125}"></a>
    <h2 class="fw-bolder text-gray-900 my-8 mx-5">댓글(${(post.comment.cnt)!0})</h2>
    <#list post.comment.list![] as comment>
        <div class="border rounded p-2 p-lg-6 mx-8 mb-3">
            <div class="mb-0">
                <div class="d-flex flex-stack flex-wrap mb-5">
                    <div class="d-flex align-items-center py-1">
                        <div class="d-flex flex-column align-items-start justify-content-center">
                            <span class="text-gray-800 fs-6 fw-bold lh-1 mb-2">
                                <#if (comment.regstrInfo.proflImgUrl)??>
                                    <div class="btn btn-icon btn-active-light-primary position-relative w-15px h-15px w-md-20px h-md-20px me-1">
                                        <img src="${(comment.regstrInfo.proflImgUrl)!}" class="img-thumbnail p-0 w-100" />
                                    </div>
                                </#if>
                                ${comment.regstrNm!"-"}
                            </span>
                            <span class="text-muted fs-7 fw-bold lh-1">
                                ${comment.regDt!}<#if (comment.mdfDt!"") != ""> / ${comment.mdfDt!}</#if>
                            </span>
                        </div>
                    </div>
                    <#if comment.isRegstr!false>
                        <div class="d-flex align-items-center py-1">
                            <div id="showMdfBtnDiv${comment.postNo!}">
                                <a href="javascript:void(0);" class="btn btn-sm btn-flex btn-light-primary btn-icon mx-1"
                                   onclick="Comment.mdfForm('${comment.postNo!}');"
                                   data-bs-toggle="tooltip" title="댓글 수정 폼을 호출합니다." data-bs-dismiss="click">
                                    <span class="svg-icon svg-icon-7">
                                        <i class="fas fa-solid fa-pen"></i>
                                    </span>
                                </a>
                                <a href="javascript:void(0);" class="btn btn-sm btn-flex btn-light-danger btn-icon"
                                   onclick="Comment.delAjax('${comment.postNo!}');"
                                   data-bs-toggle="tooltip" title="댓글을 삭제합니다." data-bs-dismiss="click">
                                <span class="svg-icon svg-icon-7">
                                    <i class="fas fa-trash-alt"></i>
                                </span>
                                </a>
                            </div>
                        </div>
                    </#if>
                </div>
                <div class="row">
                    <div class="col-xl-11 fs-5 fw-normal text-gray-800" id="commentSpan${comment.postNo!}">
                        <div class="fs-6 col-form-label" id="commentDtlSpan${comment.postNo!}">
                            <div class="div-textarea-smp" id="commentCnSpanDiv${comment.postNo!}">${comment.cn!}</div>
                        </div>
                    </div>
                    <!-- 가리고 숨기고를 반복하는 곳은 style로 선언해야 한다. -->
                    <div class="col-xl-1 text-center fs-5 fw-normal text-gray-800" id="mdfSaveBtnDiv${comment.postNo!}" style="display:none;">
                        <button type="button" class="btn btn-sm btn-primary me-2 mb-2"
                                onclick="Comment.mdfAjax('${comment.postNo!}');"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="댓글을 수정합니다.">
                            <i class="bi bi-pencil-square"></i>
                            저장
                        </button>
                        <button type="button" class="btn btn-sm btn-secondary me-2"
                                onclick="Comment.closeMdfForm('${comment.postNo!}');"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="댓글 수정 영역을 닫습니다.">
                            닫기
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </#list>

    <div class="separator separator-dashed my-6 mt-8"></div>

    <div class="row mx-5">
        <div class="row ">
            <div class="col-xl-1">
                <div class="fs-6 fw-bold col-form-label vertical-center">
                    <#if (authInfo.proflImgUrl)??>
                        <div class="btn btn-icon btn-active-light-primary position-relative w-15px h-15px w-md-20px h-md-20px me-1">
                            <img src="${(authInfo.proflImgUrl)!}" class="img-thumbnail p-0 w-100" />
                        </div>
                    </#if>
                    ${authInfo.nickNm!}
                </div>
            </div>
            <div class="row col-xl-11">
                <div class="col-xl-11">
                    <#--begin::Form-->
                    <form name="commentRegForm" id="commentRegForm" class="form">
                        <div id="comment_reg_div">
                            <#--handlebar template goes in here-->
                        </div>
                    </form>
                </div>
                <div class="col-xl-1 col-form-label">
                    <#--페이지:: 버튼: 저장하기-->
                    <@component.post_btn_submit "Comment.submit();" />
                </div>
            </div>
        </div>
    </div>
</div>

<div class="col-xl-10" id="commentMdfTemplate" hidden>
    <div class="fs-6 fw-bold col-form-label p-0">
        <#--begin::Form-->
        <form name="commentMdfForm" id="commentMdfForm__INDEX__" class="form">
            <input type="hidden" name="postNo" id="postNo__INDEX__" value="__INDEX__"/>
            <input type="hidden" name="refPostNo" value="${post.postNo!}" />
            <input type="hidden" name="refContentType" value="${post.contentType!}" />
            <input type="hidden" name="actvtyCtgrCd" value="${actvtyCtgrCd!}" >
            <div class="row">
                <div class="col-12">
                    <textarea name="cn" id="commentMdfCn__INDEX__" class="form-control required w-100" rows="3" maxlength="10000"></textarea>
                    <div id="commentMdfCnSpan__INDEX__"></div>
                </div>
            </div>
        </form>
        <!--end:Form-->
    </div>
</div>

<#include "/view/_component/page_area/comment/_comment_reg_template.hbs">
