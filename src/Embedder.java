import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

class Embedder 
{
	private File inputFile;
	private File secretFile;
	private File outputFile;
	private byte[] buff;

	public Embedder(File inputFile,File secretFile,File outputFile)throws Exception
	{

		if(inputFile.exists() == false) 
		{
			throw new Exception("Source file doesn't exist!!!");
		} 
		else if(secretFile.exists() == false) 
		{
			throw new Exception("Secret file doesn't exist!!!");
		}

		this.inputFile = inputFile;
		this.secretFile = secretFile;
		this.outputFile = outputFile;
		this.buff = new byte[1024];
		ByteManager.setFlag(0);
		ByteManager.setPatternStrategy(ByteEmbedderPatternStrategy.INCREMENTAL_1);
	}

	public void embedd() throws Exception
	{

		boolean finished = false;
		int n,k,x,y;
		int[] updatedBytes;

		n = k = x = y = 0;

		byte[] headerBytes =  HeaderManager.getHeader(secretFile).getBytes();
		n = headerBytes.length;

		FileInputStream fin = new FileInputStream(secretFile);
		BufferedImage inputImage = ImageIO.read(inputFile);

		int inputWidth = inputImage.getWidth();
   		int inputHeight = inputImage.getHeight();

   		if(inputHeight*inputWidth < secretFile.length()+n)
		{
			fin.close();
			throw new Exception("Input image is too small to hold the secret file!!!");
		}
   		System.out.println("Input Capacity :: "+inputHeight*inputWidth);
   		System.out.println("Secret File Size :: "+secretFile.length());
		System.out.println("Header Bytes Length (File Name and Associated Attributes) :: "+n);

   		WritableRaster raster = inputImage.getRaster();

   		for( x=0; x < inputWidth; x++ )
		{
   			for( y=0; y < inputHeight; y++ )
			{
   				if(k == n)
				{
   					System.out.println("HeaderBreak :: "+x+" "+y);
   					finished = true;
   					break;
   				}

   				updatedBytes = ByteManager.embedAlienData(
   						headerBytes[k++],
						new int[]{raster.getSample(x, y,0), raster.getSample(x, y,1), raster.getSample(x, y,2)});
   				raster.setSample(x, y,0, updatedBytes[0]);
				raster.setSample(x, y,1, updatedBytes[1]);
				raster.setSample(x, y,2, updatedBytes[2]);

   			}
   			if(finished)
				break;
   		}

   		finished = false;
   		k = n = 0;

   		System.out.println("Main body embedding starts at "+x+" "+y);
        for (int i = x; i < inputWidth; i++) 
		{
            for (int j = y; j < inputHeight; j++) 
			{
				
				if(k == n)
				{
					n = fin.read(buff);
					k = 0;
				}

				if(n == -1)
				{
					finished = true;
					break;
				}

				updatedBytes = ByteManager.embedAlienData(
						buff[k++],
						new int[]{raster.getSample(i, j,0), raster.getSample(i, j,1), raster.getSample(i, j,2)});
				raster.setSample(i, j,0, updatedBytes[0]);
				raster.setSample(i, j,1, updatedBytes[1]);
				raster.setSample(i, j,2, updatedBytes[2]);
            }

            if(finished)break;
        }

		inputImage.setData(raster);
		ImageIO.write(inputImage,"png",outputFile);
		System.out.println("Done with emedding!!");     

	}
	
	public static void main(String args[])
	{
		String newFileName="";
		try
		{
			Scanner sc = new Scanner(System.in);
			System.out.print("Enter the Input Image Path : ");
			String inputImagePath = sc.nextLine();
			File originalFile = new File(inputImagePath);
			if (originalFile.exists()) 
			{
                String originalFileName = originalFile.getName();
                newFileName = "Output_"+originalFileName;

                String newFilePath = "..\\Embedded" + File.separator + newFileName;

                File newFile = new File(newFilePath);

                // Copy the contents of the original file to the new file
                FileInputStream fileInputStream = new FileInputStream(originalFile);
                FileOutputStream fileOutputStream = new FileOutputStream(newFile);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) > 0) 
				{
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                fileInputStream.close();
                fileOutputStream.close();
            } 
			System.out.print("Enter the Secret Image File Path : ");
			String secretImagePath = sc.nextLine();
			File inputFile = new File(inputImagePath);
			File secretFile = new File(secretImagePath);
			File outputFile = new File("..\\Embedded"+File.separator+newFileName);
			sc.close();
			new Embedder(inputFile, secretFile, outputFile).embedd();

		} 
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}
}