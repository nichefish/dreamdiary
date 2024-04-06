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
public class SnmpParam
        implements Serializable {

    /** IP 주소 */
    @Builder.Default
    private String ipAddr = "127.0.0.1";
    /** community */
    @Builder.Default
    private String community = "private";
    /** trapOid */
    @Builder.Default
    private String trapOid = "1";
    /** 메세지 */
    @Builder.Default
    private String message = "Security";
    /** 포트 */
    @Builder.Default
    private Integer port = 162;

    /* ----- */

    /** 생성자 */
    public SnmpParam(final String ipAddr) {
        this.ipAddr = ipAddr;
    }
    public SnmpParam(final String ipAddr, final Integer port, final String community) {
        this.ipAddr = ipAddr;
        this.port = port;
        this.community = community;
    }
    public SnmpParam(final String ipAddr, final String community, final String message) {
        this.ipAddr = ipAddr;
        this.community = community;
        this.message = message;
    }
    public SnmpParam(final String ipAddr, final Integer port, final String trapOid, final String community) {
        this.ipAddr = ipAddr;
        this.port = port;
        this.trapOid = trapOid;
        this.community = community;
    }
    public SnmpParam(final String ipAddr, final Integer port, final String trapOid, final String community, final String message) {
        this.ipAddr = ipAddr;
        this.port = port;
        this.trapOid = trapOid;
        this.community = community;
        this.message = message;
    }
}
