package com.starlingbank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.starlingbank.api.StarlingClient;
import com.starlingbank.exceptions.ApiException;
import com.starlingbank.exceptions.ServiceException;
import com.starlingbank.model.Account;
import com.starlingbank.model.Amount;
import com.starlingbank.model.SavingGoal;
import com.starlingbank.service.SavingsGoalService;

/**
 * This class tests the SavingsGoalService class.
 * @author Cesar Goncalves
 */
@ExtendWith(MockitoExtension.class)
class SavingsGoalServiceTest {

    private static final int TARGET_AMOUNT_VALUE = 1000;
    private static final int API_ERROR_CODE = 500;


    // Mocking the StarlingClient
    @Mock
    private StarlingClient starlingClient;

    // Injecting the mocks into the SavingsGoalService
    @InjectMocks
    private SavingsGoalService savingsGoalService;

    // Test data
    private Account mockAccount;
    private Amount targetAmount;
    private SavingGoal mockSavingGoal;

    // Setting up the test data before each test

    @BeforeEach
    void setUp() {
        mockAccount = new Account("account-uid", "defaultCategory");
        targetAmount = new Amount(TARGET_AMOUNT_VALUE, "GBP");
        mockSavingGoal = new SavingGoal("sg-123", "Goal 1", targetAmount);
    }

    // Test for successful retrieval of savings goals
    @Test
    void getSavingsGoals_Success() throws Exception {
        // Arrange
        String mockResponse = "{\"savingsGoalList\":[{\"savingsGoalUid\":\"sg-123\",\"name\":\"Goal 1\","
            + "\"target\":{\"currency\":\"GBP\",\"minorUnits\":1000}}]}";

        when(starlingClient.getSavingsGoals(mockAccount.getAccountUid())).thenReturn(mockResponse);

        // Act
        List<SavingGoal> result = savingsGoalService.getSavingsGoals(mockAccount.getAccountUid());

        // Assert
        assertThat(result).isNotNull().isNotEmpty();
        assertThat(result.get(0)).usingRecursiveComparison().isEqualTo(mockSavingGoal);
    }

    // Test for handling of ApiException when retrieving savings goals

    @Test
    void getSavingsGoals_ApiException() throws IOException, ApiException {
        // Arrange
        when(starlingClient.getSavingsGoals(mockAccount.getAccountUid()))
            .thenThrow(new ApiException(API_ERROR_CODE, "API error", "Detailed API error"));

        // Act & Assert
        assertThatThrownBy(() -> savingsGoalService.getSavingsGoals(mockAccount.getAccountUid()))
            .isInstanceOf(ServiceException.class)
            .hasMessageContaining("Error communicating with the API")
            .hasCauseInstanceOf(ApiException.class);
    }

    // Test for successful creation of a savings goal
    @Test
    void createSavingsGoal_Success() throws IOException, ApiException {
        // Arrange
        String mockResponse = "{\"savingsGoalUid\":\"sg-123\"}";

        when(starlingClient.createSavingsGoal(mockAccount.getAccountUid(),
            mockSavingGoal.getName(), targetAmount.getCurrencyCode(), targetAmount.getMinorUnits()))
                .thenReturn(mockResponse);

        // Act
        SavingGoal result = savingsGoalService.createSavingsGoal(mockAccount, mockSavingGoal.getName(), targetAmount);

        // Assert
        assertThat(result).usingRecursiveComparison().isEqualTo(mockSavingGoal);
    }

    // Test for successful addition of money to a savings goal
    @Test
    void addMoneyToSavingsGoal_Success() throws IOException, ApiException {
        // Arrange
        doNothing().when(starlingClient).addMoneyToSavingsGoal(mockAccount.getAccountUid(),
            mockSavingGoal.getSavingsGoalUid(), targetAmount.getMinorUnits(), targetAmount.getCurrencyCode());

        // Act
        savingsGoalService.addMoneyToSavingsGoal(mockAccount, mockSavingGoal, targetAmount);

        // Assert
        verify(starlingClient).addMoneyToSavingsGoal(mockAccount.getAccountUid(),
            mockSavingGoal.getSavingsGoalUid(), targetAmount.getMinorUnits(), targetAmount.getCurrencyCode());
    }
}
