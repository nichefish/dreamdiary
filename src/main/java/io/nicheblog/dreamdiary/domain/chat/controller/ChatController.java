package io.nicheblog.dreamdiary.domain.chat.controller;

import io.nicheblog.dreamdiary.auth.jwt.provider.JwtTokenProvider;
import io.nicheblog.dreamdiary.auth.security.util.AuthUtils;
import io.nicheblog.dreamdiary.domain.chat.model.ChatMsgDto;
import io.nicheblog.dreamdiary.domain.chat.model.ChatMsgSearchParam;
import io.nicheblog.dreamdiary.domain.chat.service.ChatMsgService;
import io.nicheblog.dreamdiary.extension.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.model.AjaxResponse;
import io.nicheblog.dreamdiary.global.model.ServiceResponse;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;

/**
 * ChatController
 * <pre>
 *  채팅 관련 컨트롤러.
 * </pre>
 *
 * @author nichefish
 */
@Controller
@RequiredArgsConstructor
@Log4j2
public class ChatController {

    private final ChatMsgService chatMsgService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 기존 채팅 메시지를 DB에서 가져온다.
     *
     * @param searchParam 검색 파라미터
     * @param logParam 로그 파라미터
     * @return AjaxResponse를 포함한 ResponseEntity 객체
     * @throws Exception 예외 발생 시
     */
    @GetMapping("/chat/messages")
    @ResponseBody
    public ResponseEntity<AjaxResponse> getChatMessages(
            final ChatMsgSearchParam searchParam,
            final LogActvtyParam logParam
    ) throws Exception {

        final List<ChatMsgDto> messageList = chatMsgService.getListDto(searchParam);
        final boolean isSuccess = true;
        final String rsltMsg = MessageUtils.RSLT_SUCCESS;

        // 로그 관련 세팅
        logParam.setResult(isSuccess, rsltMsg);

        return ResponseEntity.ok(AjaxResponse.withAjaxResult(isSuccess, rsltMsg).withList(messageList));
    }

    /**
     * 클라이언트로부터 메시지를 받아서 처리하고, 결과를 반환합니다.
     *
     * @param message 클라이언트로부터 받은 메시지
     * @return AjaxResponse 객체를 포함한 결과
     * @throws Exception 예외 발생 시
     */
    @MessageMapping("/chat/send")
    @SendTo("/topic/chat")
    public ResponseEntity<AjaxResponse> sendMessage(
            final @Payload String message,
            final StompHeaderAccessor stompHeaderAccessor
    ) throws Exception {

        log.info("ChatController.sendMessage() message: {}", message);

        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException(MessageUtils.getExceptionMsg("IllegalArgumentException.empty-msg"));
        }

        // WebSocket 세션에서 attributes 가져오기
        final Authentication authentication = (Authentication) Objects.requireNonNull(stompHeaderAccessor.getSessionAttributes()).get("authentication");
        AuthUtils.setAuthentication(authentication);

        final ChatMsgDto chatMsg = ChatMsgDto.builder()
                        .cn(message)
                        .build();

        final ServiceResponse result = chatMsgService.regist(chatMsg);  // 채팅 메시지 등록
        final boolean isSuccess = result.getRslt();
        final String rsltMsg = isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE;

        return ResponseEntity.ok(AjaxResponse.fromResponseWithObj(result, rsltMsg));
    }
}