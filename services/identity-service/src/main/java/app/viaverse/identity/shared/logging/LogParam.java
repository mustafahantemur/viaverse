package app.viaverse.identity.shared.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method parameter whose runtime value should be added to the
 * {@link ActionLogContext} for the surrounding {@link ObservedAction}.
 * The annotation value is used as the structured log key.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogParam {
    String value();
}
