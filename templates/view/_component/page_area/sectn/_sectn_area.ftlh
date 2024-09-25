<#--
 * 레이아웃 > 단락(section) 모듈
 * @included_in : 단락 기능이 있는 페이지들
 -->

<!--begin::section-->
<#if post.sectn?? && post.sectn.hasSectn>
    <#list post.sectn.list![] as sectn>
        <div class="row d-flex align-items-center mb-4">
            <div class="col fs-3 fw-normal text-gray-800 py-1 border-gray-300 border-end">
                <#if sectn.title??>
                    <div class="fs-1x fw-bold mb-4 text-muted <#if sectn.deprcYn == "Y">strikethrough</#if>">
                        ＜ ${sectn.title!} ＞
                    </div>
                </#if>
                <div class="mx-5 <#if sectn.deprcYn == "Y">text-muted strikethrough</#if>">
                    ${sectn.markdownCn?no_esc}
                </div>
            </div>
            <div class="col-1">
                <button type="button" class="btn btn-sm btn-light-primary btn-outlined ms-2 py-1 px-2 cursor-pointer"
                        onclick="Sectn.mdfModal(${sectn.postNo!});"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="단락 <@spring.message "bs.tooltip.modal.mdf"/>">
                    <i class="bi bi-pencil-square p-0"></i>
                </button>
                <button type="button" class="btn btn-sm btn-light-danger btn-outlined ms-2 py-1 px-2 cursor-pointer"
                        onclick="Sectn.delAjax(${sectn.postNo!});"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="단락 <@spring.message "bs.tooltip.del"/>">
                    <i class="bi bi-trash p-0"></i>
                </button>
            </div>
            <#if sectn?index + 1 < sectn?size>
                <div class="separator separator-dashed border-gray-300 my-6"></div>
            </#if>
        </div>
    </#list>

    <div class="separator separator-dashed border-gray-300 mb-8"></div>
</#if>