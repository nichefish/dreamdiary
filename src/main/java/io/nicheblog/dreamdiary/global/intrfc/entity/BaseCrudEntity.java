package io.nicheblog.dreamdiary.global.intrfc.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * BaseCrudEntity
 * <pre>
 *  (공통/상속) 기본 CRUD Entity.
 *  "All classes in the hierarchy must be annotated with @SuperBuilder."
 * </pre>
 *
 * @author nichefish
 */
@MappedSuperclass
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BaseCrudEntity
        implements Serializable {

    /** 삭제 여부 (Y/N) */
    @Builder.Default
    @Column(name = "del_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("삭제 여부 (Y/N)")
    protected String delYn = "N";

    /**
     * JPA 서브 엔티티의 리스트 필드를 설정하는 공통 메서드.
     * 한 번 Entity가 생성된 이후부터는 새 List를 할당하면 안 되고 계속 JPA 이력이 추적되어야 한다.
     *
     * @param currentList 현재 엔티티의 리스트 필드
     * @param newList 새로운 값으로 설정할 리스트
     * @param <T> 리스트의 타입
     */
    public <T> void updtList(List<T> currentList, List<T> newList) {
        if (CollectionUtils.isEmpty(newList)) return;

        if (currentList == null) {
            currentList = new ArrayList<>(newList);
        } else {
            currentList.clear();
            currentList.addAll(newList);
        }
    }
}
