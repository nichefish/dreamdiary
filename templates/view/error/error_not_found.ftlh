<#--
  -- 에러 페이지 : 404 NOT FOUND
  -- (사이드바 없음)
  -->
<#import "/layout/layout_without_sidebar.ftlh" as layout >

<@layout.layout toolbar=toolbar!"">

<div class="d-flex-center">
    <span class="ctgr-span ctgr-imprtc">Error Page</span>
</div>
<div class="d-flex-center mt-3">
    <p>존재하지 않는 경로입니다.</p>
</div>

</@layout.layout>