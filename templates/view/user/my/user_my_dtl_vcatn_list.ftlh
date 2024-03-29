<#--
  -- 내 정보 > 휴가사용현황 Modal
  -- @included in : user_my_dtl
  -->
<div class="modal fade" id="user_my_dtl_vcatn_list" tabindex="-1" role="dialog" aria-labelledby="vcatnListModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="vcatnListModalLongTitle">[${vcatnYy.statsYy}년 휴가 상세 현황]</h5>
                <div class="btn btn-sm btn-icon btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <table class="table table-row-bordered table-row-gray-300 gy-2 align-middle fs-small">
                        <tbody>
                            <#list vcatnPaprList![] as vcatnList>
                                <tr>
                                    <td class="text-start">
                                        <#list vcatnList.vcatnSchdulList![] as schdul>
                                            <span class="pe-4">
                                                <span class="ctgr-span ctgr-gray">${schdul.vcatnNm!}</span>
                                                ${schdul.bgnDt!}
                                                <#if schdul.endDt?? && schdul.bgnDt != schdul.endDt>~ ${schdul.endDt!}</#if>
                                            </span>
                                        </#list>
                                    </td>
                                </tr>
                            <#else>
                                <tr>
                                    <td colspan=8" class="text-center"><@spring.message "view.list.empty"/></td>
                                </tr>
                            </#list>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>