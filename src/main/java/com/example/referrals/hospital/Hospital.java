package com.example.referrals.hospital;

import com.example.referrals.common.model.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "hospitals")
public class Hospital extends AuditableEntity {

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    // we mzee
    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @Column(length = 120)
    private String contactEmail;

    @Column(length = 40)
    private String contactPhone;

    @Column(length = 255)
    private String address;

    @Column(nullable = false)
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HospitalRegistrationStatus registrationStatus = HospitalRegistrationStatus.APPROVED;

    @Column(name = "registration_admin_id")
    private Long registrationAdminId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public HospitalRegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(HospitalRegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public Long getRegistrationAdminId() {
        return registrationAdminId;
    }

    public void setRegistrationAdminId(Long registrationAdminId) {
        this.registrationAdminId = registrationAdminId;
    }
}
