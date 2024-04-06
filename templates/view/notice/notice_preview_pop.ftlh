<#--
  -- 공지사항 > 글작성 미리보기 팝업
  -- (사이드바 없음)
  -->
<#import "/layout/layout_without_sidebar.ftlh" as layout >

<@layout.layout toolbar=toolbar!"">

<script>
    const Page = (function() {
        return {
            init: function () {
                //
            },
            close: function() {
                window.close();
            }
        }
    })();
</script>

<form name="procForm" id="procForm">
    <input type="hidden" name="postNo" id="postNo" value="${post.postNo!}" />
</form>
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

    <!-- 버튼 영역 -->
    <div class="d-flex justify-content-end">
        <!-- 버튼:: 팝업 닫기 -->
        <@component.btn_preview_close "Page" />
    </div>
</div>

</@layout.layout>