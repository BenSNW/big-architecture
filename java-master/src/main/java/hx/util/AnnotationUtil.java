package hx.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.stream.Stream;

public class AnnotationUtil {

	/**
	 * find whether any one of a AnnotatedElement's annotations is annotated by another annotation.
	 * if there is one, return the first as an {@link java.util.Optional Optional}
	 * 
	 * @param annotatedElement
	 * @param annotationType
	 * @return
	 */
	public static Optional<Annotation> findAnnotation(AnnotatedElement annotatedElement,
						Class<? extends Annotation> annotationType) {
		return Stream.of(annotatedElement.getAnnotations())
				.filter(ann -> ann.annotationType().isAnnotationPresent(annotationType))
				.findFirst();
	}
}
