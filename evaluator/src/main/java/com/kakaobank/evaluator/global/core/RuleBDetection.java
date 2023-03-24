package com.kakaobank.evaluator.global.core;

import com.kakaobank.evaluator.domain.account.domain.AccountAgg;
import com.kakaobank.evaluator.domain.join.domain.JoinAgg;
import com.kakaobank.evaluator.domain.transfer.domain.TransferAgg;
import com.kakaobank.evaluator.global.Detection;
import com.kakaobank.evaluator.global.Entity;
import com.kakaobank.evaluator.global.Message;
import com.kakaobank.evaluator.global.WithLogger;
import com.kakaobank.evaluator.global.utils.DatabaseUtils;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RuleBDetection implements Detection, WithLogger {
    private final JoinAgg joinAgg;
    private final AccountAgg accountAgg;
    private final TransferAgg transferAgg;

    private RuleBDetection() {
        joinAgg = new JoinAgg(DatabaseUtils.getConnection());
        accountAgg = new AccountAgg(DatabaseUtils.getConnection());
        transferAgg = new TransferAgg(DatabaseUtils.getConnection());
    }

    private static class SingletonHelper {
        private static final RuleBDetection SINGLETON = new RuleBDetection();
    }

    public static RuleBDetection getInstance(){
        return SingletonHelper.SINGLETON;
    }

    @Override
    public FraudDetection detect(String customerNumber) throws ParseException, SQLException {
        JoinAgg customer = (JoinAgg) joinAgg.read(customerNumber).get(0);
        if(ageCondition(customer.getCustomerBirth())) {
            List<Entity> accounts = accountAgg.read(customerNumber);
            long currentTime = System.currentTimeMillis();
            String from = String.valueOf(currentTime - 600000);
            String to = String.valueOf(currentTime);

            List<Message> results = new ArrayList<>();
            for(Entity account : accounts) {
                AccountAgg target = (AccountAgg) account;
                List<RuleBTransfer> transfers = transferCondition(customerNumber, target.getNumber(), from, to);
                if(transfers.isEmpty()) continue;

                results.add(new RuleBMessage(customerNumber, customer.getCustomerName(), customer.getCustomerBirth(), transfers));
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

        boolean flag = age >= 50;
        logger.debug("Over 50 years old. Age: {}", age);

        return flag;
    }

    private List<RuleBTransfer> transferCondition(String customerNumber, String accountNumber, String from, String to) throws SQLException {
        List<Entity> transfers = transferAgg.read(customerNumber, accountNumber, from, to);
        Stream<String> receivingAccountNumbersStream = transfers
                .stream()
                .filter(TransferAgg.class::isInstance)
                .map(e -> ((TransferAgg) e).getReceivingAccountNumber());
        Map<String, Long> numberOfReceiving = receivingAccountNumbersStream
                .collect(Collectors.toMap(Function.identity(), value -> 1L, Long::sum));
        logger.debug("Number of receiving accounts: {}", numberOfReceiving);

        List<RuleBTransfer> fraudTransfers = new ArrayList<>();
        for (Entity transfer : transfers) {
            TransferAgg target = (TransferAgg) transfer;
            String transferAmount = target.getAmount();
            String receivingAccountNumber = target.getReceivingAccountNumber();
            if(Long.parseLong(transferAmount) <= 300000 && numberOfReceiving.get(receivingAccountNumber) >= 5) {
                fraudTransfers.add(new RuleBTransfer(accountNumber, receivingAccountNumber, transferAmount, target.getTransactionTime()));
            }
        }

        if(!fraudTransfers.isEmpty()) {
            List<RuleBTransfer> filteredFraudTransfers;
            while (true) {
                filteredFraudTransfers = fraudTransfersFilter(fraudTransfers, numberOfReceiving);
                if (filteredFraudTransfers.isEmpty()) return fraudTransfers;
                fraudTransfers = filteredFraudTransfers;
            }
        }

        return Collections.emptyList();
    }

    private List<RuleBTransfer> fraudTransfersFilter(List<RuleBTransfer> fraudTransfers, Map<String, Long> numberOfReceiving) {
        Stream<String> numberOfAmountDetectionStream = fraudTransfers
                .stream()
                .map(RuleBTransfer::getReceivingAccountNumber);
        Map<String, Long> numberOfAmountDetection = numberOfAmountDetectionStream
                .collect(Collectors.toMap(Function.identity(), value -> 1L, Long::sum));
        logger.debug("Number of receiving accounts filtered amount detection: {}", numberOfAmountDetection);

        for(RuleBTransfer fraudTransfer : fraudTransfers) {
            String receivingAccountNumber = fraudTransfer.getReceivingAccountNumber();
            if(numberOfAmountDetection.get(receivingAccountNumber) < numberOfReceiving.get(receivingAccountNumber)) {
                logger.debug("Filtered receiving account number: {}", receivingAccountNumber);
                fraudTransfers.removeIf(e -> e.getReceivingAccountNumber().equals(receivingAccountNumber));
                return fraudTransfers;
            }
        }

        logger.debug("There is nothing to filter.");
        return Collections.emptyList();
    }
}