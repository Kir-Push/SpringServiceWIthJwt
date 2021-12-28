package com.mesh.testtask.service.impl;

import com.mesh.testtask.domain.entity.Profile;
import com.mesh.testtask.domain.entity.User;
import com.mesh.testtask.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AmountIncreaseService {

    private static final Logger LOG = LoggerFactory.getLogger(AmountIncreaseService.class);

    private final UserService userService;

    @Autowired
    public AmountIncreaseService(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(cron = "*/20 * * * * *")
    void amountInflate() {
        List<User> userList = userService.getAllUsers();
        BigDecimal hundredPercent = BigDecimal.valueOf(100);
        BigDecimal maxPercent = BigDecimal.valueOf(207); // 107 percent increase + 100 initial percent
        boolean cashIncreased = false;
        for (User user : userList) {
            Profile profile = user.getProfile();
            BigDecimal amount = profile.getAmount(); // init amount
            BigDecimal cash = profile.getCash() == null ? amount : profile.getCash();
            BigDecimal maxPossibleAmount = amount.divide(hundredPercent,2, RoundingMode.HALF_EVEN).multiply(maxPercent);

            if (maxPossibleAmount.compareTo(cash) <= 0) {
                continue;
            }

            cashIncreased = true;
            BigDecimal currentCash = cash.multiply(BigDecimal.valueOf(1.1));
            if (maxPossibleAmount.compareTo(currentCash) <= 0) {
                currentCash = maxPossibleAmount;
            }

            LOG.info("Cash increased to " + currentCash + ", for user: " + user.getId());
            profile.setCash(currentCash);
            user.setProfile(profile);
        }
        if (cashIncreased) {
            userService.saveBulkUsers(userList);
        }

    }
}
