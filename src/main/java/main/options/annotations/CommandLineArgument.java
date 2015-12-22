package main.options.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an option field as configurable.
 *
 * @author Maxime PIA
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CommandLineArgument {

	/**
	 * The command line used to set the value of the member.
	 */
	String command();

	/**
	 * A description of the effects and use of the member.
	 */
	String description();

	/**
	 * A description of how this command is used.
	 */
	String usage() default "";

	/**
	 * An example of how this command is used.
	 */
	String usageExample() default "";

	/**
	 * True of the members represents flags.
	 */
	boolean flags() default false;

}