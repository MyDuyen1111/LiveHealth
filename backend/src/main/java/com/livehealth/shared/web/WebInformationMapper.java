package com.livehealth.shared.web;

import com.livehealth.shared.web.dto.request.webinfo.WebInformationRequestDto;
import com.livehealth.shared.web.dto.response.webinfo.WebInformationResponseDto;
import com.livehealth.shared.web.WebInformation;
import org.mapstruct.*;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface WebInformationMapper {

    WebInformationResponseDto toResponseDto(WebInformation webInfo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "logoUrl", ignore = true)
    void updateEntityFromDto(WebInformationRequestDto request, @MappingTarget WebInformation webInfo);
}
