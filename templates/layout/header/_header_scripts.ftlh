<!--
 * 레이아웃 > 헤더 스크립트
 * @included_in : _header_base.ftlh
 -->

<script>
    /** 레이아웃 */
    const Layout = (function() {
        return {
            init: function() {
                // 모든 버튼에 딜레이 기능 추가
                $("button, .btn, .badge").on("click", function() {
                    commons.util.delayBtn(this);
                });
                // 개별 input 유효성검사
                commons.validate.onlyNum(".number");

                // 사용자/관리자 전환
                $("#userMode").on("change", function() {
                    let mode = $(this).val();
                    if (mode === "${Constant.AUTH_USER!}") location.replace("${Url.MAIN!}");
                    if (mode === "${Constant.AUTH_MNGR!}") location.replace("${Url.ADMIN_MAIN!}");
                });

                /** 현재 접속중인 메뉴 활성화 로직 */
                const siteAcsInfoExists = "${topMenuNo!}" !== "" && "${siteMenuNo!}" !== "";
                if (siteAcsInfoExists) {
                    $("#${topMenuNo!}").addClass("hover");
                    $("#${siteMenuNo!}").addClass("hover");
                }
            },
            /** 사이트맵 모달 팝업 호출 */
            siteMapModal: function() {
                $("#sitemap_modal").modal("show");
            },
            /** 내 정보 상세 페이지 이동 */
            myInfoDtl: function () {
                location.replace("${Url.USER_MY_DTL!}");
            },
            /** 로그아웃 처림 */
            lgout: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.lgout"/>,
                    showCancelButton: true,
                }).then(function (result) {
                    if (!result.value) return;
                    location.replace("${Url.AUTH_LGOUT!}");
                });
            }
        }
    })();
    $(document).ready(function() {
        Layout.init();
    });
</script>
