/**
 * modal_history.ts
 * 모달 이력 스크립트 모듈
 *
 * @author nichefish
 */
const ModalHistory: Page = (function(): Page {
    return {
        stack: [],

        /**
         * 모달 히스토리 초기화
         */
        init: function(): void {
            ModalHistory.reset();
        },

        /**
         * 현재 모달 정보를 stack에 추가합니다.
         * @param {Module} module
         * @param {string} methodNm
         * @param {Array} args
         */
        push: function(module: Module, methodNm: string, args: any[]): void {
            const currentModal = {
                module: module,       // 호출한 모듈 객체
                methodNm: methodNm,     // 호출한 메소드명
                args: args                  // 메소드에 전달된 인자들
            };
            
            console.log("modal history:: push.");
            ModalHistory.stack.push(currentModal);  // modalHistory에 모달 정보 추가
        },

        /**
         * 가장 최근에 열린 모달을 stack에서 제거합니다.
         *
         * @returns {Object} 스택에서 제거된 모달 정보
         */
        pop: function(): any {
            console.log("modal history:: pop.");
            return ModalHistory.stack.pop();
        },

        /**
         * 현재 열려 있는 가장 최근 모달을 확인합니다.
         *
         * @returns {Object} 가장 최근에 열린 모달 정보
         */
        peek: function(): any {
            const maxIdx: number = ModalHistory.stack.length - 1;
            return ModalHistory.stack[maxIdx];
        },

        /**
         * 현재 모달을 닫고 이전 모달로 돌아갑니다.
         * @see "templates/view/global/_modal_element.ftlh"
         */
        prev: function (): void {
            // 기존에 열린 모달이 있으면 닫기
            const openModals: NodeList = document.querySelectorAll('.modal.show'); // 열린 모달을 찾기
            openModals.forEach((modal: Node): void => {
                $(modal).modal('hide');  // 각각의 모달을 닫기
            });

            // 모달 close시 현재 모달을 자동으로 pop 처리한다.
            // 스택에 이전 모달이 있는지 확인
            const prevModal = this.peek();
            if (!prevModal) {
                console.log("modal history:: No previous modal to return to.");
                return;
            }

            console.log("modal history:: Returning to previous modal:", prevModal.methodNm);

            // 이전 모달을 여는 로직
            if (prevModal.module && typeof prevModal.module[prevModal.methodNm] === 'function') {
                console.log("modal history:: previous.");
                prevModal.module[prevModal.methodNm](...prevModal.args);
            } else {
                console.log("modal history:: Failed to return to previous modal: Method not found.");
            }
        },

        /**
         * 모달 히스토리 스택을 초기화합니다.
         */
        reset: function(): void {
            console.log("modal history:: reset.");

            // 기존에 열린 모달이 있으면 닫기
            const openModals: NodeList = document.querySelectorAll('.modal.show'); // 열린 모달을 찾기
            openModals.forEach((modal: Node): void => {
                $(modal).modal('hide');  // 각각의 모달을 닫기
            });

            $('.modal-backdrop').remove(); // 모달이 닫힐 때 백드롭 제거
            $('body').removeClass('modal-open');  // body에서 modal-open 클래스 제거
            ModalHistory.stack = [];
        },
    }
})();