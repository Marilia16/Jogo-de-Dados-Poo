package com.marilia16.dicegame.controller;

import com.marilia16.dicegame.model.User;
import com.marilia16.dicegame.model.UserValueHistory;
import com.marilia16.dicegame.repository.UserRepository;
import com.marilia16.dicegame.repository.UserValueHistoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ValuesController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserValueHistoryRepository historyRepository;

    @GetMapping("/values")
    public String values(Model model, Principal principal) {

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // usa seu método correto
        List<UserValueHistory> historyList =
                historyRepository.findAllByUserIdOrderByChangedAtDesc(user.getId());

        // cria objetos prontos para a view
        List<Map<String, Object>> historyView = historyList.stream().map(h -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", h.getId());
            map.put("oldValue", h.getOldValue());
            map.put("newValue", h.getNewValue());
            map.put("reason", h.getReason());
            map.put("changedAt", h.getChangedAt());
            map.put("diff", h.getDiff());
            return map;
        }).toList();

        model.addAttribute("user", user);
        model.addAttribute("history", historyView);

        return "values";
    }
}
