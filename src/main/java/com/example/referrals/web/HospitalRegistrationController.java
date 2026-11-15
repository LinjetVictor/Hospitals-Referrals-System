package com.example.referrals.web;

import com.example.referrals.hospital.HospitalRegistrationForm;
import com.example.referrals.hospital.HospitalRegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/hospital-registration")
public class HospitalRegistrationController {

    private final HospitalRegistrationService registrationService;

    public HospitalRegistrationController(HospitalRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("registrationForm", new HospitalRegistrationForm());
        return "auth/hospital-registration";
    }

    @PostMapping
    public String register(@Valid @ModelAttribute("registrationForm") HospitalRegistrationForm registrationForm,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/hospital-registration";
        }
        try {
            registrationService.register(registrationForm);
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("registration", ex.getMessage());
            return "auth/hospital-registration";
        }
        redirectAttributes.addFlashAttribute("message", "Your hospital registration has been submitted. You can sign in once it has been approved.");
        return "redirect:/login";
    }
}
