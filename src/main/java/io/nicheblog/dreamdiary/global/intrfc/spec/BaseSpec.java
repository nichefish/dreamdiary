package io.nicheblog.dreamdiary.global.intrfc.spec;

import org.apache.commons.net.util.SubnetUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * BaseSpec
 * <pre>
 *  (공통/상속) 검색인자 세팅 Specification 인터페이스
 * </pre>
 *
 * @author nichefish
 */
public interface BaseSpec<Entity> {

    /**
     * default: 검색 조건 목록 반환
     */
    default Specification<Entity> searchWith(final Map<String, Object> searchParamMap) {
        // filter
        searchParamMap.remove("backToList");
        searchParamMap.remove("actvtyCtgr");

        return (root, query, builder) -> {
            List<Predicate> predicate = new ArrayList<>();
            try {
                predicate = getPredicateWithParams(searchParamMap, root, builder);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.postQuery(root, query, builder, searchParamMap);
            return builder.and(predicate.toArray(new Predicate[0]));
        };
    }

    /**
     * default: 인자별로 구체적인 검색 조건 세팅
     */
    default List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<Entity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        List<Predicate> predicate = new ArrayList<>();
        for (String key : searchParamMap.keySet()) {
            // default :: 조건 파라미터에 대해 equal 검색
            try {
                predicate.add(builder.equal(root.get(key), searchParamMap.get(key)));
            } catch (Exception e) {
                e.printStackTrace();
                // log.info("unable to locate attribute " + key + " while trying root.get(key).");
            }
        }
        return predicate;
    }

    /**
     * default: 조회 후처리 (정렬 순서 변경, distinct 등)
     */
    default void postQuery(
            final Root<Entity> root,
            final CriteriaQuery<?> query,
            final CriteriaBuilder builder,
            final Map<String, Object> searchParamMap
    ) {
        // 정렬 처리:: 기본 공백, 필요시 각 함수에서 Override
        this.postQuery(root, query, builder);
    }

    /**
     * default: 조회 후처리 (정렬 순서 변경, distinct 등)
     */
    default void postQuery(
            final Root<Entity> root,
            final CriteriaQuery<?> query,
            final CriteriaBuilder builder
    ) {
        // 정렬 처리:: 기본 공백, 필요시 각 함수에서 Override
    }

    /**
     * IPv4/CIDR 검색 조건 세팅 :: 메소드 분리
     */
    private Predicate getIpPredicate(
            final String key,
            final String ipStr,
            final Root<Entity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        boolean isCidr = ipStr.contains("/");
        // ipv4인 경우 : 단순 비교
        if (!isCidr) return builder.like(root.get(key), "%" + ipStr + "%");
        // cidr인 경우 : subnetUtils 사용
        SubnetUtils subnetUtils = new SubnetUtils(ipStr);
        String lowAddress = subnetUtils.getInfo().getLowAddress();
        String mask = ipStr.substring(ipStr.indexOf("/") + 1);
        String partialIp = ipStr;
        if (Integer.parseInt(mask) >= 24)
            partialIp = lowAddress.substring(0, lowAddress.lastIndexOf(".") + 1);
        else if (Integer.parseInt(mask) >= 16)
            partialIp = lowAddress.substring(0, lowAddress.indexOf(".", lowAddress.indexOf(".") + 1) + 1);
        else if (Integer.parseInt(mask) >= 8)
            partialIp = lowAddress.substring(0, lowAddress.indexOf(".") + 1);
        return builder.like(root.get(key), partialIp + "%");
    }
}
