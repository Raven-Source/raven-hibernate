package org.raven.hibernate.jpa.test.repository;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.Date;


@FieldNameConstants
@Data
@Entity
@Table(name = "mass_task")
@IdClass(MassTaskId.class)
public class MassTask {

    @Id
    @Column(name = "task_id")
    private String taskId;

    @Id
    @Column(name = "staff_id")
    private String staffId;

    @Column(name = "staff_name")
    private String staffName;

    @Column(name = "store_code")
    private String storeCode;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "store_type")
    private String storeType;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "status")
    private String status;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "send_status")
    private Integer sendStatus;

    @Column(name = "expected_count")
    private Integer expectedCount;

    @Column(name = "arrived_count")
    private Integer arrivedCount;

    @Column(name = "failed_count")
    private Integer failedCount;

    @Column(name = "execute_status")
    private Integer executeStatus;

    @Column(name = "province_code")
    private String provinceCode;

    @Column(name = "city_code")
    private String cityCode;

    @Column(name = "area_code")
    private String areaCode;

    @Column(name = "province")
    private String province;

    @Column(name = "city")
    private String city;

    @Column(name = "area")
    private String area;

    @Column(name = "area_owner")
    private String areaOwner;

    @Column(name = "region_owner")
    private String regionOwner;

    @Column(name = "area_owner_name")
    private String areaOwnerName;

    @Column(name = "region_owner_name")
    private String regionOwnerName;

    @Column(name = "region_organization_code")
    private String regionOrganizationCode;

    @Column(name = "region_organization_name")
    private String regionOrganizationName;

    @Column(name = "area_organization_code")
    private String areaOrganizationCode;

    @Column(name = "area_organization_name")
    private String areaOrganizationName;

    @Column(name = "dt")
    private Integer dt;
}
