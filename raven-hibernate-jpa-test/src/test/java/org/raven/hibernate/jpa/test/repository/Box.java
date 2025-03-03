package org.raven.hibernate.jpa.test.repository;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author by yanfeng
 * date 2021/9/16 15:03
 */
@Data
@FieldNameConstants
public class Box {

    private Long id;
    private String name;
    private Boolean isOpen;

    private Status status = Status.Finish;

}
