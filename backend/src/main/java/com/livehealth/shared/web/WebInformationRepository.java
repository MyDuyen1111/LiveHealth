package com.livehealth.shared.web;

import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class WebInformationRepository extends BaseRepository<WebInformation, UUID> {

    public WebInformationRepository() {
        super(WebInformation.class);
    }
}
