<#--
  -- 일정 > 전체 일정 > 일정 등록 Template
  -- @included in : schdul_cal
  -->
<script id="schdul_reg_template" type="text/x-handlebars-template">
    <input type="hidden" name="schdulNo" id="schdulNo" value="{{schdulNo}}">
    <input type="hidden" name="prvtYn" id="prvtYn" value="{{#if isPrvt}}Y{{else}}N{{/if}}">
    <!--begin::Input group-->
    <div class="row mb-3">
        <div class="col-xl-2 col-2">
            <label class="col-form-label text-center fs-6 fw-bold required mb-2">제목</label>
        </div>
        <div class="col-xl-3 col-4">
            <select name="schdulTyCd" id="schdulTyCd" class="form-select form-select-solid required" onchange="Schdul.fnSchdulTyCd(this);">
                <option value="">--구분--</option>
                <#list SCHDUL_TY_CD![] as topic>
                    {{#if isPrvt}}
                        {{#unless (equals "${topic.dtlCd!}" "HLDY")}} {{#unless (equals "${topic.dtlCd!}" "CEREMONY")}} {{#unless (equals "${topic.dtlCd!}" "TLCMMT")}}
                            <option value="${topic.dtlCd!}" id="${topic.dtlCd!}" {{selectedIf (equals schdulTyCd "${topic.dtlCd!}")}} >[${topic.dtlCdNm!}]</option>
                        {{/unless}} {{/unless}} {{/unless}}
                    {{else}}
                        <option value="${topic.dtlCd!}" id="${topic.dtlCd!}" {{selectedIf (equals schdulTyCd "${topic.dtlCd!}")}} >[${topic.dtlCdNm!}]</option>
                    {{/if}}
                </#list>
            </select>
        </div>
        <div class="col-xl-7 col-6">
            <input type="text" class="form-control form-control-solid required" placeholder="제목" name="schdulNm" id="schdulNm" maxlength="120" value="{{schdulNm}}" />
        </div>
        <div class="col-xl-2 col-2">
        </div>
        <div class="col-xl-3 col-4">
            <span id="schdulTyCdSpan"></span>
        </div>
        <div class="col-xl-7 col-6">
            <span id="schdulNmSpan"></span>
        </div>
    </div>
    <!--begin::Input group-->
    <div class="row mb-3">
        <div class="col-xl-2 col-2 d-flex align-items-center justify-content-between">
            <label class="col-form-label text-center fs-6 fw-bold" for="prtcpnt">
                참여자
            </label>
            <span class="btn btn-sm btn-icon btn-light-primary" onclick="Schdul.fnAddPrtcpnt();">
                <i class="fas fa-plus float-end"></i>
            </span>
        </div>
        {{#if isPrvt}}
            <div class="col-xl-10 col-10 d-flex align-items-center">
                <div class="text-noti fs-small">※개인용 일정의 경우 참여자에게 공유할 수 있습니다. (본인과 참여자 화면에만 노출됩니다.)</div>
            </div>
            <div class="col-xl-2 col-2"></div>
        {{/if}}
        <div class="col-xl-10 col-10" id="schdul_reg_prtcpnt_div">
            {{#prtcpntList}}
                <div id="prtcpntRow{{@index}}" class="d-flex align-items-center mb-1">
                    <select name="prtcpntList[{{@index}}].userId" class="form-select form-select-solid me-2">
                        <option value="">--선택--</option>
                        <#list crtdUserList![] as user>
                            <option value="${user.userId!}" id="${user.userId!}" {{selectedIf (equals userId "${user.userId}")}}>${user.userNm!}</option>
                        </#list>
                    </select>
                    <span class="btn btn-sm btn-icon btn-light-danger" onclick="Schdul.fnRemovePrtcpnt({{@index}});">
                        <i class="fas fa-trash float-end"></i>
                    </span>
                </div>
            {{/prtcpntList}}
            <!-- handlebars template goes in here -->
        </div>
    </div>
    <!--begin::Input group-->
    <div class="row mb-3">
        <div class="col-xl-2 col-2">
            <label class="col-form-label text-center fs-6 fw-bold mb-2" for="schdulResn">설명</label>
        </div>
        <div class="col-xl-10 col-10">
            <textarea name="schdulResn" id="schdulResn" class="form-control form-control-solid h-100px" wrap="hard" maxlength="500">{{schdulResn}}</textarea>
        </div>
        <div class="col-xl-2 col-2">
        </div>
        <div class="col-xl-10 col-10">
            <span id="schdulResnSpan"></span>
        </div>
    </div>
    <!--begin::Input group-->
    <div class="row row-cols-lg-2 mb-3">
        <div class="col-xl-2 col-2">
            <label class="col-form-label text-center fs-6 fw-bold mb-2" for="schdulResn">일정</label>
        </div>
        <div class="col-xl-5 col-5">
            <label class="fs-6 mt-3 mb-2 required" for="beginDt">시작일</label>
            <input class="form-control form-control-solid required" name="beginDt" id="beginDt" placeholder="시작일" value="{{beginDt}}" readonly/>
        </div>
        <div class="col-xl-5 col-5" id="endDtDiv">
            <label class="fs-6 mt-3 mb-2" for="endDt">종료일</label>
            <input class="form-control form-control-solid" name="endDt" id="endDt" placeholder="종료일" value="{{endDt}}" readonly />
        </div>
        <div class="col-xl-2 col-2 col-form-label"></div>
        <div class="col-xl-5 col-5">
            <span id="beginDtSpan"></span>
        </div>
        <div class="col-xl-5 col-5">
            <span id="endDtSpan" class="text-noti">※미입력시 시작일자와 같게끔 처리됩니다.</span>
        </div>
    </div>
    {{^if isPrvt}}
        <!--begin::Input group-->
        <div class="row mb-3">
            <div class="col-xl-2 col-2">
                <label class="col-form-label text-center fs-6 fw-bold mb-2 cursor-help"
                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="해당 글이 작성되었음을&#10;사내 메신저(JANDI)를 통해&#10;알립니다.">
                    잔디 알림 발송
                </label>
            </div>
            <div class="col-xl-2 col-4">
                <div class="form-check form-switch mt-2 form-check-custom form-check-solid">
                    <input class="form-check-input ms-3" type="checkbox" value="Y" name="jandiYn" id="jandiYn" <#if (((notice.jandiYn)!"N") == "Y")!false>checked="checked"</#if> />
                    <label class="form-check-label fw-bold ms-3" for="jandiYn" id="jandiYnLabel" style="color:<#if (((notice.jandiYn)!"N") == "Y")!false>#0095E8<#else>gray</#if>;">
                        <#if ((notice.jandiYn)! == "Y")!false>발송<#else>미발송</#if>
                    </label>
                </div>
            </div>
            <div class="col-xl-4 col-4">
                <!-- 가리고 숨기고를 반복하는 곳은 style로 선언해야 한다. -->
                <div id="trgetTopicSpan" style="display:none;">
                    <select name="trgetTopic" id="trgetTopic" class="form-select form-select-solid"
                            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="잔디 메세지를 발송할&#10;토픽(대화방)을 선택합니다.">
                        <#list JANDI_TOPIC_CD![] as topic>
                            <#if topic.dtlCd == "SCHDUL">
                                <option value="${topic.dtlCd!}" id="${topic.dtlCd!}">[${topic.dtlCdNm!}]</option>
                            </#if>
                        </#list>
                        <#if isDev>
                            <option value="TEST">[테스트]</option>
                        </#if>
                    </select>
                </div>
            </div>
        </div>
    {{/if}}
</script>