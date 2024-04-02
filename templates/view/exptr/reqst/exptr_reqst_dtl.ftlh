<#--
  -- 경비 > 물품구매/경조사비 신청 > 상세 화면
  -->
<#import "/layout/layout.ftlh" as layout >

<@layout.layout>

<script>
    const Page = function() {
        return {
            init: function () {
                //
            },
            /** 수정 화면 이동 */
            mdfForm: function() {
                commons.util.blockUISubmit("#procForm", "${SiteUrl.EXPTR_REQST_MDF_FORM!}");
            },
            /** 삭제 처리 (ajax) */
            delAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.EXPTR_REQST_DEL_AJAX!}";
                    let ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) Page.list();
                            });
                    });
                });
            },
            /** 관리자 확인 여부 변경 (Ajax) */
            cfAjax: function() {
                Swal.fire({
                    text: "완료 처리하시겠습니까?",
                    showCancelButton: true,
                }).then(function (result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.EXPTR_REQST_CF_AJAX!}";
                    let ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 항목 미처리 (Ajax) */
            dismissAjax: function() {
                Swal.fire({
                    text: "취소(기각) 처리하시겠습니까?",
                    showCancelButton: true,
                }).then(function (result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.EXPTR_REQST_DISMISS_AJAX!}";
                    let ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 목록 화면 이동 */
            list: function() {
                commons.util.blockUIReplace("${SiteUrl.EXPTR_REQST_LIST!}?isBackToList=Y");
            }
        }
    }();
</script>

<div class="card post">
    <!--begin::Question-->
    <div class="mb-0">
        <!-- 게시물post 헤더 제목 영역 -->
        <@component.post_title post />

        <!-- 게시물post 헤더 메타 영역 -->
        <@component.post_meta_preview post />

        <div class="separator separator-dashed border-gray-300 my-8"></div>

        <!--begin::cn-->
        <div class="fs-4 fw-normal text-gray-800 px-5 py-1 pb-6">
            ${post.cn?no_esc}
        </div>

        <div class="separator separator-dashed border-gray-300 mb-8"></div>

        <!-- #include: 첨부파일 표시 영역 -->
        <#include "/view/_component/atch_file/_atch_file_dtl_area.ftlh">

    </div>
    <div class="separator separator-dashed border-gray-300 my-8"></div>

    <!-- 열람자 표시 영역 -->
    <@component.viewer_area post />

    <#if isMngr>
        <div class="row mb-5">
            <div class="col-xl-2">
                <div class="col-form-label fs-6 fw-bold mx-5">
                    <label for="cfYn">관리자 확인</label>
                </div>
            </div>
            <div class="col-xl-10">
                <div class="form-check form-switch mt-2 form-check-custom form-check-solid">
                    <#if post.cfYn == "N">
                        <span class="text-muted me-10">
                            미처리<i class="bi bi-dash text-muted"></i>
                        </span>
                        <button class="btn btn-sm btn-primary" onclick="Page.cfAjax();">처리완료하기</button>
                        <button class="btn btn-sm btn-secondary ms-4" onclick="Page.dismissAjax();">취소(기각) 처리하기</button>
                    <#elseif post.cfYn == "X">
                        <button class="btn btn-sm btn-secondary" disabled>
                            취소<i class="bi bi-x text-dark"></i>
                        </button>
                        <span class="ms-3 text-muted">처리하지 않습니다.</span>
                    <#elseif post.cfYn == "Y">
                        <button class="btn btn-sm btn-success" disabled>
                            처리완료<i class="bi bi-check"></i>
                        </button>
                        <span class="ms-3 text-success">처리 완료되었습니다.</span>
                    </#if>
                </div>
            </div>
        </div>
        <!--end:Form-->
        <!--end::Card body-->
        <!--begin::Card footer-->
        <div class="separator separator-dashed border-gray-300 my-8"></div>
    </#if>

    <!-- 버튼 영역 -->
    <div class="d-flex justify-content-end">
        <@component.btn_mdfable post isMngr />
    </div>

    <!-- 댓글 영역 -->
    <#include "/view/_component/comment/_comment_area.ftlh">

    <form name="procForm" id="procForm">
        <input type="hidden" name="postNo" id="postNo" value="${post.postNo!}" />
    </form>
</div>

</@layout.layout>