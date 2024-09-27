package io.nicheblog.dreamdiary.api.kafka;

import io.nicheblog.dreamdiary.global.util.KafkaUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * KafkaApiController
 * <pre>
 *  Kafka 관련 테스트 및 수동 처리 컨트롤러.
 * </pre>
 *
 * @author nichefish
 */
@RestController
@Log4j2
public class KafkaApiController {

    @PostMapping(value = "/kafka")
    @ResponseBody
    public String sendMessage(
            @RequestParam String message
    ) {

        log.info("message : {}", message);
        KafkaUtils.sendMessage(message);

        return "success";
    }
}