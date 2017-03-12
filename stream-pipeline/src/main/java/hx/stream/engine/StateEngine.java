package hx.stream.engine;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StateEngine {

	private static final Logger logger = LoggerFactory.getLogger(StateEngine.class);
	private Set<StateNode> states = new HashSet<>();
	
	public StateEngine stateNode(StateNode state) {
		if (state != null && !states.add(state))
			logger.warn("duplicate state: " + state);
		return this;
	}
	
	public StateEngine ruleNode(StateNode source, StateNode target, RuleNode rule) {
		if (source == null || target == null || rule == null)
			throw new IllegalArgumentException();
		states.add(source);
		states.add(target);
		source.addRule(rule);
		return this;
	}
	
	public StateNode run() throws Throwable {
		StateNode state = buildGraph();
		while (state.decision() && state.nextState() != null)
			state = state.goToNext();
		return state;
	}

	private StateNode buildGraph() {
		return null;
	}
}
