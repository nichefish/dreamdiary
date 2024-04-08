<#--
  -- 활동 로그 목록 화면
  -->
<#import "/layout/layout.ftlh" as layout >

<script>
    $(document).ready(function() {
        Flsys.init("modal");
    });
</script>
<div class="modal fade" id="flsys_home_modal" tabindex="-1" role="dialog" aria-labelledby="flsysMetaRegModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="flsysMetaRegModalLongTitle">[메타 정보 저장]</h5>
                <div class="btn btn-sm btn-icon btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <!--begin::Card header 검색+헤더 UI -->
                    <#include "/view/cmm/flsys/flsys_home_header.ftlh">
                    <!--begin::Tab -->
                    <ul class="nav nav-tabs nav-tabs-line ps-5 my-5">
                        <li class="nav-item">
                            <a class="nav-link px-6" data-bs-toggle="tab" href="#kt_tab_pane_1" onclick="Flsys.activateTab(this); Flsys.flsysListAjax('${HOME_FLSYS!}');">
                                <span class="nav-icon">
                                    <i class="bi bi-sort-down-alt"></i>
                                </span>
                                <span class="nav-text ps-1">FLSYS</span>
                            </a>
                        </li>
                    </ul>
                    <!--end: Tab-->
                    <form class="form" name="flsysMetaRegForm" id="flsysMetaRegForm">
                        <!--begin::Table-->
                        <div class="d-flex align-items-center my-4">
                            <i class="bi bi-folder2-open text-primary"></i>
                            <div class="text-muted fw-bold ms-2" id="absolute_path_div">
                                <!-- absolutePath goes in here -->
                            </div>
                            <span class="badge badge-light-primary cursor-pointer ms-6 px-2"
                                  onclick="Flsys.flsysRefreshAjax();"
                                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="현재 디렉토리 내용을&#10;새로고침합니다.">
                                <i class="bi bi-arrow-clockwise"></i>
                            </span>
                        </div>
                        <div id="flsys_list_div">
                            <!-- Handlebars Template goes in here -->
                        </div>

                        <div id="flsys_jstree_div">
                            <!-- 조직도 (jstree) 동적 생성 -->
                        </div>
                        <!--end::Table-->
                    </form>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary"
                        onclick="Swal.fire('준비중입니다.'); return false;"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.save"/>&#10;(준비중입니다.)">
                    <i class="bi bi-pencil-square"></i>
                    <span class="indicator-label">저장</span>
                    <span class="indicator-progress">
                        Please wait...
                        <span class="spinner-border spinner-border-sm align-middle ms-2"></span>
                    </span>
                </button>
                <button type="button" class="btn btn-sm btn-light" data-bs-dismiss="modal">닫기</button>
            </div>
        </div>
    </div>
</div>
<!--end::Card-->

<#include "/view/cmm/flsys/_flsys_list_template.hbs">

<#include "/view/cmm/flsys/_flsys_file_vod_modal.ftlh">

<#include "/view/cmm/flsys/_flsys_meta_reg_modal.ftlh">
<#include "/view/cmm/flsys/_flsys_meta_dtl_modal.ftlh">
