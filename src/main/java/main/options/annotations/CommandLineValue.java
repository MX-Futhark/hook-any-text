package main.options.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a public static final member of a class as a possible value for an
 * associated option field.
 *
 * @author Maxime PIA
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CommandLineValue {

	/**
	 * The command line equivalent of the value.
	 */
	String value();

	/**
	 * The effect of this value.
	 */
	String description();

	/**
	 * A command used as a shortcut for setting this value.
	 */
	String shortcut() default "";

	/**
	 * A description of the condition(s) under which the value is accepted or
	 * has an effect.
	 */
	String condition() default "";

}
