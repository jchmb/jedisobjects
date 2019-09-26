package nl.jchmb.jedisobjects.serializer.exception;

public class SerializerException extends RuntimeException {
	private static final long serialVersionUID = 2327955181423507106L;
	
	public SerializerException(Exception exception) {
		super(exception);
	}
}
