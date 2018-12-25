package com.beyondxia.host.export;
import com.beyondxia.modules.PAService;
import com.beyondxia.modules.exception.NoServiceException;

public abstract class TestServiceImplService extends PAService implements ITestServiceImpl {
	public static ITestServiceImpl get() {
		try{
			return getService(SERVICE_NAME);
		} catch (NoServiceException noServiceException) {
			noServiceException.printStackTrace();
		}
		return null;
	}
}
