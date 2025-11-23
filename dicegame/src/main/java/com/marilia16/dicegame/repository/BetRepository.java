package com.marilia16.dicegame.repository;

import com.marilia16.dicegame.model.Bet;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByFinalizedFalse();
    List<Bet> findByFinalizedTrue();
}
