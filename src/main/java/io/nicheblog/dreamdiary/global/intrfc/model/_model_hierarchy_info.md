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
    -> postNo, boardCd (composite_key)
    -> @commentList, @tagList

#### ↓ BasePostDto / BasePostListDto :: extends BaseClsfEntity
    -> title, cn, ctgrCd,
    -> @managtrList, @viewerList
