<!--
 -- 수정 권한mdfable 관련 컴포넌트
 -- @included in : lib/macros.ftl
 -->

<#macro mdfable post >
    <#if ((post.mdfable!"") == (Constant.MDFABLE_MNGR)) && isMngr>
        <i class="bi bi-person-lines-fill text-info ms-1 opacity-75"
           data-bs-toggle="tooltip" data-bs-placement="top" title="관리자 수정 가능"></i>
    <#elseif ((post.mdfable!"") == (Constant.MDFABLE_USER)) || ((post.mdfable!"") == (Constant.MDFABLE_ALL))>
        <i class="bi bi-people-fill ms-1 blink-slow"
           data-bs-toggle="tooltip" data-bs-placement="top" title="모든 사용자가 수정 가능"></i>
    </#if>
</#macro>

<#macro list_comment post >
    <#if post.comment?? && post.comment.cnt gt 0>
        <span class="mx-1 text-noti btn-active-warning fs-x-small cursor-pointer opacity-hover"
              onclick="CommentList.modal('${post.postNo!}');"
              data-bs-toggle="tooltip" data-bs-placement="top" title="댓글 모달 팝업 호출">
        [${post.comment.cnt!}]
        </span>
    </#if>
</#macro>

<#macro list_managtr post >
    <#if (post.managtrInfo)?? && (post.managtrInfo.proflImgUrl)??>
        <div class="btn btn-icon btn-active-light-primary position-relative w-15px h-15px w-md-20px h-md-20px me-1">
            <img src="${(post.managtrInfo.proflImgUrl)!}" class="img-thumbnail p-0 w-100" />
        </div>
    </#if>
    <span class="cursor-help"
          data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="최초 작성자:&#10;${post.regstrNm!"-"}">
        ${post.managt.managtrNm!"-"}
    </span>
</#macro>