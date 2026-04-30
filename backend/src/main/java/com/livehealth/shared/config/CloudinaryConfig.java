package com.livehealth.shared.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class CloudinaryConfig {

    @Inject
    @ConfigProperty(name = "cloudinary.cloud_name")
    String CLOUD_NAME;

    @Inject
    @ConfigProperty(name = "cloudinary.api_key")
    String API_KEY;

    @Inject
    @ConfigProperty(name = "cloudinary.api_secret")
    String API_SECRET;

    @Produces
    @ApplicationScoped
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CLOUD_NAME,
                "api_key", API_KEY,
                "api_secret", API_SECRET,
                "secure", true));
    }
}
