package aptj;

public enum APTJDeviceType
{
	BSC001(11), // 1 Ch benchtop stepper driver
	BSC101(12), // 1 Ch benchtop stepper driver
	BSC002(13), // 2 Ch benchtop stepper driver
	BDC101(14), // 1 Ch benchtop DC servo driver
	SCC001(21), // 1 Ch stepper driver card (used within BSC102,103 units)
	DCC001(22), // 1 Ch DC servo driver card (used within BDC102,103 units)
	ODC001(24), // 1 Ch DC servo driver cube
	OST001(25), // 1 Ch stepper driver cube
	MST601(26), // 2 Ch modular stepper driver module
	TST001(29), // 1 Ch Stepper driver T-Cube
	TDC001(31), // 1 Ch DC servo driver T-Cube
	LTSXXX(42), // LTS300/LTS150 Long Travel Integrated Driver/Stages
	L490MZ(43), // L490MZ Integrated Driver/Labjack
	BBD10X(44); // 1/2/3 Ch benchtop brushless DC servo driver

	private int mTypeId;

	APTJDeviceType(int pTypeId)
	{
		this.mTypeId = pTypeId;
	}

	public int getTypeId()
	{
		return mTypeId;
	}

}
