package com.marilia16.dicegame.config;

import com.marilia16.dicegame.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addGlobalAttributes(@AuthenticationPrincipal UserDetails userDetails,
                                    HttpServletRequest request,
                                    Model model) {

        if (userDetails != null) {
            userRepository.findByEmail(userDetails.getUsername())
                    .ifPresent(user -> model.addAttribute("currentUserEntity", user));
        }
        model.addAttribute("currentPath", request.getRequestURI());
    }
}
