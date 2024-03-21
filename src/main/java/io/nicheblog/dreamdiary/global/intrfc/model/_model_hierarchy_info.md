package net.sinzi.intranet.cmm.intrfc.model;

# Model 기본 상속 구조:

## CmmManageDto

#### ↓ CmmCrudDto

    -> //

#### ↓ CmmAuditRegDto

    -> regstrId, regDt

#### ↓ CmmAuditDto

    -> mdfusrId, mdfDt

#### ↓ CmmManageDto

    -> sortOrdr, useYn

## CmmPostDto // CmmPostListDto

#### ↓ CmmCrudDto

    -> //

#### ↓ CmmAuditRegDto

    -> regstrId, regDt

#### ↓ CmmAuditDto

    -> mdfusrId, mdfDt

#### ↓ CmmAtchDto

    -> atchFileId, atchFileInfo
