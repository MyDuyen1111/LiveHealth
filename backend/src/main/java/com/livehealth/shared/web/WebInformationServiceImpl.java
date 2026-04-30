package com.livehealth.shared.web;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.livehealth.shared.constant.ErrorMessage;
import com.livehealth.shared.web.dto.request.webinfo.WebInformationRequestDto;
import com.livehealth.shared.web.dto.response.webinfo.WebInformationResponseDto;
import com.livehealth.shared.web.WebInformation;
import com.livehealth.shared.web.WebInformationMapper;
import com.livehealth.shared.exception.VsException;
import com.livehealth.shared.web.WebInformationRepository;
import com.livehealth.shared.web.WebInformationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.livehealth.shared.base.HttpStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import com.livehealth.shared.base.MultipartFile;

import java.util.List;
import java.util.Map;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebInformationServiceImpl implements WebInformationService {

    WebInformationRepository webInformationRepository;
    WebInformationMapper webInformationMapper;
    Cloudinary cloudinary;

    @Override
    public WebInformationResponseDto getWebInformation() {
        WebInformation webInfo = getOrCreateDefaultWebInfo();
        return webInformationMapper.toResponseDto(webInfo);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public WebInformationResponseDto updateWebInformation(WebInformationRequestDto request) {
        WebInformation webInfo = getOrCreateDefaultWebInfo();

        webInformationMapper.updateEntityFromDto(request, webInfo);

        return webInformationMapper.toResponseDto(webInformationRepository.save(webInfo));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public WebInformationResponseDto uploadLogo(MultipartFile file) {
        WebInformation webInfo = getOrCreateDefaultWebInfo();

        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) result.get("secure_url");
            webInfo.setLogoUrl(imageUrl);
        } catch (Exception e) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.ERR_UPLOAD_IMAGE_FAIL);
        }

        return webInformationMapper.toResponseDto(webInformationRepository.save(webInfo));
    }

    private WebInformation getOrCreateDefaultWebInfo() {
        List<WebInformation> infos = webInformationRepository.findAll();
        if (infos.isEmpty()) {
            WebInformation newInfo = WebInformation.builder()
                .logoUrl("")
                .yearOfHardWork(0)
                .happyCustomers(0)
                .qualifiedTeamMembers(0)
                .monthlyOrders(0)
                .companyName("Công Ty TNHH")
                .slogan("Chăm sóc sức khỏe")
                .phone("0123456789")
                .email("contact@livehealth.com")
                .address("Vietnam")
                .copyrightText("Copyright © 2026")
                .build();
            return webInformationRepository.save(newInfo);
        }
        // Always return the first/only record
        return infos.get(0);
    }
}
