# Entity 기본 상속 구조:

#### ↓ BaseCrudEntity :: implements Serializable
    -> del_yn

#### ↓ BaseAuditRegEntity :: extends BaseCrudEntity
    -> regstr_id reg_dt

#### ↓ BaseAuditEntity :: extends BaseAuditRegEntity
    -> mdfuse_id, mdf_dt

#### ↓ BaseManageEntity :: extends BaseAuditEntity
    -> sort_ordr, use_yn

#### ↓ BaseAtchEntity :: extends BaseAuditEntity
    -> atch_file_no, atch_file_info

#### ↓ BaseClsfEntity :: extends BaseAtchEntity
    -> post_no, content_type (composite_key)
    -> @tag_list

#### ↓ BasePostEntity :: extends BaseClsfEntity
    -> title, cn, ctgr_cd, imprtc_yn, fxd_yn, hit_cnt

#### ↓ BaseEhncPostEntity :: extends BasePostEntity
    -> managtr_id, managt_dt
    -> @managtr_list, @viewer_list
