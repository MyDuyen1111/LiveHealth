package com.livehealth.shared.web;

import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class ClientTestimonialRepository extends BaseRepository<ClientTestimonial, UUID> {

    public ClientTestimonialRepository() {
        super(ClientTestimonial.class);
    }
}
