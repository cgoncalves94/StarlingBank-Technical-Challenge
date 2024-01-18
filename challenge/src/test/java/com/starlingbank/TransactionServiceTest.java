package com.starlingbank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.starlingbank.api.StarlingClient;
import com.starlingbank.exceptions.ApiException;
import com.starlingbank.exceptions.ServiceException;
import com.starlingbank.model.Transaction;
import com.starlingbank.service.TransactionService;

/**
 * This class is used to test the TransactionService class.
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    // Constants for testing
    private static final int API_ERROR_CODE = 500;
    private static final String ACCOUNT_UID = "account-uid";
    private static final String CATEGORY_UID = "category-uid";
    private static final String MIN_TIMESTAMP = "2021-01-01T00:00:00Z";
    private static final String MAX_TIMESTAMP = "2021-01-31T23:59:59Z";
    private static final int EXPECTED_MINOR_UNITS = 123;
    private static final String MOCK_RESPONSE = "{\"feedItems\":[{\"amount\":{\"currency\":\"GBP\","
        + "\"minorUnits\":123},\"source\":\"FASTER_PAYMENTS_OUT\"}]}";

    // Mocks for testing
    @Mock
    private StarlingClient starlingClient;

    @InjectMocks
    private TransactionService transactionService;

    // Test for successful transaction retrieval
    @Test
    void getTransactions_Success() throws Exception {
        // Mock the API response
        when(starlingClient.getTransactions(ACCOUNT_UID, CATEGORY_UID, MIN_TIMESTAMP, MAX_TIMESTAMP))
            .thenReturn(MOCK_RESPONSE);

        // Call the method under test
        List<Transaction> result = transactionService.getTransactions(ACCOUNT_UID, CATEGORY_UID, MIN_TIMESTAMP, MAX_TIMESTAMP);

        // Assert the result
        assertThat(result).isNotNull().isNotEmpty();
        assertThat(result.get(0).getMinorUnits()).isEqualTo(EXPECTED_MINOR_UNITS);

        // Verify the API was called
        verify(starlingClient).getTransactions(ACCOUNT_UID, CATEGORY_UID, MIN_TIMESTAMP, MAX_TIMESTAMP);
    }

    // Test for invalid parameters
    @Test
    void getTransactions_InvalidParameters() {
        // Assert that an exception is thrown when null parameters are passed
        assertThatThrownBy(() -> transactionService.getTransactions(null, CATEGORY_UID, MIN_TIMESTAMP, MAX_TIMESTAMP))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Input parameters cannot be null or empty");

        // Assert that an exception is thrown when empty parameters are passed
        assertThatThrownBy(() -> transactionService.getTransactions(ACCOUNT_UID, "", MIN_TIMESTAMP, MAX_TIMESTAMP))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Input parameters cannot be null or empty");
    }

    // Test for API exception
    @Test
    void getTransactions_ApiException() throws Exception {
        // Mock the API to throw an exception
        when(starlingClient.getTransactions(ACCOUNT_UID, CATEGORY_UID, MIN_TIMESTAMP, MAX_TIMESTAMP))
            .thenThrow(new ApiException(API_ERROR_CODE, "API error", "Detailed API error"));

        // Assert that a ServiceException is thrown
        assertThatThrownBy(() -> transactionService.getTransactions(ACCOUNT_UID, CATEGORY_UID, MIN_TIMESTAMP, MAX_TIMESTAMP))
            .isInstanceOf(ServiceException.class)
            .hasMessageContaining("Received an error response from the API");

        // Verify the API was called
        verify(starlingClient).getTransactions(ACCOUNT_UID, CATEGORY_UID, MIN_TIMESTAMP, MAX_TIMESTAMP);
    }

    // Test for JSON parsing exception
    @Test
    void getTransactions_JsonException() throws Exception {
        // Mock the API to return invalid JSON
        when(starlingClient.getTransactions(ACCOUNT_UID, CATEGORY_UID, MIN_TIMESTAMP, MAX_TIMESTAMP))
            .thenReturn("Invalid JSON");

        // Assert that a ServiceException is thrown
        assertThatThrownBy(() -> transactionService.getTransactions(ACCOUNT_UID, CATEGORY_UID, MIN_TIMESTAMP, MAX_TIMESTAMP))
            .isInstanceOf(ServiceException.class)
            .hasMessageContaining("Error occurred while parsing the response from the API");

        // Verify the API was called
        verify(starlingClient).getTransactions(ACCOUNT_UID, CATEGORY_UID, MIN_TIMESTAMP, MAX_TIMESTAMP);
    }
}
