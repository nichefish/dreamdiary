package io.nicheblog.dreamdiary.global.intrfc.mapstruct;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.*;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed.CommentEmbedMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed.ManagtEmbedMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed.TagEmbedMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed.ViewerEmbedMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseClsfDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.*;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

/**
 * BaseClsfMapstruct
 * <pre>
 *  (공통/상속) MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 */
public interface BaseClsfMapstruct<Dto extends BaseClsfDto, Entity extends BaseClsfEntity>
        extends BaseMapstruct<Dto, Entity> {

    @AfterMapping
    default void mapClsfFields(final Entity entity, final @MappingTarget Dto dto) throws Exception {

        // 댓글 :: 공통 필드 매핑 로직
        boolean usesCommentModule = (entity instanceof CommentEmbedModule && dto instanceof CommentCmpstnModule);
        if (usesCommentModule) {
            CommentEmbed embed = ((CommentEmbedModule) entity).getComment();
            CommentCmpstn cmpstn = CommentEmbedMapstruct.INSTANCE.toDto(embed);
            ((CommentCmpstnModule) dto).setComment(cmpstn);
        }

        // 태그 :: 공통 필드 매핑 로직
        boolean usesTagModule = (entity instanceof TagEmbedModule && dto instanceof TagCmpstnModule);
        if (usesTagModule) {
            TagEmbed embed = ((TagEmbedModule) entity).getTag();
            TagCmpstn cmpstn = TagEmbedMapstruct.INSTANCE.toDto(embed);
            ((TagCmpstnModule) dto).setTag(cmpstn);
        }
        
        // 조치 :: 공통 필드 매핑 로직
        boolean usesManagtModule = (entity instanceof ManagtEmbedModule && dto instanceof ManagtCmpstnModule);
        if (usesManagtModule) {
            ManagtEmbed embed = ((ManagtEmbedModule) entity).getManagt();
            ManagtCmpstn cmpstn = ManagtEmbedMapstruct.INSTANCE.toDto(embed);
            ((ManagtCmpstnModule) dto).setManagt(cmpstn);
        }

        // 열람 :: 공통 필드 매핑 로직
        boolean usesViewerModule = (entity instanceof ViewerEmbedModule && dto instanceof ViewerCmpstnModule);
        if (usesViewerModule) {
            ViewerEmbed embed = ((ViewerEmbedModule) entity).getViewer();
            ViewerCmpstn cmpstn = ViewerEmbedMapstruct.INSTANCE.toDto(embed);
            ((ViewerCmpstnModule) dto).setViewer(cmpstn);
        }
    }
}
