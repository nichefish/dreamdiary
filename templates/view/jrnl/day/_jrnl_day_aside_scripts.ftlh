<#--
 * 저널 일자(jrnl_day) ASIDE : 스크립트 모듈 분리
 -->

<script>
    const JrnlDayAside = (function() {
        /** 쿠키 기본 옵션*/
        const cookieOptions = {
            path: "/jrnl/day/",
            expires: commons.date.getCurrDateAddDay(36135)
        };

        return {
            /**
             * JrnlDayAside 객체 초기화
             */
            init: function() {
                const pinYyCookie = $.cookie("pin_yy");
                if (pinYyCookie !== undefined) {
                    $("#jrnl_aside #pinYy").text(pinYyCookie);
                }
                const pinMnthCookie = $.cookie("pin_mnth");
                if (pinMnthCookie !== undefined) {
                    $("#jrnl_aside #pinMnth").text(pinMnthCookie);
                }
            },

            /**
             * 오늘 날짜로 가기
             */
            today: function() {
                const todayYy = commons.date.getCurrYyStr();
                const todayMnth = commons.date.getCurrMnthStr();
                $("#jrnl_aside #yy").val(todayYy);
                $("#jrnl_aside #mnth").val(todayMnth);
                JrnlDayAside.mnth();
                // 오늘이 제일 위에 오게 하기 위해 내림차순 정렬로 변경
                JrnlDayAside.sort("DESC");
            },

            /**
             * 년도 바꾸기
             */
            yy: function() {
                commons.util.handlebarsTemplate(null, "jrnl_day_list");
                commons.util.handlebarsTemplate([], "jrnl_day_tag_list");
                commons.util.handlebarsTemplate([], "jrnl_diary_tag_list");
                commons.util.handlebarsTemplate([], "jrnl_dream_tag_list");
                JrnlDream.inKeywordSearchMode = false;
                if ($("#jrnl_aside #yy").val() === "2010") {
                    $("#jrnl_aside #mnth").val("99");
                    JrnlDayAside.mnth();
                } else {
                    $("#jrnl_aside #mnth").val("");
                }
            },

            /**
             * 월 바꾸기
             */
            mnth: function() {
                // 쿠키 설정하기
                $.cookie("jrnl_yy", $("#jrnl_aside #yy").val(), cookieOptions);
                if ($("#jrnl_aside #mnth").val() === "") return;
                $.cookie("jrnl_mnth", $("#jrnl_aside #mnth").val(), cookieOptions);
                $("#jrnl_aside #dreamKeyword").val("");
                // 목록 조회
                JrnlDay.yyMnthListAjax();
                JrnlDayTag.listAjax();
                JrnlDiaryTag.listAjax();
                JrnlDreamTag.listAjax();
                //
                JrnlDream.inKeywordSearchMode = false;
                // 페이지 상단으로 이동
                commons.util.toPageTop();
            },

            /**
             * 현재 년/월을 저장한다.
             */
            pinpoint: function() {
                const pinYy = $("#jrnl_aside #yy").val();
                const pinMnth = $("#jrnl_aside #mnth").val();
                $.cookie("pin_yy", pinYy, cookieOptions);
                $.cookie("pin_mnth", pinMnth, cookieOptions);
                $("#jrnl_aside #pinYy").text(pinYy);
                $("#jrnl_aside #pinMnth").text(pinMnth);
            },

            /**
             * 저장된 저널 년/월로 돌아가기
             */
            turnback: function() {
                const pinYyCookie = $.cookie("pin_yy");
                if (pinYyCookie !== undefined) $("#jrnl_aside #yy").val(pinYyCookie);
                const pinMnthCookie = $.cookie("pin_mnth");
                if (pinMnthCookie !== undefined) $("#jrnl_aside #mnth").val(pinMnthCookie);
                JrnlDayAside.mnth();
            },

            /**
             * 저널 일자 정렬
             * @param {string} [toBe] - 정렬 방향 ("ASC" 또는 "DESC").
             */
            sort: function(toBe) {
                const sortElement = document.querySelector("#jrnl_aside #sort");
                const asIs = sortElement.value;
                if (toBe === undefined) toBe = (asIs !== "ASC") ? "ASC" : "DESC";
                // 쿠키에 정렬 정보 저장
                $.cookie("jrnl_day_sort", toBe, cookieOptions);
                // 정렬 값 설정
                $("#jrnl_aside #sort").val(toBe);

                // 정렬 아이콘 변경
                if (toBe === "DESC") {
                    $("#jrnl_aside #sortIcon").removeClass("bi-sort-numeric-down").addClass("bi-sort-numeric-up-alt");
                } else {
                    $("#jrnl_aside #sortIcon").removeClass("bi-sort-numeric-up-alt").addClass("bi-sort-numeric-down");
                }

                // 정렬 수행
                const container = document.querySelector('#jrnl_day_list_div'); // 모든 저널 일자를 포함하는 컨테이너
                const days = Array.from(container.querySelectorAll('.jrnl-day')); // 모든 'jrnl-day' 요소를 배열로 변환
                days.sort((a, b) => {
                    const dateA = new Date(a.querySelector('.jrnl-day-header .col-1').textContent.trim());
                    const dateB = new Date(b.querySelector('.jrnl-day-header .col-1').textContent.trim());
                    return (toBe === "ASC") ? dateA - dateB : dateB - dateA;
                });

                // 컨테이너에서 모든 요소를 제거
                while (container.firstChild) {
                    container.removeChild(container.firstChild);
                }

                // 정렬된 요소를 다시 컨테이너에 추가
                days.forEach(day => {
                    container.appendChild(day);
                });
            },
        }
    })();
</script>