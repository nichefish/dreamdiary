<#--
  -- 사이트 관리 화면
  -->
<#import "/layout/layout.ftlh" as layout >
<#import "/spring.ftl" as spring />


<script defer="defer" src="/static/node_modules/sass/sass.js"></script>

<@layout.layout>

<script>
    const Page = function() {
        return {
            init: function () {
                //
            },
            /** 등록 화면 호출 */
            fnRegForm: function() {
                commons.util.handlebarsTemplate({}, "dream_info_reg", "show");
                commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                commons.validate.replaceBlankRegex("#dreanInfoRegForm #clCd");
                commons.validate.toUpperCase("dreanInfoRegForm #clCd");
            },
        }
    }();
</script>

<!--begin::Card-->
<div class="card post">
    <form name="procForm" id="procForm">
        <input type="hidden" name="userNo" id="userNo" />
    </form>
    <!--begin::Card header 검색+헤더 UI -->
    <#include "/view/dream/dream_list_header.ftlh">


</div>
<!--end::Card-->

<#include "/view/dream/info/dream_info_reg_modal.ftlh">
<#include "/view/dream/info/dream_info_reg_template.ftlh">

</@layout.layout>