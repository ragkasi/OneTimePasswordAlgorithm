package qrCode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QRCodeGenerator {
	private static final MatrixToImageConfig DEFAULT_CONFIG = new MatrixToImageConfig();
    public static final String QR_CODE_IMAGE_PATH = "C:/Users/super/eclipse-workspace/OneTimePasswordAlgorithm/docs/MyQRCode.png";

    public static void generateQRCodeImage(String text, int width, int height, String filePath)
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG",path, DEFAULT_CONFIG);
    }
    /* 
    This method takes the text to be encoded, the width and height of the QR Code, 
    and returns the QR Code in the form of a byte array.
    */
    public static byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray(); 
        return pngData;
    }

    public static void main(String[] args) throws WriterException, IOException{
		int len = Integer.parseInt(args[1]);
		int wid = Integer.parseInt(args[2]);
		byte[] ret = getQRCodeImage(args[0], len, wid);
		//generateQRCodeImage(args[0],len,wid,QR_CODE_IMAGE_PATH);
		for(int i=0; i< ret.length ; i++) {
	         System.out.print(ret[i] +" ");
	      }
		//return ret;

	}
    //javac -cp C:\Users\super\eclipse-workspace\OneTimePasswordAlgorithm\src\qrCode\* QRCodeGenerator.java
    //java -cp C:\Users\super\eclipse-workspace\OneTimePasswordAlgorithm\src qrCode.QRCodeGenerator
    //java -cp "C:\Users\super\eclipse-workspace\OneTimePasswordAlgorithm\src;C:\Users\super\eclipse-workspace\OneTimePasswordAlgorithm\src\qrCode\*" qrCode.QRCodeGenerator hello 350 350

    
    /*public static void main(String[] args) {
        try {
            generateQRCodeImage("This is my first QR Code", 350, 350, QR_CODE_IMAGE_PATH);
        } catch (WriterException e) {
            System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
        }
    }*/
}