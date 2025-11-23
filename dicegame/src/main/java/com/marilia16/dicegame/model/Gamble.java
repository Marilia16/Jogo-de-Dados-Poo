package com.marilia16.dicegame.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gamble")
public class Gamble {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bet_id")
    private Bet bet;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer valueGuess;

    private Double valueMoney;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Bet getBet() { return bet; }
    public void setBet(Bet bet) { this.bet = bet; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getValueGuess() { return valueGuess; }
    public void setValueGuess(Integer valueGuess) { this.valueGuess = valueGuess; }

    public Double getValueMoney() { return valueMoney; }
    public void setValueMoney(Double valueMoney) { this.valueMoney = valueMoney; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
