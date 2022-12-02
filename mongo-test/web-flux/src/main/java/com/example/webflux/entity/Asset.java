package com.example.webflux.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(schema = "public", name = "asset_number")
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * burks
     */
    private String burks;

    /**
     * assetNumber
     */
    private String assetNumbers;

    /**
     * formId
     */
    private String formId;

    /**
     * exsitingAsset
     */
    private Integer exsitingAsset;

    /**
     * orignalAssetNumber
     */
    private Integer orignalAssetNumber;

    /**
     * installationPeriod
     */
    private Integer installationPeriod;

    /**
     * capitalizedDate
     */
    private LocalDateTime capitalizedDate;

    /**
     * prNo
     */
    private String prNo;

    /**
     * assetCategory
     */
    private String assetCategory;

    /**
     * assetClassification
     */
    private String assetClassification;

    /**
     * assetSubclass
     */
    private String assetSubclass;

    /**
     * plantCnCode
     */
    private String plantCnCode;

    /**
     * plantEnCode
     */
    private String plantEnCode;

    /**
     * departmentCnName
     */
    private String departmentCnName;

    /**
     * departmentEnName
     */
    private String departmentEnName;

    /**
     * assetCnName
     */
    private String assetCnName;

    /**
     * assetEnName
     */
    private String assetEnName;

    /**
     * currency
     */
    private String currency;

    /**
     * unitPrice
     */
    private BigDecimal unitPrice;

    /**
     * quantity
     */
    private Integer quantity;

    /**
     * costCenter
     */
    private String costCenter;

    /**
     * costCenterDepartment
     */
    private String costCenterDepartment;

    /**
     * costCenterName
     */
    private String costCenterName;

    /**
     * vehicleModelRelated
     */
    private Integer vehicleModelRelated;

    /**
     * vehicleModel
     */
    private String vehicleModel;

    /**
     * lifeYear
     */
    private String lifeYear;

    /**
     * lifePeriod
     */
    private String lifePeriod;

    /**
     * location
     */
    private String location;

    /**
     * budgetApproved
     */
    private Integer budgetApproved;

    /**
     * internalOrder
     */
    private String internalOrder;

    /**
     * wbsElement
     */
    private String wbsElement;

    /**
     * taxInternalOrder
     */
    private String taxInternalOrder;

    /**
     * wbsElementCost
     */
    private String wbsElementCost;

    /**
     * fromNewco
     */
    private Integer fromNewco;

    /**
     * orignalAssetNumberOfNewco
     */
    private Integer orignalAssetNumberOfNewco;

    /**
     * whetherSplit
     */
    private Integer whetherSplit;

    /**
     * orignalAssetNumberOfSplit
     */
    private Integer orignalAssetNumberOfSplit;

    /**
     * actionTime
     */
    private LocalDateTime actionTime;

    /**
     * applicantId
     */
    private String applicantId;

    /**
     * applicant
     */
    private String applicant;

    /**
     * email
     */
    private String email;

    /**
     * detailId
     */
    private Long detailId;

    /**
     * department code 部门编码
     */
    private String departmentCode;

    /**
     * department name 部门名称
     */
    private String departmentName;

    /**
     * isSbm
     */
    private Boolean isSbm;

    /**
     * coordinator
     */
    private String coordinator;

    /**
     * originalValue
     */
    private BigDecimal originalValue;

    /**
     * netBookValue
     */
    private BigDecimal netBookValue;

    /**
     * assetStatus
     */
    private String assetStatus;

    /**
     * vendor
     */
    private String vendor;

    /**
     * poNo
     */
    private String poNo;

    /**
     * coordinatorId
     */
    private String coordinatorId;

    /**
     * coordinatorName
     */
    private String coordinatorName;

    /**
     * buyerId
     */
    private String buyerId;

    /**
     * buyerName
     */
    private String buyerName;

    /**
     * supplierId
     */
    private String supplierId;

    /**
     * supplierName
     */
    private String supplierName;

    /**
     * provinceId
     */
    private Long provinceId;

    /**
     * provinceEnName
     */
    private String provinceEnName;

    /**
     * provinceCnName
     */
    private String provinceCnName;

    /**
     * cityId
     */
    private Long cityId;

    /**
     * cityEnName
     */
    private String cityEnName;

    /**
     * cityCnName
     */
    private String cityCnName;

    /**
     * plantId
     */
    private String plantId;

    /**
     * plantEnName
     */
    private String plantEnName;

    /**
     * plantCnName
     */
    private String plantCnName;

    /**
     * depreciation
     */
    private BigDecimal depreciation;

    /**
     * plantAreaId
     */
    private Long plantAreaId;

    /**
     * plantAreaEnName
     */
    private String plantAreaEnName;

    /**
     * plantAreaCnName
     */
    private String plantAreaCnName;

    /**
     * responsibleId
     */
    private String responsibleId;

    /**
     * responsibleName
     */
    private String responsibleName;

    /**
     * assetType
     */
    private String assetType;

    /**
     * assetOrigin
     */
    private String assetOrigin;

    /**
     * managementStatus
     */
    private String managementStatus;

    /**
     * mainCategory
     */
    private String mainCategory;

    /**
     * payInAdvance
     */
    private BigDecimal payInAdvance;

    /**
     * accumulatedDepreciation
     */
    private BigDecimal accumulatedDepreciation;

    /**
     * isRelated
     */
    private Boolean isRelated;
}
