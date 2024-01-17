package com.starlingbank;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.starlingbank.api.StarlingClient;
import com.starlingbank.exceptions.ApiException;
import com.starlingbank.exceptions.ServiceException;
import com.starlingbank.model.Account;
import com.starlingbank.service.AccountService;

import static org.mockito.Mockito.*;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

// This class is used to test the AccountService class
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    // Mocking the StarlingClient class
    @Mock
    private StarlingClient starlingClient;

    // Injecting the mocks into the AccountService class
    @InjectMocks
    private AccountService accountService;

    // Test case for successful retrieval of account details
    @Test
    void getAccountDetails_Success() throws Exception {
        // Arrange
        String mockResponse = "{\"accounts\":[{\"accountUid\":\"12345\",\"defaultCategory\":\"67890\"}]}";
        when(starlingClient.getAccountDetails()).thenReturn(mockResponse);

        // Act
        Account result = accountService.getAccountDetails();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAccountUid()).isEqualTo("12345");
        assertThat(result.getCategoryUid()).isEqualTo("67890");

        // Verify the interaction with StarlingClient
        verify(starlingClient).getAccountDetails();
    }

    // Test case for handling ApiException
    @Test
    void getAccountDetails_ApiException() throws IOException, ApiException {
        // Arrange
        when(starlingClient.getAccountDetails()).thenThrow(new ApiException(500, "API error", "Detailed API error"));
    
        // Act & Assert
        assertThatThrownBy(() -> accountService.getAccountDetails())
            .isInstanceOf(ServiceException.class)
            .hasMessageContaining("Error communicating with the API")
            .hasCauseInstanceOf(ApiException.class);
    
        // Verify the interaction with StarlingClient
        verify(starlingClient).getAccountDetails();
    }

    // Test case for handling JSONException
    @Test
    void getAccountDetails_JsonException() throws IOException, ApiException {
        // Arrange
        String invalidJsonResponse = "{\"accounts\":}";
        when(starlingClient.getAccountDetails()).thenReturn(invalidJsonResponse);

        // Act & Assert
        assertThatThrownBy(() -> accountService.getAccountDetails())
            .isInstanceOf(ServiceException.class)
            .hasMessageContaining("Error parsing the response from the API")
            .hasCauseInstanceOf(JSONException.class);

        // Verify the interaction with StarlingClient
        verify(starlingClient).getAccountDetails();
    }

    // Test case for handling empty accounts array
    @Test
    void getAccountDetails_EmptyAccountsArray() throws IOException, ApiException {
        // Arrange
        String emptyAccountsResponse = "{\"accounts\":[]}";
        when(starlingClient.getAccountDetails()).thenReturn(emptyAccountsResponse);

        // Act & Assert
        assertThatThrownBy(() -> accountService.getAccountDetails())
            .isInstanceOf(ServiceException.class)
            .hasMessageContaining("No accounts found");

        // Verify the interaction with StarlingClient
        verify(starlingClient).getAccountDetails();
    }
}
