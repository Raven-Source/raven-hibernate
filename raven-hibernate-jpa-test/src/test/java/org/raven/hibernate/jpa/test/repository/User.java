package org.raven.hibernate.jpa.test.repository;


import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.raven.commons.data.Deletable;
import org.raven.hibernate.entity.BaseEntity;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Date;

//region lombok
@ToString
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
//endregion
@Entity
@DynamicUpdate
@Table(name = "t_user")
public class User extends BaseEntity<Long> implements Deletable {

    /**
     * 租户id
     */
    private Long tenantId;

    @CreatedDate
    protected Date createTime;

    private Boolean deleted;


    @Id
    @TGenerator
//    @GenericGenerator(name = DISTRIBUTED_ID_NAME, strategy = DISTRIBUTED_ID_STRATEGY)
    @GeneratedValue(
//            generator = DISTRIBUTED_ID_NAME
            strategy = GenerationType.AUTO
    )
//    @GeneratedValue(
//            strategy = GenerationType.AUTO
//    )
    private Long id;

    private String name;

    @Type(type = VALUE_TYPE_NAME)
    @Column(name = "user_type")
    private UserType type;

    private LocalTime time;

}
