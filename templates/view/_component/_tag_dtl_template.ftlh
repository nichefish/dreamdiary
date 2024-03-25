<!--
 -- 레이아웃 > 첨부파일 모듈
 -- @included in : 파일 첨부 기능이 있는 페이지들
 -->
<#if post.tagStrList?has_content>
    <!--begin::Row-->
    <div class="d-flex flex-stack flex-wrap mb-3 bg-light">
        <div class="col-xl-1 col-form-label fs-6 fw-bold px-5">
            태그
        </div>
        <div class="col-xl-11 text-sm-start" id="tagSpan">
            <#list post.tagStrList as tag>
                <span class="ctgr-span ctgr-gray cursor-pointer" onclick="Tag.fnDtlAjax('${tag}');"
                      data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                      title="해당 태그가 등록된 게시물 목록&#10;팝업을 호출합니다.">
                            <span class="ps-1">&#35;</span><u class="text-primary fw-normal">${tag}</u>
                        </span>
            </#list>
        </div>
    </div>
</#if>