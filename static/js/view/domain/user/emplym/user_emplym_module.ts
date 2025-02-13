/**
 * user_emplym_module.ts
 * 사용자 인사정보 스크립트 모듈
 *
 * @author nichefish
 */
if (typeof dF === 'undefined') { var dF = {} as any; }
dF.UserEmplym = (function(): dfModule {
    return {
        initialized: false,

        /**
         * initializes module.
         */
        init: function(): void {
            if (dF.UserEmplym.initialized) return;

            dF.UserEmplym.initialized = true;
            console.log("'dF.UserEmplym' module initialized.");
        },

        /** 직원정보 창 토글 */
        enableUserEmplym: function(): void {
            // 영역 표시
            cF.handlebars.template({}, "user_emplym");
            // 버튼 교체 :: 툴팁 작동 유지 위해 속성만 교체
            const $btn = $("#userEmplymBtn");
            $btn.removeClass("btn-primary").addClass("btn-danger");
            $btn.text("직원 인사정보 삭제-");
            $btn.attr("onclick", "dF.UserEmplym.disableUserEmplym();");
            $btn.attr("title", "직원 인사정보를&#10;삭제합니다.");

            // 전화번호 형식 유효성 검사
            cF.validate.cttpc("#emplymCttpc");
            // 이메일 도메인 select시 자동입력
            $("#emplymEmailDomainSelect").on("change", function(): void {
                $("#emplymEmailDomain").val($(this).val());
            });

            // 직급 변경시 '사원'일 때만 수습여부 영역 출력 + 수습여부 초기화
            $("#rankCd").on("change", function(): void {
                if ($("#rankCd").val() === "STAFF") {
                    $("#apntcYnDiv").show();
                } else {
                    $("#apntcYnDiv").hide();
                }
            });
            // 수습여부 클릭시 글씨 변경
            cF.ui.chckboxLabel("apntcYn", "수습//해당없음", "blue//gray");
            // 퇴사여부 클릭시 글씨 변경
            cF.ui.chckboxLabel("retireYn", "퇴사//해당없음", "red//gray", function(): void {$(".retireDtDiv").show();}, function(): void {$(".retireDtDiv").hide();});

            cF.datepicker.singleDatePicker("#ecnyDt", "yyyy-MM-DD", $("#ecnyDt").val());
            cF.datepicker.singleDatePicker("#retireDt", "yyyy-MM-DD", $("#retireDt").val());
        },
        /** 직원정보 창 토글 */
        disableUserEmplym: function(): void {
            // 영역 삭제
            $("#user_emplym_div").empty();
            // 버튼 교체 :: 툴팁 작동 유지 위해 속성만 교체
            const $btn = $("#userEmplymBtn");
            $btn.removeClass("btn-danger").addClass("btn-primary");
            $btn.text("직원 인사정보 추가+");
            $btn.attr("title", "직원 인사정보를 &#10;추가합니다.");
            $btn.attr("onclick", "dF.UserEmplym.enableUserEmplym();");
        },
    }
})();