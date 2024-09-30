<#--
 * 레이아웃 > 단락(section) 모듈
 * @included_in : 단락 기능이 있는 페이지들
 -->

<!--begin::section-->

<#if isMdf!false>
    <div class="separator separator-dashed border-gray-300 mb-8"></div>
    <#--begin::sectn button-->
    <div class="col d-flex justify-content-end">
        <#--헤더:: 버튼: 등록 모달 호출하기-->
        <@component.header_btn_reg_modal "Sectn.regModal(${post.postNo!}, '${post.contentType!}');" />
    </div>

    <div id="sectn_list_div">
        <#if post.sectn?? && post.sectn.hasSectn>
            <div class="separator separator-dashed border-gray-300 my-8"></div>
            <div class="draggable-zone">
                <#list post.sectn.list![] as sectn>
                    <div id="${sectn.postNo!}" class="row d-flex align-items-center mb-4 sortable-item draggable">
                        <div class="col fs-3 fw-normal text-gray-800 py-1 border-gray-300 border-end">
                            <#if sectn.title??>
                                <div class="fs-1x fw-bold mb-4 text-muted <#if sectn.deprcYn == "Y">strikethrough</#if>">
                                    <div class="draggable-handle d-flex align-items-center cursor-move pt-2"
                                         data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="드래그하여&#10;항목을 이동합니다.">
                                        <i class="ki-duotone ki-abstract-14 fs-2x">
                                            <span class="path1"></span>
                                            <span class="path2"></span>
                                        </i>
                                        <#if sectn.title != "">＜ ${sectn.title!} ＞<#else>&nbsp;&nbsp;-&nbsp;&nbsp;</#if>
                                    </div>
                                </div>
                            </#if>
                            <div class="mx-5 fs-4 fw-normal <#if sectn.deprcYn == "Y">text-muted strikethrough</#if>">
                                ${sectn.markdownCn?no_esc}
                            </div>
                            <#if sectn.comment?? && sectn.comment.cnt gt 0>
                                <div class="row py-2 fs-4">
                                    <#list sectn.comment.list![] as comment>
                                        <div class="row ms-10 text-noti my-1 <#if comment?index == 0>mt-3</#if> <#if comment?index == sectn.comment.list?size - 1>mb-3</#if>">
                                            <div class="col div-textarea-smp me-10">- ${comment.markdownCn?no_esc}</div>
                                            <div class="col-1">
                                                <button type="button" class="btn btn-sm btn-light-primary btn-outlined ms-2 py-1 px-2 cursor-pointer"
                                                        onclick="Comment.modal.mdfModal(${comment.postNo!});"
                                                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="저널 꿈 <@spring.message "bs.tooltip.modal.mdf"/>">
                                                    <i class="bi bi-pencil-square p-0"></i>
                                                </button>
                                                <button type="button" class="btn btn-sm btn-light-danger btn-outlined ms-2 py-1 px-2 cursor-pointer"
                                                        onclick="Comment.modal.delAjax(${comment.postNo!});"
                                                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="저널 꿈 <@spring.message "bs.tooltip.del"/>">
                                                    <i class="bi bi-trash p-0"></i>
                                                </button>
                                            </div>
                                        </div>
                                    </#list>
                                </div>
                            </#if>
                        </div>
                        <div class="col-1 py-3 jrnl-dream-cn d-flex justify-content-between w-50px gap-1">
                            <button type="button" class="btn btn-sm btn-light-primary btn-outlined ms-2 py-1 px-2 cursor-pointer"
                                    onclick="Comment.modal.regModal(${sectn.postNo!}, '${sectn.contentType!}');"
                                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="댓글 <@spring.message "bs.tooltip.modal.reg"/>">
                                <i class="bi bi-chat-left-dots p-0"></i>
                            </button>
                        </div>
                        <div class="col-1 ms-4 pe-0 border-2 border-gray-300 border-end h-75 w-10px">&nbsp;</div>
                        <div class="col-1 w-100px">
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
            </div>
        </#if>
    </div>
</#if>