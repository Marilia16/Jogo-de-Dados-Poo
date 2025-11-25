package com.marilia16.dicegame.service;

import com.marilia16.dicegame.model.User;
import com.marilia16.dicegame.model.UserValueHistory;
import com.marilia16.dicegame.repository.UserValueHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class UserValueHistoryService {

    private final UserValueHistoryRepository repo;

    public UserValueHistoryService(UserValueHistoryRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void registerValueChange(User user, BigDecimal oldValue, BigDecimal newValue, String reason) {

        UserValueHistory h = new UserValueHistory();
        h.setUser(user);
        h.setOldValue(oldValue);
        h.setNewValue(newValue);
        h.setReason(reason);
        h.setChangedAt(LocalDateTime.now());

        repo.save(h);
    }
}
