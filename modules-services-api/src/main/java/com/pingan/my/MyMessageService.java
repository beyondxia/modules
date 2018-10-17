package com.pingan.my;
import com.beyondxia.modules.PAService;
import com.beyondxia.modules.exception.NoServiceException;

public abstract class MyMessageService extends PAService implements IMyMessage {
	public static IMyMessage get() {
		try{
			return getService(SERVICE_NAME);
		} catch (NoServiceException noServiceException) {
			noServiceException.printStackTrace();
		}
		return null;
	}
}
