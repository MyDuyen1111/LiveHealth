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

    public long countPendingMessages() {
        return em.createQuery("SELECT COUNT(c) FROM ContactMessage c WHERE c.status = 'PENDING'", Long.class)
                .getSingleResult();
    }

    public void markMyMessagesAsRead(String email) {
        em.createQuery("UPDATE ContactMessage c SET c.userRead = true WHERE c.email = :email AND c.status = 'REPLIED'")
                .setParameter("email", email)
                .executeUpdate();
    }

    public long countUnreadRepliedMessages(String email) {
        return em.createQuery(
                "SELECT COUNT(c) FROM ContactMessage c WHERE c.email = :email AND c.status = 'REPLIED' AND c.userRead = false", 
                Long.class
        )
        .setParameter("email", email)
        .getSingleResult();
    }
}
