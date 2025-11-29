package com.marilia16.dicegame.controller;

import com.marilia16.dicegame.model.Bet;
import com.marilia16.dicegame.model.BetResult;
import com.marilia16.dicegame.model.Gamble;
import com.marilia16.dicegame.model.User;
import com.marilia16.dicegame.repository.*;
import com.marilia16.dicegame.service.BetResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class BetController {

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private GambleRepository gambleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BetResultRepository betResultRepository;

    @Autowired
    private BetResultService betResultService;

    @GetMapping("/bets")
    public String bets(Model model) {
        model.addAttribute("openBets", betRepository.findByFinalizedFalse());
        model.addAttribute("closedBets", betRepository.findByFinalizedTrue());
        return "bets";
    }

    @GetMapping("/bets/new")
    public String showCreateBetForm(Model model) {
        model.addAttribute("bet", new Bet());
        return "create_bet";
    }

    @PostMapping("/bets")
    public String createBet(@RequestParam("name") String name,
                            @AuthenticationPrincipal UserDetails currentUser) {

        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Bet bet = new Bet();
        bet.setName(name);
        bet.setUser(user);
        bet.setCreatedAt(LocalDateTime.now());
        bet.setFinalized(false);

        betRepository.save(bet);
        return "redirect:/bets";
    }

    @GetMapping("/bets/{betId}")
    public String betDetails(@PathVariable Long betId,
                             @AuthenticationPrincipal UserDetails currentUser,
                             Model model) {

        Bet bet = betRepository.findById(betId)
                .orElseThrow(() -> new RuntimeException("Aposta não encontrada"));

        List<Gamble> gambles = gambleRepository.findByBet(bet);
        BetResult betResult = betResultRepository.findByBet(bet).orElse(null);

        User user = null;
        if (currentUser != null) {
            user = userRepository.findByEmail(currentUser.getUsername()).orElse(null);
        }

        model.addAttribute("bet", bet);
        model.addAttribute("gambles", gambles);
        model.addAttribute("betResult", betResult);
        model.addAttribute("currentUserEntity", user);

        return "bet_detail";
    }

    @PostMapping("/bets/{betId}/finalize")
public String finalizeBet(@PathVariable Long betId,
                          @AuthenticationPrincipal UserDetails currentUser) {

    Bet bet = betRepository.findById(betId)
            .orElseThrow(() -> new RuntimeException("Aposta não encontrada."));

    if (!bet.getUser().getEmail().equals(currentUser.getUsername())) {
        throw new AccessDeniedException("Apenas o criador da aposta pode finalizá-la.");
    }

    if (Boolean.TRUE.equals(bet.getFinalized())) {
        return "redirect:/bets/" + betId + "?error=alreadyFinalized";
    }

    try {
        betResultService.finalizeBet(bet);

    } catch (RuntimeException e) {

        String msg = e.getMessage().toLowerCase();

        // ERRO ESPECÍFICO DE POUCOS PARTICIPANTES
        if (msg.contains("mínimo 2 participantes") ||
            msg.contains("pelo menos 2 jogadores") ||
            msg.contains("2 participantes")) {

            return "redirect:/bets/" + betId + "?error=minimum_participants";
        }

        // ERRO DESCONHECIDO
        return "redirect:/bets/" + betId + "?error=unknown";
    }

    return "redirect:/bets/" + betId + "?success=finalized";
}


    @GetMapping("/bets/{betId}/edit")
    public String editBet(@PathVariable Long betId,
                          @AuthenticationPrincipal UserDetails currentUser,
                          Model model) {

        Bet bet = betRepository.findById(betId)
                .orElseThrow(() -> new RuntimeException("Aposta não encontrada"));

        if (!bet.getUser().getEmail().equals(currentUser.getUsername())) {
            throw new AccessDeniedException("Você não pode editar esta aposta.");
        }

        model.addAttribute("bet", bet);
        return "edit";
    }

    @PostMapping("/bets/{betId}/edit")
    public String updateBet(@PathVariable Long betId,
                            @RequestParam("name") String name,
                            @AuthenticationPrincipal UserDetails currentUser) {

        Bet bet = betRepository.findById(betId)
                .orElseThrow(() -> new RuntimeException("Aposta não encontrada"));

        if (!bet.getUser().getEmail().equals(currentUser.getUsername())) {
            throw new AccessDeniedException("Você não pode editar esta aposta.");
        }

        bet.setName(name);
        betRepository.save(bet);

        return "redirect:/bets";
    }
   @PostMapping("/bets/{betId}/delete")
        public String deleteBet(@PathVariable Long betId, @AuthenticationPrincipal UserDetails currentUser) {

    Bet bet = betRepository.findById(betId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aposta não encontrada"));

    
    if (!bet.getUser().getEmail().equals(currentUser.getUsername())) {
        throw new AccessDeniedException("Você não pode deletar esta aposta.");
    }

    betRepository.delete(bet);

    return "redirect:/bets?deleted=true";
}

}
