<#--
 -- 게시판 > 목록 툴바 Header
 -- @included_in : board_post_list
 -->
<#if tagList?? && tagList?has_content>
    <hr class="my-6 text-muted" />
    <div class="d-flex align-items-center mb-6 ms-4">
        <div class="col-1 ms-4 me-6 text-center fs-6"><b>태그 : </b></div>
        <div class="col-10">
            <#list tagList![] as tag>
                <span class="py-2 me-3 cursor-pointer opacity-hover"
                      onclick="Tag.search('${tag.tagNo!}');"
                      data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="해당 태그로 게시물 목록을&#10;필터링합니다.">
                    <span class="${tag.tagClass!}">
                        <#if tag.ctgr != ""><span class="fs-7 text-noti">[${tag.ctgr!}]</span></#if>
                        <span class="em_${tag.tagNm!}">${tag.tagNm!}</span>
                    </span>
                    <span class="fs-9 text-noti fw-normal" style="margin-left:-0.25em;">${tag.size!0}</span>
                </span>
            </#list>
        </div>
    </div>
</#if>
