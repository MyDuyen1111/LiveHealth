package com.livehealth.shared.helper;

import com.livehealth.user.dto.request.user.personalInformation.UpdatePersonalInformationRequestDto;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PersonalInformationHelper {

    public UpdatePersonalInformationRequestDto handleEmptyStrings(
            UpdatePersonalInformationRequestDto personalInformation) {
        if (personalInformation == null) {
            return null;
        }

        if (personalInformation.getFirstName() != null &&
                personalInformation.getFirstName().trim().isEmpty()) {
            personalInformation.setFirstName(null);
        }

        if (personalInformation.getLastName() != null &&
                personalInformation.getLastName().trim().isEmpty()) {
            personalInformation.setLastName(null);
        }

        if (personalInformation.getPhone() != null &&
                personalInformation.getPhone().trim().isEmpty()) {
            personalInformation.setPhone(null);
        }

        if (personalInformation.getAddress() != null) {
            if (personalInformation.getAddress().getCountry() != null &&
                    personalInformation.getAddress().getCountry().trim().isEmpty()) {
                personalInformation.getAddress().setCountry(null);
            }

            if (personalInformation.getAddress().getState() != null &&
                    personalInformation.getAddress().getState().trim().isEmpty()) {
                personalInformation.getAddress().setState(null);
            }

            if (personalInformation.getAddress().getStreetAddress() != null &&
                    personalInformation.getAddress().getStreetAddress().trim().isEmpty()) {
                personalInformation.getAddress().setStreetAddress(null);
            }

            if (personalInformation.getAddress().getCompanyName() != null &&
                    personalInformation.getAddress().getCompanyName().trim().isEmpty()) {
                personalInformation.getAddress().setCompanyName(null);
            }

            if (personalInformation.getAddress().getCountry() == null &&
                    personalInformation.getAddress().getState() == null &&
                    personalInformation.getAddress().getStreetAddress() == null &&
                    personalInformation.getAddress().getCompanyName() == null) {
                personalInformation.setAddress(null);
            }
        }

        return personalInformation;
    }

}
