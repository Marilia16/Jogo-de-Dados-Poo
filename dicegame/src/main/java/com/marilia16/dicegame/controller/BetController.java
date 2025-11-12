package com.marilia16.dicegame.controller;

import com.marilia16.dicegame.model.Bet;
import com.marilia16.dicegame.model.User;
import com.marilia16.dicegame.repository.BetRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class BetController {

    @Autowired
    private BetRepository betRepository;

    @GetMapping("/bets")
    public String bets(Model model, HttpServletRequest request) {
        model.addAttribute("bets", betRepository.findAll());
        model.addAttribute("currentPath", request.getRequestURI());
        return "bets";
    }

    @PostMapping("/bets")
    public String createBet(@RequestParam("name") String name) {
        Bet bet = new Bet();
        bet.setName(name);

        // ⚠️ futuramente trocaremos isso pelo usuário logado
        User user = new User();
        user.setId(1L); // Exemplo fixo — depois você conecta com login real
        bet.setUser(user);

        bet.setCreatedAt(LocalDateTime.now());
        betRepository.save(bet);

        return "redirect:/bets";
    }
}
