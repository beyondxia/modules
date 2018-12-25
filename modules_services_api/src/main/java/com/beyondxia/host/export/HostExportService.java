package com.beyondxia.host.export;
import com.beyondxia.modules.PAService;
import com.beyondxia.modules.exception.NoServiceException;

public abstract class HostExportService extends PAService implements IHostExport {
	public static IHostExport get() {
		try{
			return getService(SERVICE_NAME);
		} catch (NoServiceException noServiceException) {
			noServiceException.printStackTrace();
		}
		return null;
	}
}
