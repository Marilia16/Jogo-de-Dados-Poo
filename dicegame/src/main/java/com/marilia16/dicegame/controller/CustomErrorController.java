package com.marilia16.dicegame.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        Object status = request.getAttribute("jakarta.servlet.error.status_code");

        Exception exception = (Exception) request.getAttribute("jakarta.servlet.error.exception");

        String message = "Ocorreu um erro inesperado.";

        if (exception != null && exception.getMessage() != null) {
            message = exception.getMessage();
        }

        model.addAttribute("status", status != null ? status.toString() : "Erro");
        model.addAttribute("message", message);

        return "custom_error";
    }
}
