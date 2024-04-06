<!--
 -- 레이아웃 > 헤더 (모바일)
 -- @included_in : layout
 -->

<!--begin::Web logo-->
<div class="d-flex align-items-center flex-grow-1 flex-lg-grow-0" style="width:235px;">
	<div class="d-flex align-items-center w-100 border-end h-50 border-gray-400">
		<a href="${SiteUrl.DOMAIN!}">
			<img alt="Logo" src="/media/img/logo/logo_racoon.jpg" class="h-30px"/>
			<span class="ps-3 fs-6 text-underline-dotted" style="letter-spacing: 0.2px">${SiteUrl.DOMAIN!}</span>
		</a>
	</div>
</div>
<!--end::Web logo-->
<!--begin::Mobile logo-->
<div class="d-flex align-items-center flex-grow-1 flex-lg-grow-0">
	<a href="${SiteUrl.DOMAIN!}" class="d-lg-none">
		<img alt="Logo" src="/media/img/logo/logo_racoon.jpg" class="h-30px"/>
	</a>
</div>
<!--end::Mobile logo-->