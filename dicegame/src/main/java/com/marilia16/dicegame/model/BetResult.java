package com.marilia16.dicegame.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bet_result")
public class BetResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "bet_id", nullable = false, unique = true)
    private Bet bet;

    // Soma dos dados
    private Integer finalSum;

    // Valor que cada vencedor ganha
    private Double winAmount;

    // Data de criação
    private LocalDateTime createdAt = LocalDateTime.now();

    // Lista de vencedores
    @ManyToMany
    @JoinTable(
        name = "bet_result_winners",
        joinColumns = @JoinColumn(name = "bet_result_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> winners;

    // ========= GETTERS & SETTERS ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Bet getBet() { return bet; }
    public void setBet(Bet bet) { this.bet = bet; }

    public Integer getFinalSum() { return finalSum; }
    public void setFinalSum(Integer finalSum) { this.finalSum = finalSum; }

    public Double getWinAmount() { return winAmount; }
    public void setWinAmount(Double winAmount) { this.winAmount = winAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<User> getWinners() { return winners; }
    public void setWinners(List<User> winners) { this.winners = winners; }
}
