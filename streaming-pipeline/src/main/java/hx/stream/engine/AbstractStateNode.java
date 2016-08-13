package hx.stream.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractStateNode implements StateNode {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(AbstractStateNode.class);
	
	protected String name;
	protected boolean decision;
	protected String message;
	protected StateNode next;
	protected Map<RuleNode, StateNode> rules;

	public AbstractStateNode(String name) {
		this.name = name;
		
		decision = true;
		rules = new HashMap<>();
	}

	@Override
	public String name() {
		return name;
	}
	
	@Override
	public boolean decision() {
		return decision;
	}
	
	@Override
	public void setDecision(boolean decision) {
		this.decision = decision;
	}
	
	@Override
	public String messgae() {
		return message;
	}
	
	@Override
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public void addRule(RuleNode rule) {
		//if (rule != null && rules.put(rule, null))
		rules.put(rule, null);
	}
	
	@Override
	public StateNode nextState() {
		return next;
	}
	
	@Override
	public void setNextState(StateNode state) {
		next = state;
	}

	/**
	 * Override considerations:
	 * <p>1. parallel processing of {@code RuleNode}s and in-time termination of rules
	 * <p>2. merge {@code StateNode} results of different {@code RuleNode}s
	 */
	@Override
	public StateNode goToNext() throws Throwable {
		if (logger.isDebugEnabled())
			logger.debug(this.toString());
		
		List<StateNode> states = applyRules(rules.keySet());	
		return mergeStates(states);
	}
	
	@Override
	public String toString() {
		return String.format("%s[%s]", this.getClass().getName(), name);
	}

	protected List<StateNode> applyRules(Set<RuleNode> rules) throws Throwable {
		List<StateNode> states = new ArrayList<>(rules.size());
		for (RuleNode rule : rules)
			states.add(rule.decide(this));
		return states;
	}
	
	private StateNode mergeStates(List<StateNode> states) {
		return null;
	}

}
