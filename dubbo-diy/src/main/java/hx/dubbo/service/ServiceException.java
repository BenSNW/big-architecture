package hx.dubbo.service;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 3022675517490998184L;
	
	private int code;
	
	public ServiceException(String msg) {
		super(msg);
		this.code = 0;		
	}
	
	public ServiceException(int code, String msg) {
		super(msg);
		this.code = code;		
	}

	public int getCode() {
		return code;
	}
	
	@Override
	public Throwable fillInStackTrace() {
        return this;
    } 

}
