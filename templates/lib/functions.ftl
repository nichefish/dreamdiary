<#--
 * Freemarker 커스텀 함수들을 정의한다.
 * @author: nichefish
 -->

<#--
 * selected
 * 두 값이 같으면 selected를 리턴한다. (<option> tag의 selected 에 사용)
 -->
<#function selected var1="" var2="">
	<#if !var1?? || !var2??>
		<#return "">
	</#if>
	<#if var1?string == var2?string>
		<#return "selected">
	</#if>
	<#return ""/>
</#function>

<#--
 * checked: 두 값이 같으면 checked를 리턴한다.
 -->
<#function checked var1="" var2="">
	<#if !var1?? || !var2??>
		<#return "">
	</#if>
	<#if var1?string == var2?string>
		<#return "checked">
	</#if>
	<#return ""/>
</#function>

<#--
 * closerNm: 전달받은 javascript 함수에서 네임스페이스를 추출한다.
 -->
<#function closerNm func>
	<#local parts = func?split(".")>
	<#local beforeLastDot = "">
	<#list parts as part>
		<#if part_has_next>
			<#local beforeLastDot = beforeLastDot + ((beforeLastDot != "")?then(beforeLastDot + ".", "") + part)>
		</#if>
	</#list>
	<#return beforeLastDot>
</#function>

