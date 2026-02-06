package uz.sqb.joyda.carddeliveryservice.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.sqb.joyda.carddeliveryservice.annotation.validation.ValidDistrictId;
import uz.sqb.joyda.carddeliveryservice.annotation.validation.ValidNeighborhoodId;
import uz.sqb.joyda.carddeliveryservice.annotation.validation.ValidRegionId;
import uz.sqb.joyda.carddeliveryservice.payload.address.AddressResponseToFront;
import uz.sqb.joyda.carddeliveryservice.payload.address.DistrictResponse;
import uz.sqb.joyda.carddeliveryservice.payload.address.NeighborhoodResponse;
import uz.sqb.joyda.carddeliveryservice.payload.address.RegionResponseV2;
import uz.sqb.joyda.carddeliveryservice.service.DistrictService;
import uz.sqb.joyda.carddeliveryservice.service.NeighborhoodService;
import uz.sqb.joyda.carddeliveryservice.service.RegionService;
import uz.sqb.joyda.commons.pojolibrary.payload.base.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/card-delivery/address")
@RequiredArgsConstructor
public class AddressController {
    private final RegionService regionService;
    private final DistrictService districtService;
    private final NeighborhoodService neighborhoodService;

    // 1. region api
    @GetMapping("/get-all-regions")
    public BaseResponse<AddressResponseToFront> getAllRegions(){
        return BaseResponse.ok(regionService.findAll());
    }

    @GetMapping("/get-region-by-id/{regionId}")
    public BaseResponse<RegionResponseV2> getRegionById(@PathVariable @NotNull @ValidRegionId Long regionId){
        return BaseResponse.ok(regionService.findById(regionId));
    }

    // 2. district api

// tuman data lari juda ko`pligi sababli bu API dan foydalanish tavsiya etilmaydi. tavsiya -> pageable dan foydalaning
//    @GetMapping("/get-all-districts")
//    public BaseResponse<List<DistrictResponse>> getAllDistricts(){
//        return BaseResponse.ok(districtService.findAll());
//    }

    @GetMapping("/get-all-districts")
    public BaseResponse<Page<DistrictResponse>> getAllDistricts(
            @PageableDefault(size = 20, sort = "name.uz") Pageable pageable
    ) {
        Page<DistrictResponse> page = districtService.findAll(pageable);
        return BaseResponse.ok(page);
    }

    @GetMapping("/get-district-by-id/{districtId}")
    public BaseResponse<DistrictResponse> getDistrictById(@PathVariable @NotNull @ValidDistrictId Long districtId){
        return BaseResponse.ok(districtService.findById(districtId));
    }

    @GetMapping("/get-districts-by-region-id/{regionId}")
    public BaseResponse<AddressResponseToFront> getDistrictsByRegionId(@PathVariable @NotNull @ValidRegionId Long regionId){
        return BaseResponse.ok(districtService.findAllByRegionId(regionId));
    }

    // 3. neighborhood api

    // mahalla data lari juda ko`pligi sababli bu API dan foydalanish tavsiya etilmaydi. tavsiya -> pageable dan foydalaning
//    @GetMapping("/get-all-neighborhoods")
//    public BaseResponse<List<NeighborhoodResponse>> getAllNeighborhoods(){
//        return BaseResponse.ok(neighborhoodService.findAll());
//    }

    @GetMapping("/get-all-neighborhoods")
    public BaseResponse<Page<NeighborhoodResponse>> getAllNeighborhoods(
            @PageableDefault(size = 20, sort = "name.uz") Pageable pageable
    ) {
        Page<NeighborhoodResponse> page = neighborhoodService.findAll(pageable);
        return BaseResponse.ok(page);
    }

    @GetMapping("/get-neighborhood-by-id/{neighborhoodId}")
    public BaseResponse<NeighborhoodResponse> getNeighborhoodById(@PathVariable @NotNull @ValidNeighborhoodId Long neighborhoodId){
        return BaseResponse.ok(neighborhoodService.findById(neighborhoodId));
    }

    @GetMapping("/get-neighborhoods-full-data-by-district-id/{districtId}")
    public BaseResponse<List<NeighborhoodResponse>> getNeighborhoodsFullDataByDistrictId(@PathVariable @NotNull @ValidDistrictId Long districtId){
        return BaseResponse.ok(neighborhoodService.findAllByDistrictId(districtId));
    }

    @GetMapping("/get-neighborhoods-by-district-id/{districtId}")
    public BaseResponse<AddressResponseToFront> getNeighborhoodsByDistrictId(@PathVariable @NotNull @ValidDistrictId Long districtId){
        return BaseResponse.ok(neighborhoodService.findAllNeighborhoodsNamesByDistrictId(districtId));
    }

}
