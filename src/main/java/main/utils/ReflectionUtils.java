package main.utils;

import java.awt.Component;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;

import gui.utils.GUIErrorHandler;

/**
 * Contains utility methods using reflection.
 *
 * @author Maxime PIA
 */
public class ReflectionUtils {

	/**
	 * Finds the getters of a class.
	 *
	 * @param c
	 * 			The class.
	 * @return The getters of the class.
	 */
	public static List<Method> getGetters(Class<?> c) {
		Method[] allMethods = c.getDeclaredMethods();
		List<Method> getters = new LinkedList<>();
		for (Method m : allMethods) {
			if (((m.getReturnType().equals(boolean.class)
				&& m.getName().startsWith("is"))
				|| m.getName().startsWith("get"))
				&& m.getParameterTypes().length == 0) {

				getters.add(m);
			}
		}
		return getters;
	}

	/**
	 * Finds the setters of a class.
	 *
	 * @param c
	 * 			The class.
	 * @return The setters of the class.
	 */
	public static List<Method> getSetters(Class<?> c) {
		return getSetters(c, false);
	}

	private static List<Method> getSetters(Class<?> c, boolean flag) {
		Method[] allMethods = c.getDeclaredMethods();
		List<Method> setters = new LinkedList<>();
		for (Method m : allMethods) {
			Class<?>[] types = m.getParameterTypes();
			if (m.getName().startsWith("set")
				&& types.length == (flag ? 2 : 1)) {

				if ((flag && types[1].equals(boolean.class)) || !flag) {
					setters.add(m);
				}
			}
		}
		return setters;
	}

	private static Method getAccessor(Class<?> c, Field field, boolean setter,
		boolean flag) {

		String accessorSuffix;
		int modifiers = field.getModifiers();
		if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
			accessorSuffix = StringUtils.screamingSnakeToWords(field.getName());
		} else {
			accessorSuffix = StringUtils.camelToWords(field.getName());
		}
		accessorSuffix = StringUtils.wordsToScreamingCamel(accessorSuffix);

		List<Method> accessors = setter ? getSetters(c, flag) : getGetters(c);
		for (Method accessor : accessors) {
			if (accessor.getName().endsWith(accessorSuffix)) {
				return accessor;
			}
		}
		return null;
	}

	/**
	 * Finds the setter of a field.
	 *
	 * @param c
	 * 			The class containing the field.
	 * @param field
	 * 			The field.
	 * @return The setter of the field.
	 */
	public static Method getSetter(Class<?> c, Field field) {
		return getAccessor(c, field, true, false);
	}

	/**
	 * Finds the getter of a field.
	 *
	 * @param c
	 * 			The class containing the field.
	 * @param field
	 * 			The field.
	 * @return The getter of the field.
	 */
	public static Method getGetter(Class<?> c, Field field) {
		return getAccessor(c, field, false, false);
	}

	/**
	 * Finds the setter of a field containing flags.
	 *
	 * @param c
	 * 			The class containing the field.
	 * @param field
	 * 			The field.
	 * @return The setter of the field.
	 */
	public static Method getFlagSetter(Class<?> c, Field field) {
		return getAccessor(c, field, true, true);
	}

	/**
	 * Finds the fields annotated by a given annotation.
	 *
	 * @param c
	 * 			The class containing the fields.
	 * @param annotationClass
	 * 			The annotation.
	 * @return The fields annotated by the annotation.
	 */
	public static List<Field> getAnnotatedFields(Class<?> c,
		Class<? extends Annotation> annotationClass) {

		Field[] fields = c.getDeclaredFields();
		List<Field> annotatedFields = new LinkedList<>();
		for (Field field : fields) {
			if (field.getAnnotation(annotationClass) != null) {
				annotatedFields.add(field);
			}
		}
		return annotatedFields;
	}

	/**
	 * Finds the annotation objects of annotated fields.
	 *
	 * @param c
	 * 			The class containing the fields.
	 * @param annotationClass
	 * 			The annotation.
	 * @return The annotations of the annotated fields.
	 */
	public static <T extends Annotation> List<T> getAnnotations(Class<?> c,
		Class<T> annotationClass) {

		Field[] fields = c.getDeclaredFields();
		List<T> annotations = new LinkedList<>();
		for (Field field : fields) {
			T annotation = field.getAnnotation(annotationClass);
			if (annotation != null) {
				annotations.add(annotation);
			}
		}
		return annotations;
	}

	/**
	 * Finds the fields with the given modifiers.
	 *
	 * @param c
	 * 			The class containing the fields.
	 * @param modifiers
	 * 			The modifiers.
	 * @return The fields with the given modifiers.
	 */
	public static List<Field> getModifiedFields(Class<?> c, int modifiers) {
		Field[] fields = c.getDeclaredFields();
		List<Field> modifiedFields = new LinkedList<>();
		for (Field field : fields) {
			if ((field.getModifiers() & modifiers) > 0) {
				modifiedFields.add(field);
			}
		}
		return modifiedFields;
	}

	private static Method getGUIComponentValueAccessor(Component elt,
		boolean setter) {

		String accessorPrefix = setter ? "set" : "get";
		Class<?>[] args = new Class<?>[0];
		try {
			if (elt instanceof JCheckBox) {
				if (setter) {
					args = new Class<?>[]{boolean.class};
				} else {
					accessorPrefix = "is";
				}
				return elt.getClass().getMethod(
					accessorPrefix + "Selected",
					args
				);
			} else if (elt instanceof JComboBox
				|| elt instanceof JSpinner) {

				if (setter) {
					args = new Class<?>[]{Object.class};
				}
				String accessorSuffix = elt instanceof JComboBox
					? "SelectedItem"
					: "Value";
				return elt.getClass().getMethod(
					accessorPrefix + accessorSuffix,
					args
				);
			}
		} catch (NoSuchMethodException | SecurityException e) {
			new GUIErrorHandler(e);
		}
		return null;
	}

	/**
	 * Finds the getter on the value contained in a component.
	 *
	 * @param elt
	 * 			The graphical component containing a value.
	 * @return The getter on the value contained in the component.
	 */
	public static Method getGUIComponentValueGetter(Component elt) {
		return getGUIComponentValueAccessor(elt, false);
	}

	/**
	 * Finds the setter on the value contained in a component.
	 *
	 * @param elt
	 * 			The graphical component containing a value.
	 * @return The setter on the value contained in the component.
	 */
	public static Method getGUIComponentValueSetter(Component elt) {
		return getGUIComponentValueAccessor(elt, true);
	}

}
