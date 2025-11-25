package com.marilia16.dicegame.repository;

import com.marilia16.dicegame.model.UserValueHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserValueHistoryRepository extends JpaRepository<UserValueHistory, Long> {

    List<UserValueHistory> findAllByUserIdOrderByChangedAtDesc(Long userId);

}
