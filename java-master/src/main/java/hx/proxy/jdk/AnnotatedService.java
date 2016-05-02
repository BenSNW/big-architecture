package hx.proxy.jdk;

public interface AnnotatedService {
	
	@ServiceAnnotation("Annotated value")
	void serve(Object... args);
	
}
