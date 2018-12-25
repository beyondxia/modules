package com.beyondxia.bussiness1.export;
import com.beyondxia.modules.PAService;
import com.beyondxia.modules.exception.NoServiceException;

public abstract class DashboardService extends PAService implements IDashboard {
	public static IDashboard get() {
		try{
			return getService(SERVICE_NAME);
		} catch (NoServiceException noServiceException) {
			noServiceException.printStackTrace();
		}
		return null;
	}
}
