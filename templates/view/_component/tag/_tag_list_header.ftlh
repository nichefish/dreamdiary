<#--
  -- 게시판 > 목록 검색 영역 Header
  -- @included_in : board_post_list
  -->
<#if tagList?? && tagList?has_content>
    <hr class="my-6 text-muted" />
    <div class="d-flex align-items-center mb-6 ms-4">
        <div class="ms-4 me-6"><b>태그 : </b></div>
        <div>
            <#list tagList![] as tag>
                <span class="ctgr-span ctgr-gray cursor-pointer opacity-hover" onclick="Tag.search('${tag.tagNo!}');"
                      data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="해당 태그로 게시물 목록을&#10;필터링합니다.">
                    <span class="ps-1">&#35;</span><span class="border-bottom text-primary fw-normal">${tag.tagNm!}</span>
                    <span class="fs-9 text-noti fw-normal">[${tag.size!0}]</span>
                </span>
            </#list>
        </div>
    </div>
</#if>
