package com.livehealth.shared.web;

import com.livehealth.shared.web.dto.request.webinfo.WebInformationRequestDto;
import com.livehealth.shared.web.dto.response.webinfo.WebInformationResponseDto;
import com.livehealth.shared.base.MultipartFile;

public interface WebInformationService {

    WebInformationResponseDto getWebInformation();

    WebInformationResponseDto updateWebInformation(WebInformationRequestDto request);

    WebInformationResponseDto uploadLogo(MultipartFile file);
}
