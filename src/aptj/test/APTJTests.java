package aptj.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import aptj.APTJDevice;
import aptj.APTJDeviceType;
import aptj.APTJDeviceFactory;

/**
 * APTJ Tests
 *
 * @author royer
 */
public class APTJTests
{

	/**
	 * Tests TST001 device
	 * @throws Exception NA
	 */
	@Test
	public void testTST001Device() throws Exception
	{
		try (APTJDeviceFactory lAPTJLibrary = new APTJDeviceFactory(APTJDeviceType.TST001))
		{

			int lNumberOfDevices = lAPTJLibrary.getNumberOfDevices();

			System.out.println(lNumberOfDevices);

			APTJDevice lDevice = lAPTJLibrary.createDeviceFromIndex(0);

			System.out.println("home()");
			lDevice.home();
			assertTrue(lDevice.waitWhileMoving(	10,
																					10 * 1000,
																					TimeUnit.MILLISECONDS));
			assertEquals(lDevice.getCurrentPosition(), 0, 0.01);

			System.out.println("moveTo(2)");
			lDevice.moveTo(2);
			assertTrue(lDevice.waitWhileMoving(	10,
																					10 * 1000,
																					TimeUnit.MILLISECONDS));
			assertEquals(lDevice.getCurrentPosition(), 2, 0.01);

			System.out.println("moveBy(-1)");
			lDevice.moveBy(-1);
			assertTrue(lDevice.waitWhileMoving(	10,
																					10 * 1000,
																					TimeUnit.MILLISECONDS));
			assertEquals(lDevice.getCurrentPosition(), 1, 0.01);

			System.out.println("move(1)");
			lDevice.move(1);
			Thread.sleep(20000);
			assertTrue(lDevice.getCurrentPosition() > lDevice.getMaxPosition() / 2);

			System.out.println("moveTo(1)");
			lDevice.moveTo(1);
			assertTrue(lDevice.waitWhileMoving(	10,
																					10 * 1000,
																					TimeUnit.MILLISECONDS));
			assertEquals(lDevice.getCurrentPosition(), 1, 0.01);

			System.out.println("move(-1)");
			lDevice.move(-1);
			Thread.sleep(3000);
			assertTrue(lDevice.getCurrentPosition() < 1);

			System.out.println("home()");
			lDevice.home();
			assertTrue(lDevice.waitWhileMoving(	10,
																					10 * 1000,
																					TimeUnit.MILLISECONDS));
			assertEquals(lDevice.getCurrentPosition(), 0, 0.5);

		}
	}

}
