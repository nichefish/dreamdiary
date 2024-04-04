<#--
  -- 게시판 > 상세 화면
  -->
<#import "/layout/layout_without_sidebar.ftlh" as layout >

<@layout.layout>

<script>
    const Page = function() {
        return {
            init: function () {
                //
            },
            close: function() {
                window.close();
            }
        }
    }();
</script>

<div class="card post">
    <!--begin::Question-->
    <div class="mb-0">
        <!-- 게시물post 헤더 제목 영역 -->
        <@component.post_title post />

        <!-- 게시물post 미리보기 헤더 메타 영역 -->
        <@component.post_meta_preview post />

        <div class="separator separator-dashed border-gray-300 my-8"></div>

        <!--begin::cn-->
        <div class="fs-4 fw-normal text-gray-800 px-5 py-1 pb-6">
            ${post.cn?no_esc}
        </div>
    </div>
    <div class="separator separator-dashed border-gray-300 my-8"></div>
    <div class="d-flex justify-content-end">
        <button type="button" class="btn btn-sm btn-light" onclick="Page.close();"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="팝업 창을 닫습니다.">
            <i class="bi bi-list"></i>닫기
        </button>
    </div>

    <form name="procForm" id="procForm">
        <input type="hidden" name="postNo" id="postNo" value="${post.postNo!}" />
        <input type="hidden" name="boardCd" id="boardCd" value="${post.boardCd!}" />
    </form>
</div>

</@layout.layout>