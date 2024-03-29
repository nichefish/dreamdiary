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
    -> post_no, content_type (composite_key), title, cn
    -> @comment_list, @tag_list

#### ↓ BasePostEntity :: extends BaseClsfEntity
    -> ctgr_cd, hit_cnt, fxd_yn, imprtc_yn

#### ↓ BaseEhncPostEntity :: extends BasePostEntity
    -> managtr_id, managt_dt
    -> @managtr_list, @viewer_list
