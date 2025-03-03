package org.raven.hibernate.jpa.test.repository;

import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.raven.commons.data.Deletable;
import org.raven.commons.data.Versioned;
import org.raven.hibernate.entity.BaseEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//region lombok

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
//endregion
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_orders")
public class Orders extends BaseEntity<Long> implements Deletable, Versioned<Long> {

    @Id
    @TGenerator
//    @GenericGenerator(name = DISTRIBUTED_ID_NAME, strategy = DISTRIBUTED_ID_STRATEGY)
    @GeneratedValue(
//            generator = DISTRIBUTED_ID_NAME
            strategy = GenerationType.AUTO
    )
    private Long id;

    private Long uid;

    //    @OneToOne(fetch = FetchType.LAZY, targetEntity = Items.class)
//    @JoinColumn(referencedColumnName = "id")
    private Long itemsId;

    private String name;

    private Boolean isPay = Boolean.FALSE;

    private BigDecimal price;

    @Type(type = VALUE_TYPE_NAME)
    private StatusType status = StatusType.Finish;

    //    @Version
    private Long version = 1L;

    @Type(type = JSON_TYPE_NAME)
    private Box box;

    @Type(type = JSON_TYPE_NAME)
//    @ElementCollection
    private List<Integer> refs = new ArrayList<>();

    @Type(type = JSON_TYPE_NAME)
//    @ElementCollection
    private List<String> codes = new ArrayList<>();

    @LastModifiedDate
    protected Date updateTime;

    @CreatedDate
    protected Date createTime;

    private Boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Fields.uid, referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Fields.itemsId, referencedColumnName = "id", insertable = false, updatable = false)
    private Items items;

    public Orders(Long id, boolean deleted) {

        this.id = id;
        this.deleted = deleted;
    }

    @Override
    public String toString() {

        // 强制初始化关联对象
//        Hibernate.initialize(user);

        return "Orders{" +
                "id=" + id +
//                ", user=" + user +
                ", name='" + name + '\'' +
                ", isPay=" + isPay +
                ", deleted=" + deleted +
                ", status=" + status +
                ", version=" + version +
                ", box=" + box +
                ", refs=" + refs +
                ", createTime=" + createTime +
                '}';
    }
}
