package aptj;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

import java.util.concurrent.TimeUnit;

import org.bridj.CLong;
import org.bridj.Pointer;

import aptj.bindings.APTLibrary;

public class APTJDevice
{
	private final APTJDeviceType mAPTJDeviceType;
	private final long mDeviceSerialNum;
	private final String mModelString, mSWVerString, mNotesString;
	private final float mLowPosition, mHighPosition, mPitch;
	private final APTJUnits mUnits;
	private final float mMaxAccel, mMaxVelocity;

	public APTJDevice(APTJDeviceType pAPTJDeviceType,
										long pDeviceSerialNum) throws APTJExeption
	{
		mAPTJDeviceType = pAPTJDeviceType;
		mDeviceSerialNum = pDeviceSerialNum;

		APTJLibrary.checkError(APTLibrary.InitHWDevice(getSerialNumber()));
		APTJLibrary.checkError(APTLibrary.MOT_Identify(getSerialNumber()));/**/

		{
			Pointer<Character> lPointerModelString = Pointer.allocateChars(1024);
			Pointer<Character> lPointerSWVerString = Pointer.allocateChars(1024);
			Pointer<Character> lPointerNotesString = Pointer.allocateChars(1024);

			APTJLibrary.checkError(APTLibrary.GetHWInfo(getSerialNumber(),
																									lPointerModelString,
																									1024,
																									lPointerSWVerString,
																									1024,
																									lPointerNotesString,
																									1024));

			mModelString = lPointerModelString.getCString().trim();
			mSWVerString = lPointerSWVerString.getCString().trim();
			mNotesString = lPointerNotesString.getCString().trim();

			lPointerModelString.release();
			lPointerSWVerString.release();
			lPointerNotesString.release();
		}

		{
			Pointer<Float> lMinPosPointer = Pointer.allocateFloat();
			Pointer<Float> lMaxPosPointer = Pointer.allocateFloat();
			Pointer<CLong> lUnitPointer = Pointer.allocateCLong();
			Pointer<Float> lPitchPointer = Pointer.allocateFloat();

			APTJLibrary.checkError(APTLibrary.MOT_GetStageAxisInfo(	getSerialNumber(),
																															lMinPosPointer,
																															lMaxPosPointer,
																															lUnitPointer,
																															lPitchPointer));/**/

			mLowPosition = lMinPosPointer.get();
			mHighPosition = lMaxPosPointer.get();
			mPitch = lPitchPointer.get();
			mUnits = (((lUnitPointer.get().intValue()) == 1) ? APTJUnits.mm
																											: APTJUnits.deg);

			lMinPosPointer.release();
			lMaxPosPointer.release();
			lUnitPointer.release();
			lPitchPointer.release();
		}

		{
			Pointer<Float> lMaxAccnLimitPointer = Pointer.allocateFloat();
			Pointer<Float> lMaxVelLimitPointer = Pointer.allocateFloat();

			APTJLibrary.checkError(APTLibrary.MOT_GetVelParamLimits(getSerialNumber(),
																															lMaxAccnLimitPointer,
																															lMaxVelLimitPointer));/**/

			mMaxAccel = lMaxAccnLimitPointer.get();
			mMaxVelocity = lMaxVelLimitPointer.get();

			lMaxAccnLimitPointer.release();
			lMaxVelLimitPointer.release();
		}

	}

	public void home() throws APTJExeption
	{
		stopIfNeeded();
		APTJLibrary.checkError(APTLibrary.MOT_MoveHome(	getSerialNumber(),
																										0));
	}

	public void stop() throws APTJExeption
	{
		APTJLibrary.checkError(APTLibrary.MOT_StopProfiled(getSerialNumber()));
	}

	public void move(double pVelocity) throws APTJExeption
	{
		move(pVelocity, getAcceleration());
	}

