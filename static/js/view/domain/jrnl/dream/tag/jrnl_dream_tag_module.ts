/**
 * jrnl_dream_tag_module.ts
 * 저널 꿈 태그 스크립트 모듈
 *
 * @author nichefish
 */
if (typeof dF === 'undefined') { var dF = {} as any; }
dF.JrnlDreamTag = (function(): dfModule {
    return {
        initialized: false,
        ctgrMap: new Map(),

        /**
         * initializes module.
         */
        init: function(): void {
            if (dF.JrnlDreamTag.initialized) return;

            dF.JrnlDreamTag.getCtgrMap();

            dF.JrnlDreamTag.initialized = true;
            console.log("'dF.JrnlDreamTag' module initialized.");
        },

        getCtgrMap: function(): void {
            const url: string = Url.JRNL_DREAM_TAG_CTGR_MAP_AJAX;
            cF.ajax.get(url, {}, function(res: AjaxResponse): void {
                if (res.rsltMap) dF.JrnlDreamTag.ctgrMap = res.rsltMap;
            });
        },

        /**
         * 목록에 따른 꿈 태그 조회 (Ajax)
         */
        listAjax: function(): void {
            const url: string = Url.JRNL_DREAM_TAG_LIST_AJAX;
            const ajaxData: Record<string, any> = { "yy": $("#jrnl_aside #yy").val(), "mnth": $("#jrnl_aside #mnth").val() };
            cF.ajax.get(url, ajaxData, function(res: AjaxResponse): void {
                if (!res.rslt) {
                    if (cF.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                    return;
                }
                cF.handlebars.template(res.rsltList, "jrnl_dream_tag_list");
            });
        },

        /**
         * 목록에 따른 꿈 태그 (전체) 조회 (Ajax)
         */
        listAllAjax: function(): void {
            const url: string = Url.JRNL_DREAM_TAG_LIST_AJAX;
            const ajaxData: Record<string, any> = { "yy": 9999, "mnth":99 };
            cF.ajax.get(url, ajaxData, function(res: AjaxResponse): void {
                if (!res.rslt) {
                    if (cF.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                    return;
                }
                // 상단에 태그 카테고리 메뉴 생성
                const ctgrSet: Set<string> = new Set();
                res.rsltList.forEach((item: Record<string, string>): void => {
                    if (item.ctgr) ctgrSet.add(item.ctgr);
                });
                cF.handlebars.template(ctgrSet, "jrnl_tag_ctgr");
                cF.handlebars.modal(res.rsltList, "jrnl_tag_list");
                $("#jrnl_tag_dtl_modal").modal("hide");
            });
        },

        /**
         * 목록에 따른 꿈 태그 (전체) 조회 (Ajax)
         */
        dreamTagGroupListAllAjax: function(): void {
            const url: string = Url.JRNL_DREAM_TAG_LIST_AJAX;
            const ajaxData: Record<string, any> = { "yy": 9999, "mnth":99 };
            cF.ajax.get(url, ajaxData, function(res: AjaxResponse): void {
                if (!res.rslt) {
                    if (cF.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                    return;
                }
                const groupedList = dF.Tag.groupTagsByCategory(res.rsltList);
                for (const ctgr in groupedList) {
                    if (!groupedList.hasOwnProperty(ctgr)) continue;
                    const eachList = groupedList[ctgr];
                    cF.handlebars.append({ "ctgr": ctgr, "tagList": eachList }, "jrnl_tag_list");
                }
                $("#jrnl_tag_list_modal").modal("show");
                $("#jrnl_tag_dtl_modal").modal("hide");
            });
        },

        /**
         * 상세 모달 호출
         * @param {string|number} tagNo - 조회할 태그 번호.
         * @param tagNm 태그 이름
         */
        dtlModal: function(tagNo: string|number, tagNm: string): void {
            event.stopPropagation();
            if (isNaN(Number(tagNo))) return;

            // 기존에 열린 모달이 있으면 닫기
            const openModals: NodeList = document.querySelectorAll('.modal.show'); // 열린 모달을 찾기
            openModals.forEach((modal: Node): void => {
                $(modal).modal('hide');  // 각각의 모달을 닫기
            });

            const self = this;
            const func: string = arguments.callee.name; // 현재 실행 중인 함수 참조
            const args: any[] = Array.from(arguments); // 함수 인자 배열로 받기

            const url: string = Url.JRNL_DREAM_TAG_DTL_AJAX;
            const ajaxData: Record<string, any> = { "tagNo": tagNo };
            cF.ajax.get(url, ajaxData, function(res: AjaxResponse): void {
                if (!res.rslt) {
                    if (cF.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                    return;
                }
                cF.handlebars.modal(res.rsltList, "jrnl_dream_tag_dtl");
                document.querySelector("#jrnl_dream_tag_dtl_modal .header_tag_nm").innerHTML = tagNm;
                document.querySelector("#jrnl_dream_tag_dtl_modal .header_tag_cnt").innerHTML = (res.rsltList?.length ?? 0).toString();

                /* modal history push */
                ModalHistory.push(self, func, args);
            });
        },

        /**
         * 모달 닫기 시 수행할 로직
         */
        closeModal: function(): void {
            /* modal history pop */
            ModalHistory.prev();
        },

        expand: function(obj: HTMLElement): void {
            $(obj).prev(".cn").toggleClass("expanded");
        }
    }
})();