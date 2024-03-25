<#--
  -- 사이트 관리 화면
  -->
<#import "/layout/layout.ftlh" as layout >
<#import "/spring.ftl" as spring />

<@layout.layout>

<script>
    const Page = function() {
        return {
            init: function () {
                //
            },
        }
    }();
    const Dream = function() {
        return {
            init: function () {
                //
            },
            /** 등록 화면 호출 */
            regModal: function() {
                commons.util.handlebarsTemplate({}, "dream_day_reg", "show");
                // commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                commons.datepicker.singleDatePicker("#dreamtDt", "yyyy-MM-DD", commons.date.getCurrDateStr("yyyy-MM-dd"));         // datepicker 날짜 검색 init
                commons.validate.replaceBlankRegex("#dreamDayRegForm #clCd");
                commons.validate.toUpperCase("dreamDayRegForm #clCd");
            },
            /** 등록 */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.DREAM_DAY_REG_AJAX!}";
                    let ajaxData = new FormData($("#dreamDayRegForm")[0]);
                    commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();;
                            });
                    });
                });
            },
        }
    }();
</script>

<!--begin::Card-->
<div class="card post">
    <form name="procForm" id="procForm">
        <input type="hidden" name="dreamDayNo" id="dreamDayNo" />
    </form>
    <!--begin::Card header 검색+헤더 UI -->
    <#include "/view/dream/day/dream_day_page_header.ftlh">


</div>
<!--end::Card-->

<#include "/view/dream/day/dream_day_reg_modal.ftlh">
<#include "/view/dream/day/dream_day_reg_template.ftlh">

</@layout.layout>