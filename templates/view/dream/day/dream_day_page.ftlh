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
</script>

<!--begin::Card-->
<div class="card post">
    <form name="procForm" id="procForm">
        <input type="hidden" name="dreamDayNo" id="dreamDayNo" />
    </form>
    <!--begin::Card header 검색+헤더 UI -->
    <#include "/view/dream/day/dream_day_page_header.ftlh">

    <div class="card-body p-9" id="dream_day_list_div">
        <!-- handlebars template goes in here -->
    </div>
</div>
<!--end::Card-->

<#include "/view/dream/day/dream_day_list_template.ftlh">
<#include "/view/dream/day/dream_day_reg_modal.ftlh">
<#include "/view/dream/day/dream_day_reg_template.ftlh">
<#include "/view/dream/piece/dream_piece_reg_modal.ftlh">
<#include "/view/dream/piece/dream_piece_reg_template.ftlh">

</@layout.layout>