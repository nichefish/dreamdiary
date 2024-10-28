package io.nicheblog.dreamdiary.global.util;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Country;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;

/**
 * Geoip2Utils
 * <pre>
 *  maxmind geoip2:: IP-국가명 매칭 라이브러리 서비스 모듈
 * </pre>
 * @author nichefish
 */
@UtilityClass
public class GeoIp2Utils {

    /**
     * IP 주소로 국가 정보 조회
     */
    public static Country getNtnByIp(final String ipAddr) throws Exception {
        File database = new File("GeoLite2-City.mmdb");
        if (!database.exists()) throw new FileNotFoundException("FileNotFoundException");

        // This reader object should be reused across lookups as creation of it is expensive.
        // If you want to use caching at the cost of a small (~2MB) memory overhead:
        DatabaseReader reader = new DatabaseReader.Builder(database).withCache(new CHMCache()).build();

        InetAddress ipAddress = InetAddress.getByName(ipAddr);
        CityResponse response = reader.city(ipAddress);

        return response.getCountry();
    }

    /**
     * IP 주소로 국가 코드 조회
     */
    public static String getNtnCdByIp(final String ipAddr) throws Exception {
        return getNtnByIp(ipAddr).getIsoCode();
    }

    /**
     * IP 주소로 국가명 (영문) 조회
     */
    public static String getNtnNmByIp(final String ipAddr) throws Exception {
        return getNtnByIp(ipAddr).getName();
    }
}