	public void move(double pVelocity, double pAcceleration) throws APTJExeption
	{
		APTJLibrary.checkError(APTLibrary.MOT_SetVelParams(	getSerialNumber(),
																												0,
																												(float) getAcceleration(),
																												(float) abs(pVelocity)));

		int lDirection = signum(pVelocity) > 0 ? 1 : 2;

		APTJLibrary.checkError(APTLibrary.MOT_MoveVelocity(	getSerialNumber(),
																												lDirection));/**/
	}

	public void setSpeed(double pSpeed) throws APTJExeption
	{
		APTJLibrary.checkError(APTLibrary.MOT_SetVelParams(	getSerialNumber(),
																												0,
																												(float) getAcceleration(),
																												(float) pSpeed));
	}

	public double getSpeed() throws APTJExeption
	{
		Pointer<Float> lMinVelPointer = Pointer.allocateFloat();
		Pointer<Float> lAccnPointer = Pointer.allocateFloat();
		Pointer<Float> lMaxVelPointer = Pointer.allocateFloat();

		APTJLibrary.checkError(APTLibrary.MOT_GetVelParams(	getSerialNumber(),
																												lMinVelPointer,
																												lAccnPointer,
																												lMaxVelPointer));/**/

		double lSpeed = lMaxVelPointer.get();

		lMinVelPointer.release();
		lAccnPointer.release();
		lMaxVelPointer.release();

		return lSpeed;
	}

	public void setAcceleration(double pAcceleration) throws APTJExeption
	{
		APTJLibrary.checkError(APTLibrary.MOT_SetVelParams(	getSerialNumber(),
																												0,
																												(float) pAcceleration,
																												(float) getSpeed()));
	}

	public double getAcceleration() throws APTJExeption
	{
		Pointer<Float> lMinVelPointer = Pointer.allocateFloat();
		Pointer<Float> lAccnPointer = Pointer.allocateFloat();
		Pointer<Float> lMaxVelPointer = Pointer.allocateFloat();

		APTJLibrary.checkError(APTLibrary.MOT_GetVelParams(	getSerialNumber(),
																												lMinVelPointer,
																												lAccnPointer,
																												lMaxVelPointer));/**/

		double lAcceleration = lAccnPointer.get();

		lMinVelPointer.release();
		lAccnPointer.release();
		lMaxVelPointer.release();

		return lAcceleration;
	}

	public void moveTo(double pNewPosition) throws APTJExeption
	{
		stopIfNeeded();
		APTJLibrary.checkError(APTLibrary.MOT_MoveAbsoluteEx(	getSerialNumber(),
																													(float) pNewPosition,
																													0));
	}

	public void moveBy(double pDeltaPosition) throws APTJExeption
	{
		stopIfNeeded();
		APTJLibrary.checkError(APTLibrary.MOT_MoveRelativeEx(	getSerialNumber(),
																													(float) pDeltaPosition,
																													0));
	}

	public double getCurrentPosition() throws APTJExeption
	{
		Pointer<Float> lPositionPointer = Pointer.allocateFloat();

		APTJLibrary.checkError(APTLibrary.MOT_GetPosition(getSerialNumber(),
																											lPositionPointer));
		double lCurrentPosition = lPositionPointer.get();

		lPositionPointer.release();

		return lCurrentPosition;
	}

	public boolean waitWhileMoving(	long pPollPeriod,
																	long pTimeOut,
																	TimeUnit pTimeUnit) throws APTJExeption
	{
		long lPeriodInMilliseconds = TimeUnit.MILLISECONDS.convert(	pPollPeriod,
																																pTimeUnit);
		long lNumberOfPeriods = 1 + (pTimeOut / lPeriodInMilliseconds);
		for (long t = 0; t < lNumberOfPeriods; t++)
		{
			if (!isMoving())
				return true;
			try
			{
				Thread.sleep(lPeriodInMilliseconds);
			}
			catch (InterruptedException e)
			{
			}
		}
		return false;
	}

	private void stopIfNeeded() throws APTJExeption
	{
		if (isLimitAttained())
			stop();
	}

