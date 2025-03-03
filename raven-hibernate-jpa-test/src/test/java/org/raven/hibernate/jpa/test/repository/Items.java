package org.raven.hibernate.jpa.test.repository;

import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.raven.hibernate.entity.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
//endregion
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_items")
public class Items extends BaseEntity<Long> {

    @Id
    @TGenerator
//    @GenericGenerator(name = DISTRIBUTED_ID_NAME, strategy = DISTRIBUTED_ID_STRATEGY)
    @GeneratedValue(
//            generator = DISTRIBUTED_ID_NAME
            strategy = GenerationType.AUTO
    )
    private Long id;

    private String name;
}
