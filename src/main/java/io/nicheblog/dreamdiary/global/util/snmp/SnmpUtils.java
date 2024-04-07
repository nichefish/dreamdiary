package io.nicheblog.dreamdiary.global.util.snmp;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Vector;

/**
 * SnmpService
 * 가청 경보(사이렌) 관련 SNMP Protocol 서비스 모듈
 * "The SNMP service acts as an internal alarm listener and *sends traps* (or notifications) to any registered SNMP trap listener."
 *
 * @author nichefish
 */
@Component
public class SnmpUtils {

    // 정의되어야 할 값들
    /** 기본 oid */
    static String defaultOID = "1";      // 사이렌의 OID값
    /** 기본 SNMP port */
    static int defaultPort = 162;
    /** 기본 IP */
    static final String defaultIp = "127.0.0.1";
    /** 기본 community */
    static final String defaultCommunity = "private";

    // "이 때, "Community String은 Default public, private로 대부분 설정되어 있다."
    // "이 String을 이용해 시스템의 주요 정보 및 설정을 파악할 수 있다. 그러므로 Default community 명인 public, private를 변경하여 사용해야 한다."
    // "통신하고자 하는 Server/Client에 모두 같은 Community String을 사용해야 한다."

    /**
     * SNMP 메세지 발송
     */
    public static Boolean sendSnmpMessage() throws Exception {
        return sendSnmpMessage(new SnmpParam(defaultIp, defaultPort, defaultCommunity));
    }
    /**
     * SNMP 메세지 발송
     * @param snmpParam : SNMP 전송 정보 (공통)
     */
    public static Boolean sendSnmpMessage(final SnmpParam snmpParam) throws Exception {
        SnmpParam snmpSend = (snmpParam != null) ? snmpParam : new SnmpParam(defaultIp, defaultPort, defaultCommunity);
        if (snmpSend.getPort() == null) snmpSend.setPort(defaultPort);

        //1. Make Protocol Data Unit
        // "Each SNMP message contains a protocol data unit (PDU)"
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(defaultOID), new OctetString(snmpSend.getMessage())));
        pdu.setType(PDU.SET);

        //2. Make target
        CommunityTarget target = new CommunityTarget();
        UdpAddress targetAddress = new UdpAddress(InetAddress.getByName(snmpSend.getIpAddr()), snmpSend.getPort());
        target.setAddress(targetAddress);
        target.setCommunity(new OctetString(snmpSend.getCommunity()));
        target.setVersion(SnmpConstants.version1);         // snmp version? 확인해야 한다.

        //3. Make SNMP Message. Simple!
        Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
        snmp.listen();

        //4. Send Message and Recieve Response
        ResponseEvent response = snmp.send(pdu, target);
        if (response.getResponse() == null) {
            System.out.println("Error: There is some problems.");
        } else {
            Vector variableBindings = (Vector) response.getResponse().getVariableBindings();
            for (Object variableBinding : variableBindings) {
                System.out.println(variableBinding);
            }
        }
        snmp.close();

        return true;
    }
}