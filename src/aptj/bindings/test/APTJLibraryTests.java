package aptj.bindings.test;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bridj.CLong;
import org.bridj.Pointer;
import org.junit.Test;

import aptj.bindings.APTLibrary;

public class APTJLibraryTests
{

	@Test
	public void test() throws InterruptedException
	{
		// APTLibrary.APTCleanUp();
		APTLibrary.APTInit();

		Pointer<CLong> lPointerNumDevices = Pointer.allocateCLong();

		APTLibrary.GetNumHWUnitsEx(	APTLibrary.HWTYPE_TST001,
																lPointerNumDevices);

		long lNumberOfDevices = lPointerNumDevices.getCLong();
		lPointerNumDevices.release();

		System.out.println(lPointerNumDevices.getCLong());

		BidiMap<Integer, Long> lIndexToSerialBidiMap = new DualHashBidiMap<>();

		for (int i = 0; i < lNumberOfDevices; i++)
		{
			Pointer<CLong> lPointerSerialNum = Pointer.allocateCLong();
			APTLibrary.GetHWSerialNumEx(APTLibrary.HWTYPE_TST001,
																	i,
																	lPointerSerialNum);
			long lSerialNumber = lPointerSerialNum.getCLong();
			lPointerSerialNum.release();

			System.out.format("_________________________________________________\n");
			System.out.format("device %d has serial num: %d \n",
												i,
												lSerialNumber);

			lIndexToSerialBidiMap.put(i, lSerialNumber);

			Pointer<Character> lPointerModelString = Pointer.allocateChars(1024);
			Pointer<Character> lPointerSWVerString = Pointer.allocateChars(1024);
			Pointer<Character> lPointerNotesString = Pointer.allocateChars(1024);

			APTLibrary.GetHWInfo(	lSerialNumber,
														lPointerModelString,
														1024,
														lPointerSWVerString,
														1024,
														lPointerNotesString,
														1024);

			String lModelString = lPointerModelString.getCString().trim();
			String lSWVerString = lPointerSWVerString.getCString().trim();
			String lNotesString = lPointerNotesString.getCString().trim();

			System.out.println(lModelString);
			System.out.println(lSWVerString);
			System.out.println(lNotesString);

			APTLibrary.InitHWDevice(lSerialNumber);
			APTLibrary.EnableEventDlg(1);

			APTLibrary.MOT_MoveVelocity(lSerialNumber,
																	1 + (((i % 2) == 0) ? 1 : -1));

		}

		Thread.sleep(10 * 1000);

		for (int i = 0; i < lNumberOfDevices; i++)
		{

			long lSerialNumber = lIndexToSerialBidiMap.get(i);

			APTLibrary.MOT_MoveHome(lSerialNumber,
															(i == lNumberOfDevices - 1) ? 1 : 0);

		}

		Thread.sleep(10 * 1000);

		APTLibrary.APTCleanUp();
	}
}
