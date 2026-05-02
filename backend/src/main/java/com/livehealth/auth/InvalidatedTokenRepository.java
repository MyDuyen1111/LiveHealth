package com.livehealth.auth;

import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Date;

@ApplicationScoped
public class InvalidatedTokenRepository extends BaseRepository<InvalidatedToken, String> {

    public InvalidatedTokenRepository() {
        super(InvalidatedToken.class);
    }

    @jakarta.transaction.Transactional
    public void deleteByExpiryTimeBefore(Date expiryTimeBefore) {
        em.createQuery("DELETE FROM InvalidatedToken t WHERE t.expiryTime < :expiryTime")
                .setParameter("expiryTime", expiryTimeBefore)
                .executeUpdate();
    }
}
