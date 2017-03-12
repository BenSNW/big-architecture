package hx.spark.stream.engine;

import java.io.Serializable;

public interface StateNode extends Serializable {

	String name();
	boolean decision();
	void setDecision(boolean decision);
	String messgae();
	void setMessage(String message);
	void addRule(RuleNode rules);
	StateNode nextState();
	void setNextState(StateNode state);
	StateNode goToNext() throws Throwable;
	
}
