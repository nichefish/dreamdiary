<#--
  -- 에러 페이지 : 404 NOT FOUND
  -- (사이드바 없음)
  -->
<#import "/layout/layout_no_aside.ftlh" as layout >
<#import "/spring.ftl" as spring />

<@layout.layout>

<div class="d-flex-center">
    <span class="ctgr-span ctgr-imprtc">Error Page</span>
</div>
<div class="d-flex-center mt-3">
    <p>존재하지 않는 경로입니다.</p>
</div>

</@layout.layout>