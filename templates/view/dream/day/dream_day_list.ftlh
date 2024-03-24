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
            /** 등록 화면 호출 */
            regModal: function() {
                commons.util.handlebarsTemplate({}, "dream_day_reg", "show");
                // commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                commons.validate.replaceBlankRegex("#dreamDayRegForm #clCd");
                commons.validate.toUpperCase("dreamDayRegForm #clCd");
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
    <#include "/view/dream/day/dream_day_list_header.ftlh">


</div>
<!--end::Card-->

<#include "/view/dream/day/dream_day_reg_modal.ftlh">
<#include "/view/dream/day/dream_day_reg_template.ftlh">

</@layout.layout>