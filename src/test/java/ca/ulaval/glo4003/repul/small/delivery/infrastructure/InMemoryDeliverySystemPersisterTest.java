package ca.ulaval.glo4003.repul.small.delivery.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.delivery.application.exception.DeliverySystemNotFoundException;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.infrastructure.InMemoryDeliverySystemPersister;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class InMemoryDeliverySystemPersisterTest {
    private InMemoryDeliverySystemPersister inMemoryDeliverySystemPersister;

    @Mock
    private DeliverySystem deliverySystem;

    @BeforeEach
    public void createDeliverySystemPersister() {
        inMemoryDeliverySystemPersister = new InMemoryDeliverySystemPersister();
    }

    @Test
    public void whenSavingDeliverySystemAndGettingDeliverySystem_shouldReturnTheRightDeliverySystem() {
        inMemoryDeliverySystemPersister.save(deliverySystem);
        DeliverySystem deliverySystemFound = inMemoryDeliverySystemPersister.get();

        assertEquals(deliverySystem, deliverySystemFound);
    }

    @Test
    public void givenNoDeliverySystem_whenGettingDeliverySystem_shouldThrowDeliverySystemNotFoundException() {
        assertThrows(DeliverySystemNotFoundException.class, () -> inMemoryDeliverySystemPersister.get());
    }
}
