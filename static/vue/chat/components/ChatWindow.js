/**
 * ChatWindow.js
 *
 * @author nichefish
 */
export default {
    name: 'ChatWindow',
    data() {
        return {
            message: ''     // 사용자 입력 메세지
        };
    },
    props: {
        chatMessages: Array,  // 상위 컴포넌트로부터 메시지 목록 전달
        isChatOpen: {
            type: Boolean,
            required: true
        }
    },
    methods: {
        sendMessage() {
            if (!this.message.trim()) return;

            this.$emit('send-message', this.message); // 부모에게 메세지를 전달
            this.message = ''; // 메세지 초기화
        },
        closeChat() {
            this.$emit('close-chat'); // 부모에게 채팅을 닫을 것을 알림
        }
    },
    template: `
        <!--begin::Chat drawer-->
        <div v-if="isChatOpen" class="app-chat-drawer bg-body drawer drawer-end drawer-on w-400px h-85">
            <!--begin::Messenger-->
            <div class="card w-100 border-0 rounded-0" id="kt_drawer_chat_messenger">
                <!--begin::Card header-->
                <div class="card-header pe-5" id="kt_drawer_chat_messenger_header">
                    <!--begin::Title-->
                    <div class="card-title">
                        <!--begin::User-->
                        <div class="d-flex justify-content-center flex-column me-3">
                            <a href="#" class="fs-4 fw-bold text-gray-900 text-hover-primary me-1 mb-2 lh-1">Brian Cox</a>
                            <!--begin::Info-->
                            <div class="mb-0 lh-1">
                                <span class="badge badge-success badge-circle w-10px h-10px me-1"></span>
                                <span class="fs-7 fw-semibold text-muted">Active</span>
                            </div>
                            <!--end::Info-->
                        </div>
                        <!--end::User-->
                    </div>
                    <!--end::Title-->
                    <!--begin::Card toolbar-->
                    <div class="card-toolbar">
                        <!--begin::Menu-->
                        <div class="me-0">
                            <button class="btn btn-sm btn-icon btn-active-color-primary" data-kt-menu-trigger="click" data-kt-menu-placement="bottom-end">
                                <i class="ki-duotone ki-dots-square fs-2">
                                    <span class="path1"></span>
                                    <span class="path2"></span>
                                    <span class="path3"></span>
                                    <span class="path4"></span>
                                </i>
                            </button>
                            <!--begin::Menu 3-->
                            <div class="menu menu-sub menu-sub-dropdown menu-column menu-rounded menu-gray-800 menu-state-bg-light-primary fw-semibold w-200px py-3" data-kt-menu="true">
                                <!--begin::Heading-->
                                <div class="menu-item px-3">
                                    <div class="menu-content text-muted pb-2 px-3 fs-7 text-uppercase">Contacts</div>
                                </div>
                                <!--end::Heading-->
                                <!--begin::Menu item-->
                                <div class="menu-item px-3">
                                    <a href="#" class="menu-link px-3" data-bs-toggle="modal" data-bs-target="#kt_modal_users_search">Add Contact</a>
                                </div>
                                <!--end::Menu item-->
                                <!--begin::Menu item-->
                                <div class="menu-item px-3">
                                    <a href="#" class="menu-link flex-stack px-3" data-bs-toggle="modal" data-bs-target="#kt_modal_invite_friends">Invite Contacts
                                        <span class="ms-2" data-bs-toggle="tooltip" title="Specify a contact email to send an invitation">
                                            <i class="ki-duotone ki-information fs-7">
                                                <span class="path1"></span>
                                                <span class="path2"></span>
                                                <span class="path3"></span>
                                            </i>
                                        </span>
                                    </a>
                                </div>
                                <!--end::Menu item-->
                                <!--begin::Menu item-->
                                <div class="menu-item px-3" data-kt-menu-trigger="hover" data-kt-menu-placement="right-start">
                                    <a href="#" class="menu-link px-3">
                                        <span class="menu-title">Groups</span>
                                        <span class="menu-arrow"></span>
                                    </a>
                                    <!--begin::Menu sub-->
                                    <div class="menu-sub menu-sub-dropdown w-175px py-4">
                                        <!--begin::Menu item-->
                                        <div class="menu-item px-3">
                                            <a href="#" class="menu-link px-3" data-bs-toggle="tooltip" title="Coming soon">Create Group</a>
                                        </div>
                                        <!--end::Menu item-->
                                        <!--begin::Menu item-->
                                        <div class="menu-item px-3">
                                            <a href="#" class="menu-link px-3" data-bs-toggle="tooltip" title="Coming soon">Invite Members</a>
                                        </div>
                                        <!--end::Menu item-->
                                        <!--begin::Menu item-->
                                        <div class="menu-item px-3">
                                            <a href="#" class="menu-link px-3" data-bs-toggle="tooltip" title="Coming soon">Settings</a>
                                        </div>
                                        <!--end::Menu item-->
                                    </div>
                                    <!--end::Menu sub-->
                                </div>
                                <!--end::Menu item-->
                                <!--begin::Menu item-->
                                <div class="menu-item px-3 my-1">
                                    <a href="#" class="menu-link px-3" data-bs-toggle="tooltip" title="Coming soon">Settings</a>
                                </div>
                                <!--end::Menu item-->
                            </div>
                            <!--end::Menu 3-->
                        </div>
                        <!--end::Menu-->
                        <!--begin::Close-->
                        <div class="btn btn-sm btn-icon btn-active-color-primary" id="kt_drawer_chat_close" @click="closeChat" >
                            <i class="ki-duotone ki-cross-square fs-2">
                                <span class="path1"></span>
                                <span class="path2"></span>
                            </i>
                        </div>
                        <!--end::Close-->
                    </div>
                    <!--end::Card toolbar-->
                </div>
                <!--end::Card header-->
                <!--begin::Card body-->
                <div class="card-body" id="kt_drawer_chat_messenger_body">
                    <!--begin::Messages-->
                    <div class="scroll-y me-n5 pe-5" data-kt-element="messages" data-kt-scroll="true" data-kt-scroll-activate="true" data-kt-scroll-height="auto" data-kt-scroll-dependencies="#kt_drawer_chat_messenger_header, #kt_drawer_chat_messenger_footer" data-kt-scroll-wrappers="#kt_drawer_chat_messenger_body" data-kt-scroll-offset="0px">
                        
                        <!--begin::Message(in)-->
                        <div v-for="(msg, index) in chatMessages" :key="index" :class="['d-flex', 'mb-10', msg && msg.isRegstr ? 'justify-content-end' : 'justify-content-start']">
                            <!--begin::Wrapper-->
                            <div class="d-flex flex-column" :class="msg && msg.isRegstr ? 'align-items-end' : 'align-items-start'">
                                <!--begin::User-->
                                <div class="d-flex align-items-center mb-2">
                                    <!--begin::Avatar-->
                                    <div class="symbol symbol-35px symbol-circle">
                                        <img alt="Pic" src="assets/media/avatars/300-25.jpg" />
                                    </div>
                                    <!--end::Avatar-->
                                    <!--begin::Details-->
                                    <div class="ms-3">
                                        <a href="#" class="fs-5 fw-bold text-gray-900 text-hover-primary me-1">{{ msg.regstrNm }}</a>
                                        <span class="text-muted fs-7 mb-1">5 Hours</span>
                                    </div>
                                    <!--end::Details-->
                                </div>
                                <!--end::User-->
                                <!--begin::Text-->
                                <div class="p-5 rounded bg-light-info text-gray-900 fw-semibold mw-lg-400px text-start" data-kt-element="message-text">
                                    {{ msg.cn }}
                                </div>
                                <!--end::Text-->
                            </div>
                            <!--end::Wrapper-->
                        </div>
                        <!--end::Message(in)-->
                    </div>
                    <!--end::Messages-->
                </div>
                <!--end::Card body-->
                <!--begin::Card footer-->
                <div class="card-footer pt-4" id="kt_drawer_chat_messenger_footer">
                    <!--begin::Input-->
                    <textarea v-model="message" class="form-control form-control-flush mb-3" rows="1" data-kt-element="input" placeholder="메세지를 입력하세요."></textarea>
                    <!--end::Input-->
                    <!--begin:Toolbar-->
                    <div class="d-flex flex-stack">
                        <!--begin::Actions-->
                        <div class="d-flex align-items-center me-2">
                            <button class="btn btn-sm btn-icon btn-active-light-primary me-1" type="button" data-bs-toggle="tooltip" title="Coming soon">
                                <i class="ki-duotone ki-paper-clip fs-3"></i>
                            </button>
                            <button class="btn btn-sm btn-icon btn-active-light-primary me-1" type="button" data-bs-toggle="tooltip" title="Coming soon">
                                <i class="ki-duotone ki-cloud-add fs-3">
                                    <span class="path1"></span>
                                    <span class="path2"></span>
                                </i>
                            </button>
                        </div>
                        <!--end::Actions-->
                        <!--begin::Send-->
                        <button class="btn btn-primary" type="button" data-kt-element="send" @click="sendMessage">Send</button>
                        <!--end::Send-->
                    </div>
                    <!--end::Toolbar-->
                </div>
                <!--end::Card footer-->
            </div>
            <!--end::Messenger-->
        </div>
        <!--end::Chat drawer-->
    `,
    style: `
    .app-chat-drawer {
      position: fixed;
      bottom: 100px;
      right: 20px;
      width: 300px;
      height: 400px;
      background: #fff;
      border: 1px solid #ddd;
      border-radius: 10px;
      box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
      padding: 20px;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
    }

    .chat-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .close-btn {
      background-color: #f44336;
      color: white;
      border: none;
      border-radius: 5px;
      padding: 5px 10px;
      cursor: pointer;
    }

    .chat-body {
      margin-top: 10px;
    }

    textarea {
      width: 100%;
      height: 80%;
      margin-bottom: 10px;
    }
  `
};