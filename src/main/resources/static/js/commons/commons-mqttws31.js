/**
 * commons-mqttws31.js
 * @namespace: commons.mqttws31
 * @author: nichefish
 * @since: 2022-06-27~
 * 공통 - mqtt 웹소켓 클라이언트 함수 모듈
 * (노출식 모듈 패턴 적용 :: commons.util.enterKey("#userId") 이런식으로 사용)
 */
if (typeof commons === 'undefined') { var commons = {}; }
commons.mqttws31 = (function() {
    return {
        defaultOptions: {

        },
        /**
         * mqttStart
         */
        fnMqttStart: function(componentId, options, onSuccFunc) {
            let mqttClient = commons.mqttws31.getMqttClient(componentId, options);
            commons.mqttws31.fnMqttConnect(mqttClient, options, onSuccFunc);
            return mqttClient;
        },
        /**
         * mqttClient 객체 생성해서 반환 :: 메소드 분리
         */
        getMqttClient: function(componentId, option) {
            let mqttHost = options.host;
            let mqttPort = options.port;
            let mqttClientId = options.title + "_client_" + componentId + Math.floor(Math.random() * 10000000);

            let mqttClient = new Paho.MQTT.Client(mqttHost, mqttPort, mqttClientId);
            mqttClient.onConnectionLost = function(responseObject) {
                // 연결 재시도
                if (options.connRetryCnt === undefined) options.connRetryCnt = 0;
                options.connRetryCnt++;
                if (option.connRetryCnt < 5) {
                    options.reconnTimer = setTimeout(function() {
                        commons.mqttws31.fnMqttConnect(mqttClient)
                    }, options.reconnTimeout);
                }
            }
            if (options.fnMqttMsg !== undefined) {
                mqttClient.onMessageArrived = function(message, options) {
                    options.fnMqttMsg(message);
                }
            }
            return mqttClient;
        },
        /**
         * mqttConnect :: 메소드 분리
         */
        fnMqttConnect: function(mqttClient, options, onSuccFunc) {
            if (options === undefined) options = {};
            let isLogging = options.logging !== undefined ? options.logging : false;
            let topics = options.topics;
            mqttClient.connect({
                userName: options.userName,
                password: options.password,
                timeout: 5,
                useSSL: options.useSSL !== undefined ? options.useSSL : false,
                keepAliveInterval: 30,
                onSuccess: function() {
                    if (isLogging) console.log(mqttClient.clientId + " // connect: onConnect...");
                    if (onSuccFunc !== undefined) {
                        if (!Array.isArray(topics) || topics.length < 1) return;
                        topics.forEach(function(topic) {
                            mqttClient.subscribe(topics);
                        });

                    }
                },
                onFailure: function() {
                    if (isLogging) console.log(mqttClient.clientId + " // connect: onFailure...");
                }
            });
        },
        /**
         * mqttPublish
         */
        fnPublish: function(mqttClient, options, topic, payloadStr) {
            if (options === undefined) options = {};
            let isLogging = options.logging !== undefined ? options.logging : false;
            let message = new Paho.MQTT.Message(payloadStr);
            message.destinationName = topic;
            mqttClient.send(message);
            if (isLogging) console.log(mqttClient.clientId + " // message sent:: " + topic + " // " + payloadStr);
        }
    }
})();
