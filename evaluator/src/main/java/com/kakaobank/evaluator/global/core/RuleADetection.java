package com.kakaobank.evaluator.global.core;

import com.kakaobank.evaluator.domain.account.domain.AccountAgg;
import com.kakaobank.evaluator.domain.deposit.domain.DepositAgg;
import com.kakaobank.evaluator.domain.join.domain.JoinAgg;
import com.kakaobank.evaluator.domain.transfer.domain.TransferAgg;
import com.kakaobank.evaluator.domain.withdraw.domain.WithdrawAgg;
import com.kakaobank.evaluator.global.Detection;
import com.kakaobank.evaluator.global.Entity;
import com.kakaobank.evaluator.global.Message;
import com.kakaobank.evaluator.global.WithLogger;
import com.kakaobank.evaluator.global.utils.DatabaseUtils;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RuleADetection implements Detection, WithLogger {
    private final JoinAgg joinAgg;
    private final AccountAgg accountAgg;
    private final DepositAgg depositAgg;
    private final WithdrawAgg withdrawAgg;
    private final TransferAgg transferAgg;

    private RuleADetection() {
        joinAgg = new JoinAgg(DatabaseUtils.getConnection());
        accountAgg = new AccountAgg(DatabaseUtils.getConnection());
        depositAgg = new DepositAgg(DatabaseUtils.getConnection());
        withdrawAgg = new WithdrawAgg(DatabaseUtils.getConnection());
        transferAgg = new TransferAgg(DatabaseUtils.getConnection());
    }

    private static class SingletonHelper {
        private static final RuleADetection SINGLETON = new RuleADetection();
    }

    public static RuleADetection getInstance(){
        return RuleADetection.SingletonHelper.SINGLETON;
    }

    @Override
    public FraudDetection detect(String customerNumber) throws ParseException, SQLException {
        JoinAgg customer = (JoinAgg) joinAgg.read(customerNumber).get(0);
        if(ageCondition(customer.getCustomerBirth())) {
            List<Entity> accounts = accountAgg.read(customerNumber);
            long currentTime = System.currentTimeMillis();
            String from = String.valueOf(currentTime - 7200000);
            String to = String.valueOf(currentTime);

            List<Message> results = new ArrayList<>();
            for(Entity account : accounts) {
                AccountAgg target = (AccountAgg) account;
                if (accountCondition(target, currentTime)) {
                    List<Entity> deposits = depositAgg.read(customerNumber, target.getNumber());
                    long totalDepositAmount = getTotalDepositAmount(deposits);
                    if (totalDepositAmount >= 1000000) {
                        logger.debug("The total deposit amount is more than 1 million won. Amount: {}", totalDepositAmount);

                        String accountNumber = target.getNumber();
                        long totalWithdrawAmount = getTotalTransferAmount(customerNumber, accountNumber, from, to)
                                + getTotalWithdrawAmount(customerNumber, accountNumber, from, to);
                        logger.debug("Total amount withdrawn within 2 hours: {}", totalWithdrawAmount);

                        long balance = totalDepositAmount - totalWithdrawAmount;
                        if (balance <= 10000) {
                            logger.debug("{} - {} = {}(balance)", totalDepositAmount, totalWithdrawAmount, balance);

                            results.add(new RuleAMessage(
                                    customerNumber, customer.getCustomerName(),
                                    customer.getCustomerBirth(), accountNumber,
                                    target.getTransactionTime(), String.valueOf(totalDepositAmount),
                                    String.valueOf(totalWithdrawAmount)));
                        }
                    }
                }
            }

            if(!results.isEmpty()) return new FraudDetection(results);
            else return null;
        }
        return null;
    }

    private boolean ageCondition(String birth) throws ParseException {
        Date date = new SimpleDateFormat("yyyyMMdd").parse(birth);
        Calendar calendar = Calendar.getInstance();

        int currentYear  = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay   = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTime(date);
        int age = currentYear - calendar.get(Calendar.YEAR);
        int birthMonth = calendar.get(Calendar.MONTH) + 1;
        int birthDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (birthMonth * 100 + birthDay > currentMonth * 100 + currentDay) {
            age--;
        }

        boolean flag = age >= 60;
        logger.debug("Over 60 years old. Age: {}", age);

        return flag;
    }

    private boolean accountCondition(AccountAgg target, long currentTime) {
        String transactionTime = target.getTransactionTime();

        boolean result = (currentTime - Long.parseLong(transactionTime)) < 172800000;
        logger.debug("The account opened in 48 hours. Opened time: {}", transactionTime);

        return result;
    }

    private long getTotalDepositAmount(List<Entity> deposits) {
        long totalDepositAmount = 0;
        for (Entity entity : deposits) {
            DepositAgg agg = (DepositAgg) entity;
            totalDepositAmount += Long.parseLong(agg.getAmount());
        }
        return totalDepositAmount;
    }

    private long getTotalTransferAmount(String customerNumber, String accountNumber, String from, String to) throws SQLException {
        List<Entity> transfers = transferAgg.read(customerNumber, accountNumber, from, to);
        long totalTransferAmount = 0;
        for (Entity entity : transfers) {
            TransferAgg agg = (TransferAgg) entity;
            String transferAmount = agg.getAmount();

            totalTransferAmount += Long.parseLong(transferAmount);
        }
        return totalTransferAmount;
    }

    private long getTotalWithdrawAmount(String customerNumber, String accountNumber, String from, String to) throws SQLException {
        List<Entity> withdraws = withdrawAgg.read(customerNumber, accountNumber, from, to);
        long totalWithdrawAmount = 0;
        for (Entity entity : withdraws) {
            WithdrawAgg agg = (WithdrawAgg) entity;
            String withdrawAmount = agg.getAmount();

            totalWithdrawAmount += Long.parseLong(withdrawAmount);
        }
        return totalWithdrawAmount;
    }
}