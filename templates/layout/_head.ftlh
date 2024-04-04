<#--
 -- 레이아웃 > head
 -- metronic v8.2.5
 -- @included_in : layout
 -->
<#setting number_format="c">

<head>
    <title></title>
    <base href="${SiteUrl.DOMAIN!}">
    <meta name="description" content="
            The most advanced Bootstrap 5 Admin Theme with 40 unique prebuilt layouts on Themeforest trusted by 100,000 beginners and professionals. Multi-demo,
            Dark Mode, RTL support and complete React, Angular, Vue, Asp.Net Core, Rails, Spring, Blazor, Django, Express.js, Node.js, Flask, Symfony & Laravel versions.
            Grab your copy now and get life-time updates for free.
        "/>
    <meta name="keywords" content="
            metronic, bootstrap, bootstrap 5, angular, VueJs, React, Asp.Net Core, Rails, Spring, Blazor, Django, Express.js,
            Node.js, Flask, Symfony & Laravel starter kits, admin themes, web design, figma, web development, free templates,
            free admin themes, bootstrap theme, bootstrap template, bootstrap dashboard, bootstrap dak mode, bootstrap button,
            bootstrap datepicker, bootstrap timepicker, fullcalendar, datatables, flaticon
        "/>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <meta charset="utf-8" />
    <meta property="og:locale" content="ko_KR" />
    <meta property="og:type" content="article" />
    <meta property="og:title" content="Metronic - The World's #1 Selling Bootstrap Admin Template by KeenThemes" />
    <meta property="og:url" content="https://keenthemes.com/metronic"/>
    <meta property="og:site_name" content="Metronic by Keenthemes" />
    <link rel="shortcut icon" href="/favicon.ico" />

<#--    <!--Fonts(used by all pages) &ndash;&gt;
    <link type="text/css" rel="stylesheet" href="/css/font.css?family=Poppins:300,400,500,600,700" />-->

    <!--Metronic:: Gloabl Css Bundle(used by all pages) -->
    <link type="text/css" href="/metronic/assets/plugins/global/plugins.bundle.css?${releaseDate!}" rel="stylesheet" type="text/css"/>
    <link type="text/css" href="/metronic/assets/css/style.bundle.css?${releaseDate!}" rel="stylesheet" type="text/css"/>
    <!--Fonts:: Gloabl Css Bundle(used by all pages) -->
    <link type="text/css" rel="stylesheet" href="/lib/fontawesome/css/fontawesome.css?${releaseDate!}" />
    <link type="text/css" rel="stylesheet" href="/lib/bootstrap-icon/bootstrap-icons.css?${releaseDate!}" />


    <!--My:: Gloabl Css Bundle(used by all pages) -->
    <link type="text/css" rel="stylesheet" href="/css/commons.css?${releaseDate!}" />
    <link type="text/css" rel="stylesheet" href="/css/commons-respns.css?${releaseDate!}" />
    <link type="text/css" rel="stylesheet" href="/css/font.css?${releaseDate!}" />

    <!--Javascript:: Global Javascript Bundle(used by all pages)-->
    <script type="text/javascript" src="/js/handlebars.min.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/moment.min.js?${releaseDate!}"></script>
    <!--Metronic:: Global Javascript Bundle(used by all pages)-->
    <script src="/metronic/assets/plugins/global/plugins.bundle.js?${releaseDate!}"></script>

    <script type="text/javascript" src="/js/jquery.cookie.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/jquery.blockUI.min.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/jquery.form.min.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/jquery.validate.min.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/additional-methods.js?${releaseDate!}"></script>

    <!--begin::Fonts(mandatory for all pages)-->
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />

    <script src="/metronic/assets/js/scripts.bundle.js?${releaseDate!}"></script>

    <!--Custom Javascript Bundle(used by all pages)-->
    <script type="text/javascript" src="/js/commons/commons.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/commons/commons-validate.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/commons/commons-date.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/commons/commons-datepicker.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/commons/commons-helpers.js?${releaseDate!}"></script>
</head>

<!--
-->