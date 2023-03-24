package com.kakaobank.transactiongenerator.global;

import com.kakaobank.transactiongenerator.global.kafka.KafkaSampler;
import com.kakaobank.transactiongenerator.global.utils.KafkaUtils;
import com.kakaobank.transactiongenerator.global.utils.SimulatorUtils;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class Simulator implements Runnable, WithLogger {
    private final String threadName;
    private long amount = SimulatorUtils.amountGen();

    public Simulator(long number) {
        threadName = "simulator-thread-" + number;
    }

    @Override
    public void run() {
        logger.info("[{}]Create a simulation. Start time: {}", threadName, new Date());

        KafkaSampler withdrawSampler = new KafkaSampler();
        KafkaSampler transferSampler = new KafkaSampler();
        KafkaSampler depositSampler = new KafkaSampler();
        try {
            withdrawSampler.setupTest(KafkaUtils.WITHDRAW_GENERATOR);
            transferSampler.setupTest(KafkaUtils.TRANSFER_GENERATOR);
            depositSampler.setupTest(KafkaUtils.DEPOSIT_GENERATOR);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        String customerNumber = SimulatorUtils.stringGen();
        String accountNumber = SimulatorUtils.accountGen();
        try {
            before(depositSampler, customerNumber, accountNumber);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        String receivingAccountNumber = SimulatorUtils.accountGen();
        while(true) {
            int rInt = ThreadLocalRandom.current().nextInt(0, 3);
            try {
                if (rInt == 0) {
                    if(!withdraw(withdrawSampler, customerNumber, accountNumber)) break;
                } else if (rInt == 1) {
                    deposit(depositSampler, customerNumber, accountNumber);
                } else {
                    if(!transfer(transferSampler, customerNumber, accountNumber, receivingAccountNumber)) break;
                }
            } catch(Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
                break;
            }
        }
    }

    private void before(KafkaSampler depositSampler, String customerNumber, String accountNumber) throws Exception {
        logger.info("1. Join");
        KafkaSampler joinSampler = new KafkaSampler();
        joinSampler.setupTest(KafkaUtils.JOIN_GENERATOR);
        joinSampler.runTest(SimulatorUtils.timeGen(), customerNumber, SimulatorUtils.CUSTOMER_NAME, SimulatorUtils.birthGen());
        logger.info("2. New account");
        KafkaSampler accountSampler = new KafkaSampler();
        accountSampler.setupTest(KafkaUtils.ACCOUNT_GENERATOR);
        accountSampler.runTest(accountNumber, SimulatorUtils.timeGen(), customerNumber);
        logger.info("3. Deposit");
        depositSampler.runTest(String.valueOf(amount), SimulatorUtils.timeGen(), accountNumber, customerNumber);
    }

    private boolean withdraw(KafkaSampler withdrawSampler, String customerNumber, String accountNumber) throws Exception {
        logger.info("Withdraw");
        long withdrawAmount = SimulatorUtils.withdrawAmountGen();
        amount -= withdrawAmount;
        if(amount < 0) {
            amount += withdrawAmount;
            logger.error("The withdrawal amount({}) is larger than the remaining amount({}).", withdrawAmount, amount);
            return false;
        }
        withdrawSampler.runTest(String.valueOf(withdrawAmount), SimulatorUtils.timeGen(), accountNumber, customerNumber);
        return true;
    }

    private boolean transfer(KafkaSampler transferSampler, String customerNumber, String accountNumber, String receivingAccountNumber) throws Exception {
        logger.info("Transfer");
        long transferAmount = SimulatorUtils.transferAmountGen();
        amount -= transferAmount;
        if(amount < 0) {
            amount += transferAmount;
            logger.error("The transfer amount({}) is larger than the remaining amount({}).", transferAmount, amount);
            return false;
        }
        transferSampler.runTest(String.valueOf(transferAmount), SimulatorUtils.timeGen(), customerNumber, accountNumber, SimulatorUtils.BANK_NAME, receivingAccountNumber, SimulatorUtils.stringGen());
        return true;
    }

    private void deposit(KafkaSampler depositSampler, String customerNumber, String accountNumber) throws Exception {
        logger.info("Deposit");
        long depositAmount = SimulatorUtils.amountGen();
        amount += depositAmount;
        depositSampler.runTest(String.valueOf(depositAmount), SimulatorUtils.timeGen(), accountNumber, customerNumber);
    }
}