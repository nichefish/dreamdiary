package io.nicheblog.dreamdiary.global.config;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;

/**
 * HttpClientConfig
 * <pre>
 *  https에서 SSL 인증서 관련 무시 설정
 * </pre>
 *
 * @author nichefish
 */
public class HttpClientConfig {
    public static HttpComponentsClientHttpRequestFactory trustRequestFactory() throws Exception {

        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = SSLContexts.custom()
                                           .loadTrustMaterial(null, acceptingTrustStrategy)
                                           .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        CloseableHttpClient httpClient = HttpClients.custom()
                                                    .setSSLSocketFactory(csf)
                                                    .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        return requestFactory;
    }
}

