/**
 * jrnl_dream_module.ts
 * 저널 꿈 스크립트 모듈
 *
 * @author nichefish
 */
if (typeof dF === 'undefined') { var dF = {} as any; }
dF.JrnlDream = (function(): dfModule {
    return {
        initialized: false,
        inKeywordSearchMode: false,
        tagify: null,

        savedYy: null,
        savedMnth: null,

        /**
         * initializes module.
         */
        init: function(): void {
            if (dF.JrnlDream.initialized) return;

            /* initialize submodules. */
            dF.JrnlDreamTag.init();

            dF.JrnlDream.initialized = true;
            console.log("'dF.JrnlDream' module initialized.");
        },

        /**
         * form init
         * @param {Record<string, any>} obj - 폼에 바인딩할 데이터
         */
        initForm: function(obj: Record<string, any> = {}): void {
            /* show modal */
            cF.handlebars.modal(obj, "jrnl_dream_reg", ["header"]);

            /* jquery validation */
            cF.validate.validateForm("#jrnlDreamRegForm", dF.JrnlDream.regAjax, {
                rules: {
                    elseDreamerNm: {
                        required: function() {
                            return $("#jrnlDreamRegForm #elseDreamYn").prop(":checked", false);
                        }
                    },
                },
                ignore: undefined
            });
            // 체크박스 상태 변경시 필드 재검증
            $("#elseDreamYn").change(function(): void {
                $("#elseDreamerNm").valid();
            });
            // checkbox init
            cF.ui.chckboxLabel("imprtcYn", "중요//해당없음", "red//gray");
            // checkbox init
            cF.ui.chckboxLabel("nhtmrYn", "악몽//해당없음", "red//gray");
            // checkbox init
            cF.ui.chckboxLabel("hallucYn", "입면환각//해당없음", "blue//gray");
            // checkbox init
            cF.ui.chckboxLabel("elseDreamYn", "해당//미해당", "blue//gray", function(): void {
                $("#elseDreamerNmDiv").removeClass("d-none");
            }, function(): void {
                $("#elseDreamerNmDiv").addClass("d-none");
            });
            /* tinymce editor reset */
            cF.tinymce.init('#tinymce_jrnlDreamCn');
            cF.tinymce.setContentWhenReady("tinymce_jrnlDreamCn", obj.cn || "");
            /* tagify */
            dF.JrnlDream.tagify = cF.tagify.initWithCtgr("#jrnlDreamRegForm #tagListStr", dF.JrnlDreamTag.ctgrMap);
        },

        /**
         * 목록 조회 (Ajax)
         */
        keywordListAjax: function(): void {
            const keyword: string = $("#jrnl_aside #dreamKeyword").val() as string;
            if (cF.util.isEmpty(keyword)) return;

            // 검색 시 기존 년월 저장
            dF.JrnlDream.savedYy = $("#jrnl_aside #yy").val() as string;
            dF.JrnlDream.savedMnth = $("#jrnl_aside #mnth").val() as string;

            const url: string = Url.JRNL_DREAM_LIST_AJAX;
            const ajaxData = { "dreamKeyword": $("#jrnl_aside #dreamKeyword").val() };
            cF.ajax.get(url, ajaxData, function(res: AjaxResponse): void {
                if (!res.rslt) {
                    if (cF.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                    return;
                }
                $("#jrnl_aside #yy").val("");
                $("#jrnl_aside #mnth").val("");
                // 목록 영역 클리어
                $("#jrnl_aside #diaryKeyword").val("");
                $("#jrnl_day_list_div").empty();
                $("#jrnl_diary_list_div").empty();
                // 태그 헤더 클리어
                $("#jrnl_day_tag_list_div").empty();
                $("#jrnl_diary_tag_list_div").empty();
                $("#jrnl_dream_tag_list_div").empty();
                cF.ui.closeModal();
                cF.handlebars.template(res.rsltList, "jrnl_dream_list");
                dF.JrnlDream.inKeywordSearchMode = true;
                // 버튼 추가
                $("#jrnl_aside #jrnl_diary_reset_btn").remove();
                const resetBtn = $(`<button type="button" id="jrnl_dream_reset_btn" class="btn btn-sm btn-outline btn-light-danger px-4" 
                                          onclick="dF.JrnlDream.resetKeyword();" data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                                          aria-label="꿈 키워드 검색을 리셋합니다." 
                                          data-bs-original-title="꿈 키워드 검색을 리셋합니다." data-kt-initialized="1">
                                     <i class="bi bi-x pe-0"></i>
                                  </button>`);
                $("#jrnl_aside #jrnl_dream_search_btn").after(resetBtn);
                resetBtn.tooltip();
            }, "block");
        },

        /**
         * 키워드 검색 종료
         */
        resetKeyword: function(): void {
            $("#jrnl_aside #jrnl_dream_reset_btn").remove();
            $("#jrnl_aside #yy").val(dF.JrnlDream.savedYy);
            $("#jrnl_aside #mnth").val(dF.JrnlDream.savedMnth);
            dF.JrnlDayAside.mnth();
        },

        /**
         * 등록 모달 호출
         * @param {string|number} jrnlDayNo - 저널 일자 번호.
         * @param {string} stdrdDt - 기준 날짜.
         * @param {string} jrnlDtWeekDay - 기준 날짜 요일.
         */
        regModal: function(jrnlDayNo: string|number, stdrdDt: string, jrnlDtWeekDay: string): void {
            if (isNaN(Number(jrnlDayNo))) return;

            const obj = { jrnlDayNo: jrnlDayNo, stdrdDt: stdrdDt, jrnlDtWeekDay: jrnlDtWeekDay };
            /* initialize form. */
            dF.JrnlDream.initForm(obj);
        },

        /**
         * form submit
         */
        submit: function(): void {
            tinymce.get("tinymce_jrnlDreamCn").save();
            $("#jrnlDreamRegForm").submit();
        },

        /**
         * 등록 (Ajax)
         */
        regAjax: function(): void {
            const postNoElmt: HTMLInputElement = document.querySelector("#jrnlDreamRegForm [name='postNo']") as HTMLInputElement;
            const isReg: boolean = postNoElmt?.value === "";
            Swal.fire({
                text: Message.get(isReg ? "view.cnfm.reg" : "view.cnfm.mdf"),
                showCancelButton: true,
            }).then(function(result: SwalResult): void {
                if (!result.value) return;

                const url: string = isReg ? Url.JRNL_DREAM_REG_AJAX : Url.JRNL_DREAM_MDF_AJAX;
                const ajaxData: FormData = new FormData(document.getElementById("jrnlDreamRegForm") as HTMLFormElement);
                cF.$ajax.multipart(url, ajaxData, function(res: AjaxResponse): void {
                    Swal.fire({ text: res.message })
                        .then(function(): void {
                            if (!res.rslt) return;

                            const isCalendar: boolean = Page?.calendar !== undefined;
                            if (isCalendar) {
                                Page.refreshEventList();
                                dF.JrnlDreamTag.listAjax();     // 태그 refresh
                            } else {
                                if (dF.JrnlDream.inKeywordSearchMode) {
                                    dF.JrnlDream.keywordListAjax();
                                } else {
                                    dF.JrnlDay.yyMnthListAjax();
                                    dF.JrnlDreamTag.listAjax();         // 태그 refresh
                                }
                            }

                            // TODO: 결산 페이지에서 처리시도 처리해 줘야 한다.
                            cF.ui.unblockUI();

                            /* modal history pop */
                            ModalHistory.reset();
                        });
                }, "block");
            });
        },

        /**
         * 상세 모달 호출
         * @param {string|number} postNo - 글 번호.
         */
        dtlModal: function(postNo: string|number): void {
            if (isNaN(Number(postNo))) return;

            // 기존에 열린 모달이 있으면 닫기
            const openModals: NodeList = document.querySelectorAll('.modal.show'); // 열린 모달을 찾기
            openModals.forEach((modal: Node): void => {
                $(modal).modal('hide');  // 각각의 모달을 닫기
            });

            const self = this;
            const func: string = arguments.callee.name; // 현재 실행 중인 함수 참조
            const args: any[] = Array.from(arguments); // 함수 인자 배열로 받기

            const url: string = Url.JRNL_DREAM_DTL_AJAX;
            const ajaxData: Record<string, any> = { "postNo" : postNo };
            cF.ajax.get(url, ajaxData, function(res: AjaxResponse): void {
                if (!res.rslt) {
                    if (cF.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                    return;
                }
                const rsltObj: Record<string, any> = res.rsltObj;
                /* show modal */
                cF.handlebars.modal(rsltObj, "jrnl_dream_dtl");

                /* modal history push */
                ModalHistory.push(self, func, args);
            });
        },

        /**
         * 수정 모달 호출
         * @param {string|number} postNo - 글 번호.
         */
        mdfModal: function(postNo: string|number): void {
            if (isNaN(Number(postNo))) return;

            // 기존에 열린 모달이 있으면 닫기
            const openModals: NodeList = document.querySelectorAll('.modal.show'); // 열린 모달을 찾기
            openModals.forEach((modal: Node): void => {
                $(modal).modal('hide');  // 각각의 모달을 닫기
            });

            const self = this;
            const func: string = arguments.callee.name; // 현재 실행 중인 함수 참조
            const args: any[] = Array.from(arguments); // 함수 인자 배열로 받기

            const url: string = Url.JRNL_DREAM_DTL_AJAX;
            const ajaxData: Record<string, any> = { "postNo" : postNo };
            cF.ajax.get(url, ajaxData, function(res: AjaxResponse): void {
                if (!res.rslt) {
                    if (cF.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                    return;
                }
                const { rsltObj } = res;
                /* initialize form. */
                dF.JrnlDream.initForm(rsltObj);

                /* modal history push */
                ModalHistory.push(self, func, args);
            });
        },

        /**
         * 삭제 (Ajax)
         * @param {string|number} postNo - 글 번호.
         */
        delAjax: function(postNo: string|number): void {
            if (isNaN(Number(postNo))) return;

            Swal.fire({
                text: Message.get("view.cnfm.del"),
                showCancelButton: true,
            }).then(function(result: SwalResult): void {
                if (!result.value) return;

                const url: string = Url.JRNL_DREAM_DEL_AJAX;
                const ajaxData: Record<string, any> = { "postNo": postNo };
                cF.$ajax.post(url, ajaxData, function(res: AjaxResponse): void {
                    Swal.fire({ text: res.message })
                        .then(function(): void {
                            if (!res.rslt) return;

                            const isCalendar: boolean = Page?.calendar !== undefined;
                            if (isCalendar) {
                                Page.refreshEventList();
                                dF.JrnlDreamTag.listAjax();     // 태그 refresh
                            } else {
                                if (dF.JrnlDream.inKeywordSearchMode) {
                                    dF.JrnlDream.keywordListAjax();
                                } else {
                                    dF.JrnlDay.yyMnthListAjax();
                                    dF.JrnlDreamTag.listAjax();     // 태그 refresh
                                }
                            }

                            /* modal history pop */
                            ModalHistory.reset();
                        });
                }, "block");
            });
        },
    }
})();