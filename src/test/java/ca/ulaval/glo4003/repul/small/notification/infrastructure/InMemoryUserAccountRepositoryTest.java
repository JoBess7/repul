package ca.ulaval.glo4003.repul.small.notification.infrastructure;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.notification.application.exception.UserAccountNotFoundException;
import ca.ulaval.glo4003.repul.notification.domain.UserAccount;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryUserAccountRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class InMemoryUserAccountRepositoryTest {
    private static final SubscriberUniqueIdentifier AN_ACCOUNT_VALID_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final SubscriberUniqueIdentifier AN_ACCOUNT_INVALID_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final MealKitUniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();

    @Mock
    private UserAccount userAccount;
    private InMemoryUserAccountRepository inMemoryUserAccountRepository;

    @BeforeEach
    public void createRepo() {
        inMemoryUserAccountRepository = new InMemoryUserAccountRepository();
    }

    @Test
    public void whenSavingAccountAndGettingAccountById_shouldReturnTheRightAccount() {
        given(userAccount.getAccountId()).willReturn(AN_ACCOUNT_VALID_ID);

        inMemoryUserAccountRepository.save(userAccount);
        UserAccount accountFound = inMemoryUserAccountRepository.getAccountById(AN_ACCOUNT_VALID_ID);

        assertEquals(userAccount, accountFound);
    }

    @Test
    public void givenNoAccount_whenGettingAccountById_shouldThrowUserAccountNotFoundException() {
        assertThrows(UserAccountNotFoundException.class, () -> inMemoryUserAccountRepository.getAccountById(AN_ACCOUNT_INVALID_ID));
    }

    @Test
    public void whenSavingAccountAndGettingAccountByMealKitId_shouldReturnTheRightAccount() {
        given(userAccount.getAccountId()).willReturn(AN_ACCOUNT_VALID_ID);
        given(userAccount.getMealKitIds()).willReturn(List.of(A_MEAL_KIT_ID));

        inMemoryUserAccountRepository.save(userAccount);
        UserAccount accountFound = inMemoryUserAccountRepository.getAccountByMealKitId(A_MEAL_KIT_ID);

        assertEquals(userAccount, accountFound);
    }

    @Test
    public void givenNoAccount_whenGettingAccountByMealKitId_shouldThrowUserAccountNotFoundException() {
        assertThrows(UserAccountNotFoundException.class, () -> inMemoryUserAccountRepository.getAccountByMealKitId(AN_ACCOUNT_INVALID_ID));
    }
}
