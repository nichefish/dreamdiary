# Entity 기본 상속 구조:

#### ↓ BaseCrudEntity :: implements Serializable
    - DEL_YN

#### ↓ BaseAuditRegEntity :: extends BaseCrudEntity
    -> REGSTR_ID, REG_DT

#### ↓ BaseAuditEntity :: extends BaseAuditRegEntity
    -> MDFUSR_ID, MDF_DT

#### ↓ BaseManageEntity :: extends BaseAuditEntity
    -> SORT_ORDR, USE_YN

#### ↓ BaseAtchEntity :: extends BaseAuditEntity
    -> ATCH_FILE_NO, ATCH_FILE_INFO

#### ↓ BaseClsfEntity :: extends BaseAtchEntity
    -> POST_NO, BOARD_CD (composite_key)
    -> @commentList, @tagList

#### ↓ BasePostEntity :: extends BaseClsfEntity
    -> title, cn, ctgrCd,
    -> @managtrList, @viewerList
