package com.marilia16.dicegame.service;

import com.marilia16.dicegame.model.*;
import com.marilia16.dicegame.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class BetResultService {

    @Autowired
    private GambleRepository gambleRepository;

    @Autowired
    private BetResultRepository betResultRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserValueHistoryRepository userValueHistoryRepository;

    private final Random random = new Random();

    public void finalizeBet(Bet bet) {

        List<Gamble> gambles = gambleRepository.findByBet(bet);

        if (gambles.size() < 2) {
            throw new RuntimeException("A aposta exige pelo menos 2 jogadores.");
        }

        // Soma final dos dados (entre 2 e 12)
        int dice1 = random.nextInt(6) + 1;
        int dice2 = random.nextInt(6) + 1;
        int finalSum = dice1 + dice2;

        // Cria resultado
        BetResult result = new BetResult();
        result.setBet(bet);
        result.setFinalSum(finalSum);
        result.setCreatedAt(LocalDateTime.now());

        // Lista de vencedores
        List<User> winners = gambles.stream()
                .filter(g -> g.getValueGuess() == finalSum)
                .map(Gamble::getUser)
                .collect(Collectors.toList());

        result.setWinners(winners);

        // Valor total apostado
        double totalMoney = gambles.stream()
                .mapToDouble(Gamble::getValueMoney)
                .sum();

        // Se houver vencedores divide o prêmio
        double winAmount = winners.isEmpty() ? 0.0 : totalMoney / winners.size();
        result.setWinAmount(winAmount);

        // 🟢 Atualiza saldos dos usuários com histórico
        for (Gamble g : gambles) {

            User user = g.getUser();
            BigDecimal oldValue = user.getValueTotal();
            BigDecimal newValue;

            if (winners.contains(user)) {
                newValue = oldValue.add(BigDecimal.valueOf(winAmount));
            } else {
                newValue = oldValue.subtract(BigDecimal.valueOf(g.getValueMoney()));
            }

            user.setValueTotal(newValue);
            userRepository.save(user);

            // Histórico
            UserValueHistory history = new UserValueHistory();
            history.setUser(user);
            history.setOldValue(oldValue);
            history.setNewValue(newValue);
            history.setReason("Aposta #" + bet.getId());
            history.setChangedAt(LocalDateTime.now());
            userValueHistoryRepository.save(history);
        }

        // Atualiza lucro / prejuízo da aposta
        bet.setProfit(BigDecimal.valueOf(winAmount * winners.size()));
        bet.setLoss(BigDecimal.valueOf(totalMoney - (winAmount * winners.size())));
        bet.setFinalized(true);
        bet.setClosedAt(LocalDateTime.now());

        // Salva tudo
        betResultRepository.save(result);
    }
}
