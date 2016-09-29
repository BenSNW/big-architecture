package hx.stream.engine;

/**
 * A functional interface used to transform states.
 * Implementation class should override the equals method. How ??
 * Or how to make them singleton ??!!!
 * 
 * Created by BenSNW on Jun 7, 2016
 *
 */
@FunctionalInterface
public interface RuleNode {
	
	StateNode decide(StateNode state) throws Throwable;	
}
