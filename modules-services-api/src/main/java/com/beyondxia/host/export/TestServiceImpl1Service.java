package com.beyondxia.host.export;
import com.beyondxia.modules.PAService;
import com.beyondxia.modules.exception.NoServiceException;

public abstract class TestServiceImpl1Service extends PAService implements ITestServiceImpl1 {
	public static ITestServiceImpl1 get() {
		try{
			return getService(SERVICE_NAME);
		} catch (NoServiceException noServiceException) {
			noServiceException.printStackTrace();
		}
		return null;
	}
}
