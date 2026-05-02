package com.livehealth.user;

import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AddressRepository extends BaseRepository<Address, UUID> {

    public AddressRepository() {
        super(Address.class);
    }

    public Optional<Address> findByCountryAndStateAndStreetAddressAndCompanyNameAndZipCode(
            String country, String state, String streetAddress, String companyName, int zipCode) {
        return em.createQuery(
                "SELECT a FROM Address a WHERE a.country = :country AND a.state = :state AND a.streetAddress = :streetAddress " +
                "AND (:companyName IS NULL AND a.companyName IS NULL OR a.companyName = :companyName) AND a.zipCode = :zipCode",
                Address.class
        )
        .setParameter("country", country)
        .setParameter("state", state)
        .setParameter("streetAddress", streetAddress)
        .setParameter("companyName", companyName)
        .setParameter("zipCode", zipCode)
        .getResultList().stream()
        .findFirst();
    }
}
