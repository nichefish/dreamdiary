package io.nicheblog.dreamdiary.global.handler;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolver;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * UTF8DecodeResourceResolver
 * <pre>
 *  리소스 이름에 한글 사용시 깨짐 방지 위한 처리
 * </pre>
 *
 * @author nichefish
 */
public class UTF8DecodeResourceResolver
        extends PathResourceResolver
        implements ResourceResolver {

    @Override
    protected Resource getResource(
            final @NotNull String resourcePathParam,
            final @NotNull Resource location
    ) throws IOException {
        String resourcePath = URLDecoder.decode(resourcePathParam, StandardCharsets.UTF_8);
        return super.getResource(resourcePath, location);
    }
}