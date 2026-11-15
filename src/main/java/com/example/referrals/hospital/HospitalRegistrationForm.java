package com.example.referrals.hospital;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class HospitalRegistrationForm {

    @NotBlank(message = "Hospital name is required")
    @Size(max = 120)
    private String hospitalName;

    @NotBlank(message = "Hospital code is required")
    @Size(max = 40)
    private String hospitalCode;

    @Email(message = "Enter a valid hospital contact email address")
    @Size(max = 120)
    private String contactEmail;

    @Size(max = 40)
    private String contactPhone;

    @Size(max = 255)
    private String address;

    @NotBlank(message = "Administrator first name is required")
    @Size(max = 80)
    private String adminFirstName;

    @NotBlank(message = "Administrator last name is required")
    @Size(max = 80)
    private String adminLastName;

    @NotBlank(message = "Administrator email is required")
    @Email(message = "Enter a valid administrator email address")
    @Size(max = 150)
    private String adminEmail;

    @NotBlank(message = "A password is required")
    @Size(min = 8, max = 120, message = "Password must be between 8 and 120 characters")
    private String password;

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
    public String getHospitalCode() { return hospitalCode; }
    public void setHospitalCode(String hospitalCode) { this.hospitalCode = hospitalCode; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getAdminFirstName() { return adminFirstName; }
    public void setAdminFirstName(String adminFirstName) { this.adminFirstName = adminFirstName; }
    public String getAdminLastName() { return adminLastName; }
    public void setAdminLastName(String adminLastName) { this.adminLastName = adminLastName; }
    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
