# Service Interface 기본 상속 구조:

#### ↓ BaseReadonlyService :: implements Serializable
    -> getRepository, getSpec, getMapstruct (dependency injection)
    -> getListEntity, getListDto, pageEntityToDto, getDtlEntity, getDtlDto

#### ↓ BaseCrudInterface :: extends BaseReadonlyService
    -> preRegist, regist, preModify, modify, updt, preDelete, delete

#### ↓ BaseManageInterface :: extends BaseCrudInterface
    -> setInUse, setInUnuse

#### ↓ BaseMultiCrudService :: extends BaseCrudInterface
    -> getFileService (dependency injection)
    -> CRUD uses multipartRequest

#### ↓ BaseClsfService :: extends BaseMultiCrudService

#### ↓ BasePostService :: extends BaseClsfService
