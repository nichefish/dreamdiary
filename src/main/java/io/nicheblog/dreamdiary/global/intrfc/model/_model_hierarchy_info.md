# Model 기본 상속 구조:

#### ↓ BaseCrudDto :: implements Serializable
    -> rnum

#### ↓ BaseAuditRegDto :: extends BaseCrudDto
    -> regstrId, regDt

#### ↓ BaseAuditDto :: extends BaseAuditRegDto
    -> mdfusrId, mdfDt

#### ↓ BaseManageDto :: extends BaseAuditDto
    -> useYn, sortOrdr

#### ↓ BaseAtchDto :: extends BaseAuditDto
    -> atchFileNo, atchFileInfo

#### ↓ BaseClsfDto / BaseClsfDto :: extends BaseAtchDto
    -> postNo, contentType(composite_key), title, cn
    -> @commentList, @tagList

#### ↓ BasePostDto / BasePostListDto :: extends BaseAtchDto
    -> ctgrCd, fxdYn, hitCnt, imprtcYn

#### ↓ BaseEhncPostDto / BaseEnhcPostListDto :: extends BasePostDto / BasePostListDto
    -> managtr_id, managt_dt
    -> @managtr_list, @viewer_list
