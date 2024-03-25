<!--
 -- 레이아웃 > 댓글 모듈
 -- @included in : 댓글 기능이 있는 페이지들
 -->
<script>
    let Comment = function() {
        return {
            init: function () {
                /** jquery :: validation */
                commons.validate.validateForm("#commentRegForm", Comment.regAjax);
            },
            /** 댓글 입력 처리 (ajax) */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.BOARD_COMMENT_REG_AJAX!}";
                    let ajaxData = $("#commentRegForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();;
                            });
                    });
                });
            },
            /** 댓글 수정 폼 생성 */
            mdfForm: function(commentNo) {
                $("#commentDtlSpan" + commentNo).hide();
                let str = $("#commentMdfTemplate").html().replace(/__INDEX__/g, commentNo);
                $("#commentSpan" + commentNo).append(str);
                $("#commentMdfCn" + commentNo).html($("#commentCnSpanDiv" + commentNo).html());
                $("#showMdfBtnDiv" + commentNo).hide();
                $("#mdfSaveBtnDiv" + commentNo).show();
                commons.validate.validateForm("#commentMdfForm", Comment.mdfAjax);
            },
            /** 댓글 수정 폼 닫기 */
            fnCloseMdfForm: function(commentNo) {
                $("#commentDtlSpan" + commentNo).show();
                $("#commentSpan" + commentNo).empty();
                $("#commentMdfCn" + commentNo).empty();
                $("#showMdfBtnDiv" + commentNo).show();
                $("#mdfSaveBtnDiv" + commentNo).hide();
            },
            /** 댓글 수정 처리 (ajax) */
            mdfAjax: function(commentNo) {
                if (commons.util.isEmpty($("#commentMdfCn" + commentNo), "value")) {
                    Swal.fire("댓글 내용을 입력해주세요.");
                    return false;
                }
                Swal.fire({
                    text: <@spring.message "view.cnfm.mdf"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.BOARD_COMMENT_MDF_AJAX!}";
                    let ajaxData = $("#commentMdfForm" + commentNo).serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 댓글 삭제 처리 (ajax) */
            delAjax: function(commentNo) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function (result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.BOARD_COMMENT_DEL_AJAX!}";
                    let ajaxData = {"commentNo": commentNo};
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            }
        }
    }();

    $(document).ready(function() {
        Comment.init();
    });
</script>

<div class="separator separator-dashed mt-8"></div>
<div id="commentArea">
    <a id="answers" data-kt-scroll-offset="{default: 100, lg: 125}"></a>
    <h2 class="fw-bolder text-gray-900 my-8 mx-5">댓글(${post.commentCnt!})</h2>
    <#list post.commentList![] as comment>
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
                            <div id="showMdfBtnDiv${comment.commentNo!}">
                                <a href="javascript:void(0);" class="btn btn-sm btn-flex btn-light-primary btn-icon mx-1"
                                   onclick="Comment.mdfForm('${comment.commentNo!}');"
                                   data-bs-toggle="tooltip" title="댓글 수정 폼을 호출합니다." data-bs-dismiss="click">
                                <span class="svg-icon svg-icon-7">
                                    <i class="fas fa-solid fa-pen"></i>
                                </span>
                                </a>
                                <a href="javascript:void(0);" class="btn btn-sm btn-flex btn-light-danger btn-icon"
                                   onclick="Comment.delAjax('${comment.commentNo!}');"
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
                    <div class="col-xl-11 fs-5 fw-normal text-gray-800" id="commentSpan${comment.commentNo!}">
                        <div class="fs-6 col-form-label" id="commentDtlSpan${comment.commentNo!}">
                            <div class="div-textarea-smp" id="commentCnSpanDiv${comment.commentNo!}">${comment.commentCn!}</div>
                        </div>
                    </div>
                    <!-- 가리고 숨기고를 반복하는 곳은 style로 선언해야 한다. -->
                    <div class="col-xl-1 text-center fs-5 fw-normal text-gray-800" id="mdfSaveBtnDiv${comment.commentNo!}" style="display:none;">
                        <button type="button" class="btn btn-sm btn-primary me-2 mb-2"
                                onclick="Comment.mdfAjax('${comment.commentNo!}');"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="댓글을 수정합니다.">
                            <i class="bi bi-pencil-square"></i>
                            저장
                        </button>
                        <button type="button" class="btn btn-sm btn-secondary me-2"
                                onclick="Comment.fnCloseMdfForm('${comment.commentNo!}');"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="댓글 수정 영역을 닫습니다.">
                            닫기
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </#list>

    <div class="row mx-5">
        <div class="separator separator-dashed my-6 mt-8"></div>
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
            <div class="col-xl-11">
                <form name="commentRegForm" id="commentRegForm" class="form">
                    <input type="hidden" name="commentNo" id="commentNo"/>
                    <input type="hidden" name="postNo" value="${post.postNo!}" placeholder="postNo">
                    <input type="hidden" name="boardCd" value="${post.boardCd!}" placeholder="boardCd"/>
                    <div class="row">
                        <div class="col-xl-11 form-group mb-2">
                            <textarea name="commentCn" id="commentRegCn" class="form-control required" rows="2" maxlength="10000" placeholder="댓글 작성..." data-kt-autosize="true"
                                      data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="댓글 내용을 입력합니다."></textarea>
                            <div id="commentRegCnSpan"></div>
                        </div>
                        <div class="col-xl-1">
                            <button type="button" class="btn btn-sm btn-primary me-2"
                                    onclick="commons.util.submit('#commentRegForm');"
                                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="댓글을 등록합니다.">
                                <i class="bi bi-pencil-square"></i>
                                등록
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<div class="col-xl-10" id="commentMdfTemplate" hidden>
    <div class="fs-6 fw-bold col-form-label p-0">
        <form name="commentMdfForm" id="commentMdfForm__INDEX__" class="form">
            <input type="hidden" name="commentNo" id="commentNo__INDEX__" value="__INDEX__"/>
            <input type="hidden" name="postNo" value="${post.postNo!}" placeholder="postNo">
            <input type="hidden" name="boardCd" value="${post.boardCd!}" placeholder="boardCd"/>
            <div class="row">
                <div class="col-12">
                    <textarea name="commentCn" id="commentMdfCn__INDEX__" class="form-control required w-100" rows="3" maxlength="10000"></textarea>
                    <div id="commentMdfCnSpan__INDEX__"></div>
                </div>
            </div>
        </form>
        <!--end:Form-->
    </div>
</div>