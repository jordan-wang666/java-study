package com.example.databasetest;

import com.example.databasetest.entity.AssetNumber;
import com.example.databasetest.repository.AssetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class DatabaseTestApplicationTests {

    @Resource
    private AssetRepository assetRepository;

    @Test
    void contextLoads() {
        Optional<AssetNumber> byId = assetRepository.findById(4000);
        long count = assetRepository.count();
        System.out.println(count);
//        List<AssetNumber> list = new ArrayList<>();
//        for (int i = 0; i < 40000; i++) {
//            list.add(AssetNumber.builder()
//                    .id(40000+i)
//                    .burks("SY")
//                    .assetNumbers(String.valueOf(i))
//                    .formId(String.valueOf(i))
//                    .exsitingAsset(i)
//                    .orignalAssetNumber(i)
//                    .installationPeriod(i)
//                    .capitalizedDate(LocalDateTime.now())
//                    .prNo(String.valueOf(i))
//                    .assetCategory(String.valueOf(i))
//                    .assetClassification(String.valueOf(i))
//                    .assetSubclass(String.valueOf(i))
//                    .plantCnCode(String.valueOf(i))
//                    .plantEnCode(String.valueOf(i))
//                    .departmentCnName(String.valueOf(i))
//                    .departmentEnName(String.valueOf(i))
//                    .assetCnName(String.valueOf(i))
//                    .assetEnName(String.valueOf(i))
//                    .currency("RMB")
//                    .unitPrice(new BigDecimal(i))
//                    .quantity(i)
//                    .costCenter(String.valueOf(i))
//                    .costCenterDepartment(String.valueOf(i))
//                    .costCenterName(String.valueOf(i))
//                    .vehicleModelRelated(i)
//                    .vehicleModel(String.valueOf(i))
//                    .lifeYear(String.valueOf(i))
//                    .lifePeriod(String.valueOf(i))
//                    .location(String.valueOf(i))
//                    .budgetApproved(i)
//                    .internalOrder(String.valueOf(i))
//                    .wbsElement(String.valueOf(i))
//                    .taxInternalOrder(String.valueOf(i))
//                    .wbsElementCost(String.valueOf(i))
//                    .fromNewco(i)
//                    .orignalAssetNumberOfNewco(i)
//                    .whetherSplit(i)
//                    .orignalAssetNumberOfSplit(i)
//                    .actionTime(LocalDateTime.now())
//                    .applicantId(String.valueOf(i))
//                    .applicant(String.valueOf(i))
//                    .email(String.valueOf(i))
//                    .build());
//        }
//
//        assetRepository.saveAll(list);
    }

}
