package com.example.referrals.hospital;

import com.example.referrals.common.model.RoleName;
import com.example.referrals.user.AppUser;
import com.example.referrals.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Set;

@Service
@Transactional
public class HospitalRegistrationService {

    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public HospitalRegistrationService(HospitalRepository hospitalRepository,
                                       UserRepository userRepository,
                                       PasswordEncoder passwordEncoder) {
        this.hospitalRepository = hospitalRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(HospitalRegistrationForm form) {
        String hospitalName = normalize(form.getHospitalName());
        String hospitalCode = normalize(form.getHospitalCode()).toUpperCase();
        String adminEmail = normalize(form.getAdminEmail());

        if (hospitalRepository.existsByNameIgnoreCase(hospitalName)) {
            throw new IllegalArgumentException("A hospital with this name is already registered.");
        }
        if (hospitalRepository.existsByCodeIgnoreCase(hospitalCode)) {
            throw new IllegalArgumentException("This hospital code is already registered.");
        }
        if (userRepository.existsByEmailIgnoreCase(adminEmail)) {
            throw new IllegalArgumentException("An account with this administrator email already exists.");
        }

        Hospital hospital = new Hospital();
        hospital.setName(hospitalName);
        hospital.setCode(hospitalCode);
        hospital.setContactEmail(normalizeNullable(form.getContactEmail()));
        hospital.setContactPhone(normalizeNullable(form.getContactPhone()));
        hospital.setAddress(normalizeNullable(form.getAddress()));
        hospital.setEnabled(false);
        hospital.setRegistrationStatus(HospitalRegistrationStatus.PENDING);
        hospital = hospitalRepository.save(hospital);

        AppUser admin = new AppUser();
        admin.setFirstName(normalize(form.getAdminFirstName()));
        admin.setLastName(normalize(form.getAdminLastName()));
        admin.setEmail(adminEmail);
        admin.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        admin.setHospital(hospital);
        admin.setEnabled(false);
        admin.setRoles(Set.of(RoleName.HOSPITAL_ADMIN));
        admin = userRepository.save(admin);

        hospital.setRegistrationAdminId(admin.getId());
    }

    private String normalize(String value) { return value.trim(); }

    private String normalizeNullable(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
