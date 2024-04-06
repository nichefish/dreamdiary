package io.nicheblog.dreamdiary.global.util.snmp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

/**
 * SnmpSendParam
 * <pre>
 *  SNMP 연동 Param
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
@ToString
public class SnmpSendParam
        implements Serializable {

    /** IP 주소 */
    private String ipAddr;
    /** community */
    private String community;
    /** trapOid */
    private String trapOid;
    /** 메세지 */
    private String message;
    /** 포트 */
    private Integer port;

    /* ----- */

    /** 생성자 */
    public SnmpSendParam(final String ipAddr, final Integer port, final String community) {
        this.ipAddr = ipAddr;
        this.port = port;
        this.community = community;
    }
    public SnmpSendParam(final String ipAddr, final String community, final String message) {
        this.ipAddr = ipAddr;
        this.community = community;
        this.message = message;
    }
    public SnmpSendParam(final String ipAddr, final Integer port, final String trapOid, final String community) {
        this.ipAddr = ipAddr;
        this.port = port;
        this.trapOid = trapOid;
        this.community = community;
    }
    public SnmpSendParam(final String ipAddr, final Integer port, final String trapOid, final String community, final String message) {
        this.ipAddr = ipAddr;
        this.port = port;
        this.trapOid = trapOid;
        this.community = community;
        this.message = message;
    }
}
