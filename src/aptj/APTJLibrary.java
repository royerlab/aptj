package aptj;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bridj.CLong;
import org.bridj.Pointer;

import aptj.bindings.APTLibrary;

public class APTJLibrary implements AutoCloseable
{
	private APTJDeviceType mAPTDeviceType;
	private BidiMap<Integer, Long> mIndexToSerialBidiMap = new DualHashBidiMap<>();

	public APTJLibrary(APTJDeviceType pAPTDeviceType) throws APTJExeption
	{
		super();
		mAPTDeviceType = pAPTDeviceType;
		checkError(APTLibrary.APTInit());
		enumerateDevices();
	}

	@Override
	public void close() throws Exception
	{
		checkError(APTLibrary.APTCleanUp());
	}

	private final void enumerateDevices() throws APTJExeption
	{
		mIndexToSerialBidiMap.clear();

		int lNumberOfDevices = getNumberOfDevices();
		for (int i = 0; i < lNumberOfDevices; i++)
		{
			Pointer<CLong> lPointerSerialNum = Pointer.allocateCLong();
			checkError(APTLibrary.GetHWSerialNumEx(	mAPTDeviceType.getTypeId(),
																							i,
																							lPointerSerialNum));
			long lSerialNumber = lPointerSerialNum.getCLong();
			lPointerSerialNum.release();

			mIndexToSerialBidiMap.put(i, lSerialNumber);

		}
	}

	public final APTJDevice createDevice(int pDeviceIndex) throws APTJExeption
	{
		return new APTJDevice(mAPTDeviceType,
													mIndexToSerialBidiMap.get(pDeviceIndex));
	}

	static long checkError(long pReturnCode) throws APTJExeption
	{
		if (pReturnCode != 0)
		{
			System.out.println("Return code=" + pReturnCode);
			throw new APTJExeption(pReturnCode);
		}
		return pReturnCode;
	}

	public final int getNumberOfDevices()
	{
		Pointer<CLong> lPointerNumDevices = Pointer.allocateCLong();

		APTLibrary.GetNumHWUnitsEx(	mAPTDeviceType.getTypeId(),
																lPointerNumDevices);

		long lNumberOfDevices = lPointerNumDevices.getCLong();
		lPointerNumDevices.release();

		return (int) lNumberOfDevices;
	}

}
