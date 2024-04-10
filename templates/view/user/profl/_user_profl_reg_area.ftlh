<#--
  -- 사용자 관리 > 계정 및 권한 관리 > 등록 > 사용자(직원) 정보 추가 Template
  -- @included_in : user_reg_form
  -->
<script>
    const UserInfo = function() {
        return {
            init: function () {
                <#if (user.userProfl.itemList)?has_content>
                    UserInfo.addUserInfoItemMdfList();      // 기존 항목에 대하여 행 쭈루룩 추가
                </#if>

                commons.validate.cttpc("#cttpc");              // 전화번호 형식 유효성 검사
                // 이메일 도메인 select시 자동입력
                $("#emailDomainSelect").on("change", function() {
                    $("#emailDomain").val($(this).val());
                });

                <#if isMdf>
                    // 수정화면:: 이메일 selectbox 세팅
                    let emailDomain = "${(user.userProfl.emailDomain)!}";
                    $("#emailDomainSelect option").each(function(idx, elmt) {
                        if (elmt.value === emailDomain) $(elmt).attr("selected", "selected");
                    });

                    <#if (user.userProfl.jobTitleCd)! == "STAFF">
                        $("#apntcYnDiv").show();"이름은 필수 항목입니다."
                    </#if>
                </#if>

                // 직급 변경시 '사원'일 때만 수습여부 영역 출력 + 수습여부 초기화
                $("#jobTitleCd").on("change", function() {
                    if ($("#jobTitleCd").val() === "STAFF") {
                        $("#apntcYnDiv").show();
                    } else {
                        $("#apntcYnDiv").hide();
                    }
                });
                // 수습여부 클릭시 글씨 변경
                commons.util.chckboxLabel("apntcYn", "수습//해당없음", "blue//gray");
                // 퇴사여부 클릭시 글씨 변경
                commons.util.chckboxLabel("retireYn", "퇴사//해당없음", "red//gray", function() {$(".retireDtDiv").show();}, function() {$(".retireDtDiv").hide();});

                commons.datepicker.singleDatePicker("#ecnyDt", "yyyy-MM-DD");
                $("#ecnyDt").val("${(user.userProfl.ecnyDt)!}");
                commons.datepicker.singleDatePicker("#retireDt", "yyyy-MM-DD");
                $("#retireDt").val("${(user.userProfl.retireDt)!}");
                $.validator.addMethod("dupChck", function(value) {
                    return (value === "Y");
                });
            },
            /** 직원정보 창 토글 */
            enableUserProfl: function() {
                // 영역 표시
                commons.util.handlebarsTemplate({}, "user_profl");
                $('[data-bs-toggle="tooltip"]').tooltip();
                // 버튼 교체 :: 툴팁 작동 유지 위해 속성만 교체
                let $btn = $("#userProflRegBtn");
                $btn.removeClass("btn-primary").addClass("btn-danger");
                $btn.text("프로필 정보 삭제-");
                $btn.attr("onclick", "UserInfo.disableUserProfl();");
                $btn.attr("title", "사용자 프로필 정보를&#10;삭제합니다.");
                // 음력여부 클릭시 글씨 변경
                commons.util.chckboxLabel("lunarYn", "음력//양력", "blue//gray");
                commons.datepicker.singleDatePicker("#brthdy", "yyyy-MM-DD");
            },
            /** 직원정보 창 토글 */
            disableUserProfl: function() {
                // 영역 삭제
                $("#user_profl_div").empty();
                // 버튼 교체 :: 툴팁 작동 유지 위해 속성만 교체
                let $btn = $("#userProflRegBtn");
                $btn.removeClass("btn-danger").addClass("btn-primary");
                $btn.text("프로필 정보 추가+");
                $btn.attr("title", "사용자 프로필 정보를&#10;추가합니다.");
                $btn.attr("onclick", "UserInfo.enableUserProfl();");
                $('[data-bs-toggle="tooltip"]').tooltip();
            },
            /**
             * 사용자 정보 추가항목 영역 새로 추가
             */
            addUserInfoItem: function () {
                let reqstItemIdx = commons.util.getReqstItemIdx("span", "id^=userProflItem", "userProflItem");		// elmt, selector, elmtId
                let tableTmpl = "<div class='row my-5 my-lg-0' id='itemContainer"+reqstItemIdx+"'>" + $("#userProflItemTemplate").html() + "</div>";
                let str = tableTmpl.replace(/__INDEX__/g, reqstItemIdx);
                $("#userItemListDiv").append(str);
            },
            /**
             * 사용자 정보 추가항목 영역 기존 정보 목록 추가
             */
            addUserInfoItemMdfList: function() {
                <#if user.userProfl?? && user.userProfl.itemList?has_content>
                    let reqstItemIdx, tableTmpl, str;
                    <#list user.userProfl.itemList![] as item>
                        reqstItemIdx = commons.util.getReqstItemIdx("span", "id^=userProflItem", "userProflItem");		// elmt, selector, elmtId

                        tableTmpl = "<div class='row my-5 my-lg-0' id='itemContainer"+reqstItemIdx+"'>" + $("#userProflItemTemplate").html() + "</div>";

                        str = tableTmpl.replace(/__INDEX__/g, reqstItemIdx);

                        $("#userItemListDiv").append(str);
                        $("#userProflItemNo"+reqstItemIdx).val("${item.userProflItemNo}");
                        $("#itemNm"+reqstItemIdx).val(`${item.itemNm}`);
                        $("#itemCn"+reqstItemIdx).val(`${item.itemCn}`);
                        $("#itemDc"+reqstItemIdx).val(`${item.itemDc}`);
                    </#list>
                </#if>
            },
            /** 추가항목 영역 삭제 */
            delUserInfoItemSpan: function(idx) {
                if ($("#userIntoItemId"+idx).val() === "") {
                    $("div#itemContainer"+idx).remove();
                } else {
                    $("#delYn"+idx).val("Y");
                    $("div#itemContainer"+idx).hide();
                }
            }
        }
    }();

    $(document).ready(function() {
        UserInfo.init();
    });
</script>
<!-- include:: Handlebars template -->
<#include "/view/user/profl/_user_profl_reg_template.hbs">
