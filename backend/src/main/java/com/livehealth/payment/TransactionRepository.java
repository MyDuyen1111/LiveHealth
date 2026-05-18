package com.livehealth.payment;

import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TransactionRepository extends BaseRepository<Transaction, UUID> {

    public TransactionRepository() {
        super(Transaction.class);
    }

    public Optional<Transaction> findByVnpTxnRef(String vnpTxnRef) {
        return em.createQuery("SELECT t FROM Transaction t WHERE t.vnpTxnRef = :vnpTxnRef", Transaction.class)
                .setParameter("vnpTxnRef", vnpTxnRef)
                .getResultList().stream()
                .findFirst();
    }
}
