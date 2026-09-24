package io.nicheblog.dreamdiary.feature.prospective.keep.model;

import io.nicheblog.dreamdiary.feature.attachable._shared.type.ContentType;
import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * ProspectiveKeepDtoTestFactory
 * <pre>
 *  Factory helpers for ProspectiveKeep test DTO objects.
 * </pre>
 *
 * @author nichefish
 */
@UtilityClass
@ActiveProfiles("test")
public class ProspectiveKeepDtoTestFactory {

    /**
     * Build a default DTO for tests.
     */
    public static ProspectiveKeepDto create() throws Exception {
        return ProspectiveKeepDto.builder()
                .contentType(ContentType.PROSPECTIVE_KEEP.key)
                .title("test_title")
                .content("test_cn")
                .build();
    }

    /**
     * Build a DTO with an existing key for update/delete tests.
     *
     * @param key primary key
     */
    public static ProspectiveKeepDto createWithKey(final Integer key) throws Exception {
        return ProspectiveKeepDto.builder()
                .id(key)
                .contentType(ContentType.PROSPECTIVE_KEEP.key)
                .title("test_title")
                .content("test_cn")
                .build();
    }
}

