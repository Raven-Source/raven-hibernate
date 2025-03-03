package org.raven.hibernate.jpa.test.repository;

//import org.raven.hibernate.interceptor.TenantInterceptor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
//@EntityListeners({TenantInterceptor.class})
@Entity
@DynamicUpdate
@Table(name = "entity")
public class MetaEntity extends TenantEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;
}
