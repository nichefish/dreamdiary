<#--
 * 레이아웃 > NOTION 참조 영역
 -->

<#--begin::Row-->
<#if post.notionPageId?? && post.notionPageId != "">
    <div class="d-flex flex-stack flex-wrap mb-3 bg-light">
        <div class="col-xl-2 col-form-label fs-6 fw-bold px-5"
             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="Notion 페이지에 대한&#10;참조 정보입니다.">
            <span class="cursor-help">
                노션 페이지 참조 ID
            </span>
        </div>
        <div class="col-xl-10">
            <div class="row my-1">
                <div class="col-xl-3">
                    <span class="svg-icon svg-icon-muted svg-icon-2x">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path opacity="0.3" d="M5 8.04999L11.8 11.95V19.85L5 15.85V8.04999Z" fill="currentColor"></path>
                            <path d="M20.1 6.65L12.3 2.15C12 1.95 11.6 1.95 11.3 2.15L3.5 6.65C3.2 6.85 3 7.15 3 7.45V16.45C3 16.75 3.2 17.15 3.5 17.25L11.3 21.75C11.5 21.85 11.6 21.85 11.8 21.85C12 21.85 12.1 21.85 12.3 21.75L20.1 17.25C20.4 17.05 20.6 16.75 20.6 16.45V7.45C20.6 7.15 20.4 6.75 20.1 6.65ZM5 15.85V7.95L11.8 4.05L18.6 7.95L11.8 11.95V19.85L5 15.85Z" fill="currentColor"></path>
                        </svg>
                    </span>
                    <span>${post.notionPageId!}</span>
                    <a class="badge badge-secondary p-2 btn-white blank blink-slow float-end"
                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="모달로 보기">
                        <i class="bi bi-stickies fs-5 text-noti opacity-hover" onclick="Page.notionModal('${post.notionPageId!}');"></i>
                    </a>
                </div>
            </div>
        </div>
    </div>
</#if>

<#include "/global/common/notion/notion_page_modal.ftlh">