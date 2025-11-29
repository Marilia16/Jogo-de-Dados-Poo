package com.marilia16.dicegame.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_value_history")
public class UserValueHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "old_value", nullable = false)
    private BigDecimal oldValue;

    @Column(name = "new_value", nullable = false)
    private BigDecimal newValue;

    @Column(name = "diff")
    private BigDecimal diff; 

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    // GETTERS E SETTERS

    @PrePersist
    @PreUpdate
    private void calculateDiff() {
        if (oldValue != null && newValue != null) {
            this.diff = newValue.subtract(oldValue);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BigDecimal getOldValue() { return oldValue; }
    public void setOldValue(BigDecimal oldValue) { this.oldValue = oldValue; }

    public BigDecimal getNewValue() { return newValue; }
    public void setNewValue(BigDecimal newValue) { this.newValue = newValue; }

    public BigDecimal getDiff() { return diff; }  // << GETTER
    public void setDiff(BigDecimal diff) { this.diff = diff; } // << SETTER

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}
