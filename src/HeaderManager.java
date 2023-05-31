import java.io.File;

public class HeaderManager{

	private static final char DISCRIMINATOR = '^';
	private static final char FILLER = '_';
	private static final int HEADER_LENGTH = 50;
	private static final int NAME_LENGTH = 40; 
	private static final int SIZE_LENGTH = 9; 


	public static String getHeader(File f){

		String fName = f.getName();
		String fLength = String.valueOf(f.length());

		if(fName.length() > NAME_LENGTH)fName = fName.substring(0, NAME_LENGTH);

//		string buffer
		while(fName.length() < NAME_LENGTH)fName = FILLER + fName;
//		string buffer
		while(fLength.length() < SIZE_LENGTH)fLength = FILLER + fLength;

		return fName+DISCRIMINATOR+fLength;
	}

	public static boolean isHeaderValid(String header)throws Exception
	{

		int n = header.length();
		int discriminatorCount = 0;
		int fillerCount = 0;

		for(int i=0;i<n;i++){
			if(header.charAt(i) == DISCRIMINATOR) {
				discriminatorCount++;
			} else if(header.charAt(i) == FILLER) {
				fillerCount++;
			}
		}

		if(discriminatorCount != 1) {
			throw new Exception("No secret file found!!!");
		}

		if(fillerCount+getName(header).length()+1+String.valueOf(getLength(header)).length() != header.length()){
			throw new Exception("No secret file found!!!");
		}

		return true;
	}

	public static String getName(String header){
		return ((header.substring(0,(header.indexOf(DISCRIMINATOR)))).replaceAll(String.valueOf(HeaderManager.FILLER)," ")).trim();
	}

	public static int getLength(String header){
		return Integer.parseInt(((header.substring((header.indexOf(DISCRIMINATOR)+1))).replaceAll(String.valueOf(HeaderManager.FILLER)," ")).trim());
	}

	public static int getHeaderLength(){
		return HeaderManager.HEADER_LENGTH;
	}

}