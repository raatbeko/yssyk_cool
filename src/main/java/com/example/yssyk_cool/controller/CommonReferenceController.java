package com.example.yssyk_cool.controller;

import com.example.yssyk_cool.dto.commonReference.response.AreaResponse;
import com.example.yssyk_cool.dto.commonReference.response.CityResponse;
import com.example.yssyk_cool.service.CommonReferenceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/common-reference")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommonReferenceController {

    final CommonReferenceService commonReferenceService;

    @GetMapping("/get-cities")
    List<CityResponse> getCities(){
        return null;
    }

    @GetMapping("/get-areas")
    List<AreaResponse> getAreas(){
        return null;
    }

}
