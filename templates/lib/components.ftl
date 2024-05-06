<#--
 * Freemarker 매크로 파일을 직접 작성 또는 include하여 모아둔다.
 * (주로 컴포넌트들)
 * (이후 현재 파일을 auto-import하여 전체 매크로를 사용 가능하도록)
 * @author: nichefish
 -->

<!--include::page elements-->
<#include "/view/_component/page_area/_page_elements.ftlh">

<!-- list-table items -->
<#include "/view/_component/list_area/_list_elements.ftlh">
<!-- buttons -->
<#include "/view/_component/page_area/_page_buttons.ftlh">

<!-- modal_elements -->
<#include "/view/_component/modal/_modal_elements.ftlh">

<!-- header_elements -->
<#include "/view/_component/_header_elements.ftlh">

<!-- in progress... -->
<#include "/view/_component/page_area/_post_module.ftlh">

<!-- in progress... -->
<#include "/view/_component/page_area/_page_clsf_module.ftlh">
