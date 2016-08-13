package hx.stream.engine;

public class TerminationState extends AbstractStateNode {

	private static final long serialVersionUID = -2880820639643909837L;
	public static final TerminationState OK_STATE = new TerminationState(true, "OK");
	public static final TerminationState ERROR_STATE = new TerminationState(false, "Error");
	
	private TerminationState(boolean decision, String message) {
		super("Final state");
		this.decision = decision;
		this.message = message;
	}
	
	public static TerminationState okState() {
		return OK_STATE;
	}

	public static TerminationState errorState() {
		return OK_STATE;
	}
	
	
}
