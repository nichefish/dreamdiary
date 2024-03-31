<#--
  -- 레이아웃 > head
  -- @included in : layout
  -->
<#setting number_format="c">
<#assign releaseDate = releaseDate!DateUtils.getCurrDateStr(DateUtils.PTN_PDATE)>

<head>
    <title></title>
    <base href="${SiteUrl.DOMAIN!}">
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <meta charset="utf-8" />
    <meta property="og:locale" content="ko_KR" />
    <meta property="og:type" content="article" />
    <link rel="shortcut icon" href="/favicon.ico" />

    <!--Fonts(used by all pages) -->
    <link type="text/css" rel="stylesheet" href="/css/font.css?family=Poppins:300,400,500,600,700" />
    <!--Gloabl Css Bundle(used by all pages) -->
    <link type="text/css" rel="stylesheet" href="/metronic/assets/css/style.bundle.css?${releaseDate!}" />
    <link type="text/css" rel="stylesheet" href="/metronic/assets/plugins/global/plugins.bundle.css?${releaseDate!}" />
    <link type="text/css" rel="stylesheet" href="/lib/fontawesome/css/fontawesome.css?${releaseDate!}" />
    <link type="text/css" rel="stylesheet" href="/lib/bootstrap-icon/bootstrap-icons.css?${releaseDate!}" />

    <link type="text/css" rel="stylesheet" href="/lib/bootstrap/css/bootstrap.min.css?${releaseDate!}" />
    <link type="text/css" rel="stylesheet" href="/metronic/assets/css/style.bundle.css?${releaseDate!}" />
    <link type="text/css" rel="stylesheet" href="/css/commons.css?${releaseDate!}" />
    <link type="text/css" rel="stylesheet" href="/css/commons-respns.css?${releaseDate!}" />
    <!--Global Javascript Bundle(used by all pages)-->
    <script type="text/javascript" src="/js/jquery-3.6.0.min.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/jquery.form.min.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/jquery.cookie.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/handlebars.min.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/moment.min.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/metronic/assets/js/scripts.bundle.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/metronic/assets/plugins/global/plugins.bundle.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/jquery.validate.min.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/additional-methods.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/jquery.blockUI.min.js?${releaseDate!}"></script>
    <!--Custom Javascript Bundle(used by all pages)-->
    <script type="text/javascript" src="/js/commons/commons.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/commons/commons-validate.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/commons/commons-date.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/commons/commons-datepicker.js?${releaseDate!}"></script>
    <script type="text/javascript" src="/js/commons/commons-helpers.js?${releaseDate!}"></script>
</head>

