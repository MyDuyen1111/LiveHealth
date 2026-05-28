package com.livehealth.shared.web;

import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class ContactMessageRepository extends BaseRepository<ContactMessage, UUID> {

    public ContactMessageRepository() {
        super(ContactMessage.class);
    }

    public java.util.List<ContactMessage> findByEmailOrderByCreatedAtDesc(String email) {
        return em.createQuery(
                "SELECT c FROM ContactMessage c WHERE c.email = :email ORDER BY c.createdAt DESC", 
                ContactMessage.class
        )
        .setParameter("email", email)
        .getResultList();
    }
}
