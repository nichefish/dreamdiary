/**
 * global.d.ts
 *
 * @author nichefish
 */

/* ----- */

/**
 * cF : 공통 유틸리티 함수 모듈
 */
declare namespace cF {
    interface Module {
        [key: string]: Module;
    }
}
declare const cF: {
    [key: string]: Module;
};

/**
 * dF : 공통 도메인 함수 모듈
 */
declare namespace dF {
    interface Module {
        [key: string]: Module;
    }
}
declare const cF: {
    init: Function;
    [key: string]: Module;
};
/**
 * Module : 기능 단위 함수 묶음.
 */
declare interface Module {
    [key: string]: any;
}
declare interface dfModule extends Module {
    init: Function<any>;
    initialized: boolean;
    [key: string]: any;
}
/**
 * Page : 페이지 전횽 함수 묶음.
 */
declare interface Page extends Module {
    init: Function<any>;
    [key: string]: any;
}
declare var Page: {
    [key: string]: any;
}

declare interface Tagify {
    [key: string]: any;
}

/**
 * Model : Spring 컨텍스트에서 Model에 추가된 요소들.
 */
declare const Model: {
    [key: string]: any;
};
/**
 * Message : Spring Boot에서 메세지 번들로 관리되는 Message 요소들.
 */
declare const AuthInfo: {
    userId: string,
    nickNm: string,
    email: string,
    proflImgUrl: string,
    isMngr: boolean,
    authList: Array
};
/**
 * Url : Spring Boot에서 정적으로 관리되는 Url 요소들.
 */
declare const Url: {
    [key: string]: string;
};
/**
 * Message : Spring Boot에서 메세지 번들로 관리되는 Message 요소들.
 */
declare const Message: {
    get: Function
};
/**
 * Constant : Spring Boot에서 정적으로 관리되는 상수들.
 */
declare const Constant: {
    [key: string]: string;
};

/**
 * AjaxResponse : Spring Boot에서 Ajax 요청에 반환되는 응답 객체
 */
declare interface AjaxResponse {
    rslt: boolean;
    message: string;
    status: number;

    rsltObj?: {
        [key: string]: any;
    };
    rsltList?: object[];
    rsltMap?: Record<string, any>;
    rsltVal?: number;
    rsltStr?: string;
    url?: string;
}