	public boolean isLimitAttained() throws APTJExeption
	{

		long lStatusBits = getStatusBits();

		boolean lCheckBit1 = checkBit(lStatusBits, 1 - 1);
		boolean lCheckBit2 = checkBit(lStatusBits, 2 - 1);
		boolean lCheckBit3 = checkBit(lStatusBits, 3 - 1);
		boolean lCheckBit4 = checkBit(lStatusBits, 4 - 1);

		boolean lIsLimitAttained = lCheckBit1 || lCheckBit2
																|| lCheckBit3
																|| lCheckBit4;

		return lIsLimitAttained;
	}

	public boolean isMoving() throws APTJExeption
	{

		long lStatusBits = getStatusBits();

		boolean lCheckBit1 = checkBit(lStatusBits, 5 - 1);
		boolean lCheckBit2 = checkBit(lStatusBits, 6 - 1);
		boolean lCheckBit3 = checkBit(lStatusBits, 7 - 1);
		boolean lCheckBit4 = checkBit(lStatusBits, 8 - 1);
		boolean lCheckBit5 = checkBit(lStatusBits, 10 - 1);

		/*System.out.println("lCheckBit1=" + lCheckBit1);
		System.out.println("lCheckBit2=" + lCheckBit2);
		System.out.println("lCheckBit3=" + lCheckBit3);
		System.out.println("lCheckBit4=" + lCheckBit4);
		System.out.println("lCheckBit5=" + lCheckBit5);/**/

		boolean lIsMoving = lCheckBit1 || lCheckBit2
												|| lCheckBit3
												|| lCheckBit4
												|| lCheckBit5;

		return lIsMoving;
	}

	private static boolean checkBit(long pValue, int pBitIndex)
	{
		return ((pValue & (1L << pBitIndex)) != 0);

	}

	private long getStatusBits() throws APTJExeption
	{
		Pointer<CLong> lStatusBitsPointer = Pointer.allocateCLong();

		APTJLibrary.checkError(APTLibrary.MOT_GetStatusBits(getSerialNumber(),
																												lStatusBitsPointer));

		/*System.out.println("status bits: " + toBinaryString(lStatusBitsPointer.get()
																																				.longValue()));/**/

		long lStatusBits = lStatusBitsPointer.get().longValue();

		lStatusBitsPointer.release();

		return lStatusBits;
	}

	private static String toBinaryString(long pLongValue)
	{
		return new StringBuilder(Long.toBinaryString(pLongValue)).reverse()
																															.toString();
	}

	public APTJDeviceType getDeviceType()
	{
		return mAPTJDeviceType;
	}

	public long getSerialNumber()
	{
		return mDeviceSerialNum;
	}

	public String getModelString()
	{
		return mModelString;
	}

	public String getSoftwareVersionString()
	{
		return mSWVerString;
	}

	public String getNotesString()
	{
		return mNotesString;
	}

	public float getLowPosition()
	{
		return mLowPosition;
	}

	public float getHighPosition()
	{
		return mHighPosition;
	}

	public APTJUnits getUnits()
	{
		return mUnits;
	}

	public float getPitch()
	{
		return mPitch;
	}

	public float getMaxAcceleration()
	{
		return mMaxAccel;
	}

	public float getMaxVelocity()
	{
		return mMaxVelocity;
	}

	@Override
	public String toString()
	{
		return String.format(	"APTJDevice [mAPTJDeviceType=%s, mDeviceSerialNum=%s, mModelString=%s, mSWVerString=%s, mNotesString=%s, mLowPosition=%s, mHighPosition=%s, mPitch=%s, mUnits=%s, mMaxAccel=%s, mMaxVelocity=%s]",
													mAPTJDeviceType,
													mDeviceSerialNum,
													mModelString,
													mSWVerString,
													mNotesString,
													mLowPosition,
													mHighPosition,
													mPitch,
													mUnits,
													mMaxAccel,
													mMaxVelocity);
	}

}
