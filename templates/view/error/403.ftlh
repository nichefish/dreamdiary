<#--
  -- 에러 페이지 : 403 ACCESS DENIED
  -- (사이드바 없음)
  -->
<#import "/layout/layout_without_sidebar.ftlh" as layout >

<@layout.layout>

<div class="d-flex-center">
    <span class="ctgr-span ctgr-imprtc">Access Denied</span>
</div>
<div class="d-flex-center mt-3">
    <p>접근 권한이 없습니다.</p>
</div>

</@layout.layout>