package io.nicheblog.dreamdiary.feature.prospective.keep.entity;

import io.nicheblog.dreamdiary.feature.attachable._shared.type.ContentType;
import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * ProspectiveKeepEntityTestFactory
 * <pre>
 *  Factory helpers for ProspectiveKeep test Entity objects.
 * </pre>
 *
 * @author nichefish
 */
@UtilityClass
@ActiveProfiles("test")
public class ProspectiveKeepEntityTestFactory {

    /**
     * Build a default Entity for tests.
     */
    public static ProspectiveKeepEntity create() throws Exception {
        return ProspectiveKeepEntity.builder()
                .contentType(ContentType.PROSPECTIVE_KEEP.key)
                .title("test_title")
                .content("test_cn")
                .sortOrder(1)
                .build();
    }
}

