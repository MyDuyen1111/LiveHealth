package com.livehealth.shared.web;

import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class ProfessionalMemberRepository extends BaseRepository<ProfessionalMember, UUID> {

    public ProfessionalMemberRepository() {
        super(ProfessionalMember.class);
    }
}
