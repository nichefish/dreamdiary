# Service Interface 기본 상속 구조:

# CmmManageInterface

#### ↓ CmmReadonlyInterface

    -> getRepository, getSpec, getMapstruct
    .. 의존성을 외부에서 주입받아 인터페이스 내부에서 사용 가능하도록 함
    -> getListEntity, pageEntityToDto, getDtoList, getDtoDtl, getEntityDtl 

#### ↓ CmmCrudInterface

    -> preRegist, regist, preModify, modify, updt, preDelete, delete

#### ↓ CmmManageInterface

    -> setInUse, setInUnuse

# CmmPostInterface

#### ↓ CmmReadonlyInterface

    -> getRepository, getSpec, getMapstruct
    .. 의존성을 외부에서 주입받아 인터페이스 내부에서 사용 가능하도록 함
    -> getListEntity, pageEntityToDto, getDtoList, getDtoDtl, getEntityDtl 

#### ↓ CmmCrudInterface

    -> preRegist, regist, preModify, modify, updt, preDelete, delete

#### ↓ CmmMultiCrudInterface

    -> getFileService
    .. 의존성을 외부에서 주입받아 인터페이스 내부에서 사용 가능하도록 함
    -> regist(with multipart),  modify(with multipart)
