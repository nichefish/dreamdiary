/** 서버 기기 등록과 로그아웃 해제를 직렬 실행해 늦은 등록이 해제를 덮지 않도록 한다. */
export interface KeepNativeToken { type: "android" | "ios"; data: string }
export interface KeepPushPorts {
  cleanup: () => Promise<void>;
  prepareChannel: () => Promise<void>;
  permission: () => Promise<boolean>;
  projectId: () => string;
  token: (projectId: string, hint?: KeepNativeToken) => Promise<string>;
  deviceId: (create: boolean) => Promise<string | null>;
  timeZone: () => string;
  register: (deviceId: string, token: string, zone: string) => Promise<void>;
  unregister: (deviceId: string) => Promise<void>;
}

export type KeepPushStatus = { state: "idle" | "syncing" | "ready" | "denied" | "error"; message: string };
export const IDLE_KEEP_PUSH: KeepPushStatus = { state: "idle", message: "로그인 후 알림 수신 상태를 확인합니다." };

/** 사용자에게 표시 가능한 고정 안내. 네이티브·토큰 예외 원문은 화면과 로그로 전달하지 않는다. */
export class KeepPushError extends Error {
  constructor(message: string) { super(message); this.name = "KeepPushError"; }
}

/** 인증 세대·로그아웃 잠금으로 실행 중인 토큰 요청과 계정 전환의 순서를 통제한다. */
export class KeepPushRegistration {
  private owner: string | null = null;
  private generation = 0;
  private loggingOut = false;
  private chain: Promise<void> = Promise.resolve();
  private snapshot = IDLE_KEEP_PUSH;
  private listeners = new Set<(status: KeepPushStatus) => void>();

  constructor(private readonly ports: KeepPushPorts) {}

  /** 인증 계정 변경은 이전 계정의 대기 작업을 무효화한다. */
  setOwner(owner: string | null): void {
    if (owner === this.owner) return;
    this.owner = owner;
    ++this.generation;
    this.loggingOut = false;
    this.publish(IDLE_KEEP_PUSH);
    console.info("[KeepPush] authentication changed", { authenticated: owner !== null });
  }

  /** 화면은 토큰 대신 수신 준비·권한·오류 상태만 구독한다. */
  subscribe(listener: (status: KeepPushStatus) => void): () => void {
    this.listeners.add(listener);
    listener(this.snapshot);
    return () => { this.listeners.delete(listener); };
  }

  private publish(status: KeepPushStatus): void {
    this.snapshot = status;
    for (const listener of this.listeners) listener(status);
  }

  private enqueue(task: () => Promise<void>): Promise<void> {
    const next = this.chain.then(task);
    this.chain = next.catch(() => {});
    return next;
  }

  /** 로그인·전경 복귀·토큰 변경·수동 재시도에서 현재 권한과 시간대를 서버에 반영한다. */
  sync(hint?: KeepNativeToken): Promise<void> {
    const owner = this.owner;
    const generation = this.generation;
    if (!owner || this.loggingOut) return Promise.resolve();
    const current = () => this.owner === owner && this.generation === generation && !this.loggingOut;
    return this.enqueue(async () => {
      if (!current()) return;
      this.publish({ state: "syncing", message: "알림 수신 상태를 확인하고 있습니다." });
      let phase = "cleanup";
      try {
        await this.ports.cleanup();
        if (!current()) return;
        phase = "channel";
        await this.ports.prepareChannel();
        if (!current()) return;
        phase = "permission";
        if (!(await this.ports.permission())) {
          if (!current()) return;
          const id = await this.ports.deviceId(false);
          if (!current()) return;
          phase = "permission-detach";
          if (id) await this.ports.unregister(id);
          if (current()) this.publish({ state: "denied", message: "알림 권한이 꺼져 있습니다. 휴대폰 설정에서 허용한 뒤 다시 확인하세요." });
          console.info("[KeepPush] permission denied; device detached");
          return;
        }
        if (!current()) return;
        phase = "project";
        const projectId = this.ports.projectId();
        phase = "token";
        const token = await this.ports.token(projectId, hint);
        if (!current()) return;
        phase = "device";
        const id = await this.ports.deviceId(true);
        if (!id) throw new KeepPushError("알림 기기 식별자를 준비하지 못했습니다.");
        if (!current()) return;
        const zone = this.ports.timeZone();
        phase = "register";
        await this.ports.register(id, token, zone);
        if (current()) this.publish({ state: "ready", message: "Keep 리마인드 알림을 받을 준비가 됐습니다." });
        console.info("[KeepPush] device registration acknowledged");
      } catch (error) {
        console.warn("[KeepPush] registration failed", { phase, errorType: error instanceof Error ? error.name : "unknown" });
        if (current()) this.publish({ state: "error", message: error instanceof KeepPushError ? error.message
          : "알림 등록을 확인하지 못했습니다. 네트워크와 푸시 설정을 확인한 뒤 다시 시도하세요." });
      }
    });
  }

  /** 등록 작업이 끝난 뒤 해제한다. 해제 미확인은 로그아웃을 실패시키고 인증을 보존한다. */
  detachForLogout(): Promise<void> {
    if (this.loggingOut) return Promise.reject(new KeepPushError("로그아웃을 처리하고 있습니다."));
    this.loggingOut = true;
    const owner = this.owner;
    const generation = this.generation;
    return this.enqueue(async () => {
      try {
        if (this.owner !== owner || this.generation !== generation) {
          throw new KeepPushError("인증 상태가 변경됐습니다. 현재 계정에서 다시 시도하세요.");
        }
        const id = await this.ports.deviceId(false);
        if (this.owner !== owner || this.generation !== generation) {
          throw new KeepPushError("인증 상태가 변경됐습니다. 현재 계정에서 다시 시도하세요.");
        }
        if (id) await this.ports.unregister(id);
        console.info("[KeepPush] logout device detach acknowledged", { deviceExists: id !== null });
      } catch (error) {
        if (this.generation === generation) {
          this.loggingOut = false;
          const message = "알림 등록 해제를 확인하지 못해 로그아웃을 멈췄습니다. 연결을 확인한 뒤 로그아웃을 다시 시도하세요.";
          this.publish({ state: "error", message });
          console.warn("[KeepPush] logout blocked", { errorType: error instanceof Error ? error.name : "unknown" });
          throw new KeepPushError(message);
        }
        throw error;
      }
    });
  }
}