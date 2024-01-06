package ca.ulaval.glo4003.repul.user.middleware;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Roles {
    Role[] value();
}
