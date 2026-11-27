package com.tddai.broker.http;

import static org.hamcrest.Matchers.hasSize;

import com.tddai.broker.support.BrokerIntegrationTest;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BalanceEndpointsTest extends BrokerIntegrationTest {

    @Test
    void opensAnEurBalance() {
        String id = createAccount("Ada");

        openBalance(id, "EUR").statusCode(201).body("balances", hasSize(2));
    }

    @Test
    void opensEurOnlyOnce() {
        String id = createAccount("Ada");
        openBalance(id, "EUR").statusCode(201);

        openBalance(id, "EUR").statusCode(409);
    }

    @Test
    void usdIsAlreadyOpenSoOpeningItConflicts() {
        String id = createAccount("Ada");

        openBalance(id, "USD").statusCode(409);
    }

    @Test
    void noOtherCurrencyCanBeOpened() {
        String id = createAccount("Ada");

        openBalance(id, "GBP").statusCode(400);
    }

    @Test
    void openingABalanceOnAnUnknownAccountIsNotFound() {
        openBalance(UUID.randomUUID().toString(), "EUR").statusCode(404);
    }
}
