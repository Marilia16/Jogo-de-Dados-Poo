package com.marilia16.dicegame.repository;

import com.marilia16.dicegame.model.Gamble;
import com.marilia16.dicegame.model.Bet;
import com.marilia16.dicegame.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GambleRepository extends JpaRepository<Gamble, Long> {
    List<Gamble> findByBet(Bet bet);
    Optional<Gamble> findByBetAndUser(Bet bet, User user);
}
