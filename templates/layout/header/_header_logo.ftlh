<!--
 * 레이아웃 > 헤더 > 모바일
 * @included_in : layout/header/_header_base.ftlh
 -->

<!--begin::Web logo-->
<div class="d-flex align-items-center flex-grow-1 flex-lg-grow-0" style="width:235px;">
	<div class="d-flex align-items-center w-100 border-end h-50 border-gray-400">
		<a href="${Url.MAIN!}"
		   data-bs-toggle="tooltip" data-bs-placement="bottom" data-bs-dismiss="click" title="${Url.DOMAIN!}">
			<img alt="Logo" src="/media/img/logo/logo_racoon.jpg" class="h-30px"/>
			<span class="ps-3 fs-6 text-underline-dotted" style="letter-spacing: 0.2px">
				${Url.DOMAIN!}
			</span>
		</a>
	</div>
</div>
<!--end::Web logo-->
<!--begin::Mobile logo-->
<div class="d-flex align-items-center flex-grow-1 flex-lg-grow-0">
	<a href="${Url.MAIN!}" class="d-lg-none">
		<img alt="Logo" src="/media/img/logo/logo_racoon.jpg" class="h-30px"/>
	</a>
</div>
<!--end::Mobile logo-->