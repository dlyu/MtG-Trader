package com.mtg.trade.guide;

/*
 * A singleton class that holds several global constants to be used anywhere within the app
 * */
final class GlobalConstants {
	static GlobalConstants sConstants;
	static final int sConditionFlagNM = 0x0;
	static final int sConditionFlagSP = 0x1;
	static final int sConditionFlagMP = 0x2;
	
	static final int sFlagNormal = 0;
	static final int sFlagCallback = 1;
	
	static final String sProductUrl = "http://sales.starcitygames.com/carddisplay.php?product=";
	
	private GlobalConstants() {
		
	}
	
	public static GlobalConstants getInstance() {
		if (sConstants == null) {
			sConstants = new GlobalConstants();
		}
		return sConstants;
	}
	
	public static String implode(String[] arr, String delimiter) {
		String str = "";
		int length = arr.length;
		int idx = 0;
		while (true) {
			str = str + arr[idx++];
			if (idx == length)
				break;
			str = str + delimiter;
		}
		
		return str;
	}
	
	public static String[] explode(String str, String delimiter) {
		// Take care of special characters if any. Otherwise it's no different from calling str.split
		return str.split(delimiter);
	}
	
	public static int getProductHashFromView(RawCardData card) {
    	int productId = Integer.parseInt(card.getProductId());
    	String productCondition = card.getCondition();
    	int productHashMask = GlobalConstants.sConditionFlagNM;
    	if (productCondition.equals("SP"))
    		productHashMask = GlobalConstants.sConditionFlagSP;
    	else if (productCondition.equals("MP"))
    		productHashMask = GlobalConstants.sConditionFlagMP;
    	
    	// Bit shift the product ID to the left by 2 bits and OR it with the condition mask
    	int productHash = (productId << 2) | productHashMask;
    	return productHash;
	}
	
	public static String[] getDataFromProductHash(String hash) {
		try {
			int intHash = Integer.parseInt(hash);
			return getDataFromProductHash(intHash);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}
	
	public static String[] getDataFromProductHash(int hash) {
		int productId = hash >> 2;
    	int conditionFlag = hash & 0x3;
    	String condition = "";
    	switch (conditionFlag) {
	    	case sConditionFlagNM:
	    		condition = "NM";
	    		break;
	    	case sConditionFlagSP:
	    		condition = "SP";
	    		break;
	    	case sConditionFlagMP:
	    		condition = "MP";
	    		break;
    	}
    	return new String[] {String.format("%d", productId), condition};
	}
}
