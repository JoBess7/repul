package ca.ulaval.glo4003.repul.small.subscription.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.fixture.subscription.AddCardRequestFixture;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriberFixture;
import ca.ulaval.glo4003.repul.subscription.api.AccountResource;
import ca.ulaval.glo4003.repul.subscription.api.request.AddCardRequest;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;
import ca.ulaval.glo4003.repul.subscription.application.payload.ProfilePayload;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AccountResourceTest {
    private static final SubscriberUniqueIdentifier SUBSCRIBER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final ProfilePayload A_SUBSCRIBER_PROFILE_PAYLOAD =
        ProfilePayload.from(new SubscriberFixture().withSubscriberId(SUBSCRIBER_ID).build().getProfile());
    private static final AddCardRequest AN_ADD_CARD_REQUEST = new AddCardRequestFixture().build();

    private AccountResource accountResource;
    @Mock
    private SubscriberService subscriberService;
    @Mock
    private ContainerRequestContext containerRequestContext;

    @BeforeEach
    public void createAccountResource() {
        given(containerRequestContext.getProperty(any())).willReturn(SUBSCRIBER_ID.getUUID().toString());
        accountResource = new AccountResource(subscriberService);
    }

    @Test
    public void whenGettingAccount_shouldReturn200() {
        given(subscriberService.getSubscriberProfile(SUBSCRIBER_ID)).willReturn(A_SUBSCRIBER_PROFILE_PAYLOAD);

        Response response = accountResource.getAccount(containerRequestContext);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenGettingAccount_shouldReturnAccount() {
        given(subscriberService.getSubscriberProfile(SUBSCRIBER_ID)).willReturn(A_SUBSCRIBER_PROFILE_PAYLOAD);

        Response response = accountResource.getAccount(containerRequestContext);

        assertEquals(A_SUBSCRIBER_PROFILE_PAYLOAD, response.getEntity());
    }

    @Test
    public void whenAddingCard_shouldReturn204() {
        Response response = accountResource.addCard(containerRequestContext, AN_ADD_CARD_REQUEST);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenAddingCard_shouldAddCardToSubscriber() {
        accountResource.addCard(containerRequestContext, AN_ADD_CARD_REQUEST);

        verify(subscriberService).addCard(SUBSCRIBER_ID, new UserCardNumber(AN_ADD_CARD_REQUEST.cardNumber));
    }
}
