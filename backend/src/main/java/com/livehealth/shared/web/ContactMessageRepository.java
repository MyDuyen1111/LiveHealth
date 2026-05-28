package com.livehealth.shared.web;

import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class ContactMessageRepository extends BaseRepository<ContactMessage, UUID> {

    public ContactMessageRepository() {
        super(ContactMessage.class);
    }
}
