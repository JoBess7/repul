package ca.ulaval.glo4003.repul.small.notification.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.notification.application.exception.DeliveryPersonAccountNotFoundException;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccount;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryDeliveryPersonAccountRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class InMemoryDeliveryPersonAccountRepositoryTest {
    private static final DeliveryPersonUniqueIdentifier ACCOUNT_VALID_ID = new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();
    @Mock
    private DeliveryPersonAccount deliveryPersonAccount;
    private InMemoryDeliveryPersonAccountRepository inMemoryDeliveryPersonAccountRepository;

    @BeforeEach
    public void createRepo() {
        inMemoryDeliveryPersonAccountRepository = new InMemoryDeliveryPersonAccountRepository();
    }

    @Test
    public void whenSavingAccountAndGettingAccount_shouldReturnTheRightAccount() {
        given(deliveryPersonAccount.getAccountId()).willReturn(ACCOUNT_VALID_ID);

        inMemoryDeliveryPersonAccountRepository.save(deliveryPersonAccount);
        DeliveryPersonAccount accountFound = inMemoryDeliveryPersonAccountRepository.getByAccountId(ACCOUNT_VALID_ID);

        assertEquals(deliveryPersonAccount, accountFound);
    }

    @Test
    public void givenNoAccount_whenGettingAccount_shouldThrowDeliveryPersonAccountNotFoundException() {
        assertThrows(DeliveryPersonAccountNotFoundException.class, () -> inMemoryDeliveryPersonAccountRepository.getByAccountId(ACCOUNT_VALID_ID));
    }
}
