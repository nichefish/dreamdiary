<#--
  -- 내 정보(user_my) > 휴가사용현황 Modal
  -- @included_in : user_my_dtl
  -->

<div class="modal fade" id="user_my_dtl_vcatn_list" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <!-- 영역:: 모달 헤더 -->
            <@component.modal_header "${vcatnYy.statsYy}년 휴가 상세 현황" />
            <!-- 영역:: 모달 내용 -->
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <table class="table table-row-bordered table-row-gray-300 gy-2 align-middle fs-small">
                        <tbody>
                            <#list vcatnPaprList![] as vcatnList>
                                <tr>
                                    <td class="text-start">
                                        <#list vcatnList.schdulList![] as schdul>
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