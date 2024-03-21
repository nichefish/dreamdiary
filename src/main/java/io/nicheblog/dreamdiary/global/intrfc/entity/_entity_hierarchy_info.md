package net.sinzi.intranet.cmm.intrfc.entity;

# Entity 기본 상속 구조:

## CmmManageEntity

#### ↓ CmmCrudEntity

    -> DEL_YN

#### ↓ CmmAuditRegEntity

    -> REGSTR_ID, REG_DT

#### ↓ CmmAuditEntity

    -> MDFUSR_ID, MDF_DT

#### ↓ CmmManageEntity

    -> SORT_ORDR, USE_YN

## CmmPostEntity

#### ↓ CmmCrudEntity

    -> DEL_YN

#### ↓ CmmAuditRegEntity

    -> REGSTR_ID, REG_DT

#### ↓ CmmAuditEntity

    -> MDFUSR_ID, MDF_DT

#### ↓ CmmAtchEntity

    -> ATCH_FILE_ID, ATCH_FILE_INFO
