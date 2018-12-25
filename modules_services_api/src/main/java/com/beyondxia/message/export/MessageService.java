package com.beyondxia.message.export;
import com.beyondxia.modules.PAService;
import com.beyondxia.modules.exception.NoServiceException;

public abstract class MessageService extends PAService implements IMessage {
	public static IMessage get() {
		try{
			return getService(SERVICE_NAME);
		} catch (NoServiceException noServiceException) {
			noServiceException.printStackTrace();
		}
		return null;
	}
}
