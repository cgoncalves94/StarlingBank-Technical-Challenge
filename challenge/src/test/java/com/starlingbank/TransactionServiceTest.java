package com.starlingbank;

import com.starlingbank.api.StarlingClient;
import com.starlingbank.exceptions.ApiException;
import com.starlingbank.exceptions.ServiceException;
import com.starlingbank.model.Transaction;
import com.starlingbank.service.TransactionService;
import java.util.List;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * This class is used to test the TransactionService class.
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private static final int EXPECTED_MINOR_UNITS = 123;
    private static final int MOCK_MINOR_UNITS = 123;
    private static final int API_ERROR_CODE = 500;


    // Mocking the StarlingClient class
    @Mock
    private StarlingClient starlingClient;

    // Injecting the mocks into the TransactionService class
    @InjectMocks
    private TransactionService transactionService;

    // Test case for successful retrieval of transactions
    @Test
    void getTransactionsBetween_Success() throws Exception {
        // Arrange
        String accountUid = "account-uid";
        String categoryUid = "category-uid";
        String minTransactionTimestamp = "2021-01-01T00:00:00Z";
        String maxTransactionTimestamp = "2021-01-31T23:59:59Z";
        String mockResponse = "{\"feedItems\":[{\"amount\":{\"currency\":\"GBP\","
                + "\"minorUnits\":123},\"source\":\"FASTER_PAYMENTS_OUT\"}]}";
        when(starlingClient.getTransactions(accountUid, categoryUid, minTransactionTimestamp, maxTransactionTimestamp))
            .thenReturn(mockResponse);

        // Act
        List<Transaction> result = transactionService
            .getTransactions(accountUid, categoryUid, minTransactionTimestamp, maxTransactionTimestamp);

        // Assert
        assertThat(result).isNotNull().isNotEmpty();
        assertThat(result.get(0).getMinorUnits()).isEqualTo(EXPECTED_MINOR_UNITS);
        assertThat(result.get(0).getSource()).isEqualTo("FASTER_PAYMENTS_OUT");

        // Verify the interaction with StarlingClient
        verify(starlingClient)
            .getTransactions(accountUid, categoryUid, minTransactionTimestamp, maxTransactionTimestamp);
    }

    // Test case for handling ApiException
    @Test
    void getTransactionsBetween_ApiException() throws Exception {
        // Arrange
        String accountUid = "account-uid";
        String categoryUid = "category-uid";
        String minTransactionTimestamp = "2021-01-01T00:00:00Z";
        String maxTransactionTimestamp = "2021-01-31T23:59:59Z";
        when(starlingClient.getTransactions(accountUid, categoryUid, minTransactionTimestamp, maxTransactionTimestamp))
            .thenThrow(new ApiException(API_ERROR_CODE, "API error", "Detailed API error"));

        // Act & Assert
        assertThatThrownBy(() -> transactionService
            .getTransactions(accountUid, categoryUid, minTransactionTimestamp, maxTransactionTimestamp))
            .isInstanceOf(ServiceException.class)
            .hasMessageContaining("Error response from the API")
            .hasCauseInstanceOf(ApiException.class);

        // Verify the interaction with StarlingClient
        verify(starlingClient)
            .getTransactions(accountUid, categoryUid, minTransactionTimestamp, maxTransactionTimestamp);
    }

    // Test case for handling JSONException
    @Test
    void getTransactionsBetween_JsonException() throws Exception {
        // Arrange
        String accountUid = "account-uid";
        String categoryUid = "category-uid";
        String minTransactionTimestamp = "2021-01-01T00:00:00Z";
        String maxTransactionTimestamp = "2021-01-31T23:59:59Z";
        // Simulate a broken JSON response with a missing closing brace
        String mockResponse = "{\"feedItems\":[{\"amount\":{\"currency\":\"GBP\",\"minorUnits\":"
            + MOCK_MINOR_UNITS + "},\"source\":\"FASTER_PAYMENTS_OUT\"";
        when(starlingClient
            .getTransactions(accountUid, categoryUid, minTransactionTimestamp, maxTransactionTimestamp))
            .thenReturn(mockResponse);

        // Act & Assert
        assertThatThrownBy(() -> transactionService
            .getTransactions(accountUid, categoryUid, minTransactionTimestamp, maxTransactionTimestamp))
            .isInstanceOf(ServiceException.class)
            .hasMessageContaining("Error parsing the response from the API")
            .hasCauseInstanceOf(JSONException.class);

        // Verify the interaction with StarlingClient
        verify(starlingClient)
            .getTransactions(accountUid, categoryUid, minTransactionTimestamp, maxTransactionTimestamp);
    }
}
