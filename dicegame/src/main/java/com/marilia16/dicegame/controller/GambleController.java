package com.marilia16.dicegame.controller;

import com.marilia16.dicegame.model.Bet;
import com.marilia16.dicegame.model.Gamble;
import com.marilia16.dicegame.model.User;
import com.marilia16.dicegame.repository.BetRepository;
import com.marilia16.dicegame.repository.GambleRepository;
import com.marilia16.dicegame.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/gamble")
public class GambleController {

    @Autowired
    private GambleRepository gambleRepository;

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{betId}")
    public String viewBet(@PathVariable Long betId, Model model) {
        Bet bet = betRepository.findById(betId)
                .orElseThrow(() -> new RuntimeException("Aposta não encontrada"));

        List<Gamble> gambles = gambleRepository.findByBet(bet);

        model.addAttribute("bet", bet);
        model.addAttribute("gambles", gambles);

        return "bet_detail";
    }

    @PostMapping("/{betId}/lance")
    public String placeGamble(@PathVariable Long betId,
                              @RequestParam("valueGuess") Integer valueGuess,
                              @RequestParam("valueMoney") Double valueMoney,
                              @AuthenticationPrincipal UserDetails currentUser) {

        Bet bet = betRepository.findById(betId)
                .orElseThrow(() -> new RuntimeException("Aposta não encontrada"));

        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (valueGuess < 2 || valueGuess > 12)
            throw new RuntimeException("Palpite deve estar entre 2 e 12");

        if (valueMoney <= 0)
            throw new RuntimeException("Valor da aposta deve ser maior que zero");

        if (gambleRepository.findByBetAndUser(bet, user).isPresent())
            throw new RuntimeException("Usuário já participou dessa aposta");

        Gamble gamble = new Gamble();
        gamble.setBet(bet);
        gamble.setUser(user);
        gamble.setValueGuess(valueGuess);
        gamble.setValueMoney(valueMoney);
        gamble.setCreatedAt(LocalDateTime.now());

        gambleRepository.save(gamble);

        return "redirect:/gamble/" + betId;
    }
}
