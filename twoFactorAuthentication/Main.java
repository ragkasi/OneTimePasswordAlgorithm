/*
    * OneTimePasswordAlgorithm.java
    * OATH Initiative,
    * HOTP one-time password algorithm
    *
    */

   /* Copyright (C) 2004, OATH.  All rights reserved.
    *
    * License to copy and use this software is granted provided that it
    * is identified as the "OATH HOTP Algorithm" in all material
    * mentioning or referencing this software or this function.
    *
    * License is also granted to make and use derivative works provided
    * that such works are identified as
    *  "derived from OATH HOTP algorithm"
    * in all material mentioning or referencing the derived work.
    *
    * OATH (Open AuTHentication) and its members make no
    * representations concerning either the merchantability of this
    * software or the suitability of this software for any particular
    * purpose.
    *
    * It is provided "as is" without express or implied warranty
    * of any kind and OATH AND ITS MEMBERS EXPRESSaLY DISCLAIMS
    * ANY WARRANTY OR LIABILITY OF ANY KIND relating to this software.
    *
    * These notices must be retained in any copies of any part of this
    * documentation and/or software.
    */

   package twoFactorAuthentication;

   import org.openauthentication.otp.OneTimePasswordAlgorithmProgram;

   import com.google.zxing.WriterException;

   import qrCode.QRCodeGenerator;

   import java.io.IOException;
   import java.io.File;
   import java.io.DataInputStream;
   import java.io.FileInputStream ;
   import java.lang.reflect.UndeclaredThrowableException;
   import java.nio.charset.Charset;
   import java.nio.charset.StandardCharsets;
   import java.security.GeneralSecurityException;
   import java.security.NoSuchAlgorithmException;
   import java.security.InvalidKeyException;

   import javax.crypto.Mac;
   import javax.crypto.spec.SecretKeySpec;
   import java.time.Instant;
   import java.util.Date;
   import java.util.Random;
   import java.util.Arrays;
   import java.util.Base64;
   import java.io.*;
   import java.io.FileWriter;
   
   import java.sql.Connection;
   import java.sql.DriverManager;
   import java.sql.PreparedStatement;
   import java.sql.ResultSet;
   import java.sql.SQLException;
   import java.sql.Statement;
   
   import java.io.PrintWriter;
   import javax.servlet.ServletException;
   import javax.servlet.annotation.WebServlet;
   import javax.servlet.http.HttpServlet;
   import javax.servlet.http.HttpServletRequest;
   import javax.servlet.http.HttpServletResponse;
   
   public class Main {
       public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, InterruptedException, WriterException, IOException, SQLException {
    	   int leftLimit = 48; // numeral '0'
    	   int rightLimit = 122; // letter 'z'
    	   int targetStringLength = 50;
    	   Random random = new Random();
    	   String generatedString = random.ints(leftLimit, rightLimit + 1)
    	    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
    	    .limit(targetStringLength)
   	      	.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
   	      	.toString();
    	   int codeDigits = 6;
    	   long time = System.currentTimeMillis();
    	   boolean addCheckSum = false;
    	   int truncationOffset = 16;
    	   FileWriter fw = new FileWriter("./docs/TimeCount.json");
    	   FileReader fr = new FileReader("./docs/TimeCount.json");
    	   int i;
    	   System.out.println(time);
    	   System.out.println(generatedString);
    	   byte[] secret1 = QRCodeGenerator.getQRCodeImage(generatedString, 350, 350);
    	   fw.write("{\n");
    	   fw.write("\t\"id\":\"\",\n");
    	   fw.write("\t\"secret_id\":\""+generatedString.substring(8,12)+"\",\n");
    	   fw.write("\t\"time_count\":\""+time+"\"\n");
    	   fw.write("}\n");
    	   QRCodeGenerator.generateQRCodeImage(generatedString, 350, 350,"./docs/MyQRCode.png");
    	   String x = OneTimePasswordAlgorithmProgram.generateOTP(secret1,time,codeDigits,addCheckSum,truncationOffset);
    	   System.out.println(x);
    	   System.out.println("Loading driver...");

    	   try {
    	       Class.forName("com.mysql.cj.jdbc.Driver");
    	       System.out.println("Driver loaded!");
    	   } catch (ClassNotFoundException e) {
    	       throw new IllegalStateException("Cannot find the driver in the classpath!", e);
    	   }
    	   DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
    	   String dbURL = "jdbc:mysql://localhost:3306/timecount";
    	   String username = "raghav";
    	   String password = "Builder12";
    	   Connection conn = DriverManager.getConnection(dbURL,username,password);
    	   if (conn != null) {
    	       System.out.println("Connected");
    	   }
    	   Statement st = conn.createStatement();
    	   String sqlInsert = "INSERT INTO timecount.timecount (time_count, secret_id, confirmed, location) VALUES "
                   + "('"+time+"', '"+generatedString.substring(8,12)+"','0','1');";
    	   st.execute(sqlInsert);
    	   FileWriter htmlWriter = new FileWriter("C:/xampp/htdocs/WebAuth.html");
    	   htmlWriter.write("<!DOCTYPE html>\n");
    	   htmlWriter.write("<html>\n");
    	   htmlWriter.write("<head>\n");
    	   htmlWriter.write("<title>Web Auth</title>\n");
    	   htmlWriter.write("<meta charset=\"UTF-8\">\n");
    	   htmlWriter.write("<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/water.css@2/out/water.min.css\">\n");
    	   htmlWriter.write("</head>\n");
    	   htmlWriter.write("<body>\n");
    	   htmlWriter.write("<form action=\"info.php\" method=\"post\">\n");
    	   htmlWriter.write("<label for=\"otp\">Enter the otp on screen</label>\n");
    	   htmlWriter.write("<input type=\"text\" id=\"otp\" name=\"otp\">\n");
    	   htmlWriter.write("<label for=\"phone\">Enter phone number</label>\n");
    	   htmlWriter.write("<input type=\"text\" id=\"phone\" name=\"phonenumber\">\n");
    	   htmlWriter.write("<label>\n");
    	   htmlWriter.write("<input type=\"checkbox\" name=\"terms\">I agree to the terms and conditions\n");
    	   htmlWriter.write("</label>\n");
    	   htmlWriter.write("<br>\n");
    	   htmlWriter.write("<button>Submit</button>\n");
    	   htmlWriter.write("<input type = \"hidden\" id = \"secret\" name= \"secretName\" value=\""+generatedString+"\"/>\n");
    	   htmlWriter.write("</form>\n");
    	   htmlWriter.write("</body>\n");
    	   htmlWriter.write("</html>\n");
    	   htmlWriter.close();
    	   //FileWriter infoWriter = new FileWriter("C:\\xampp\\htdocs\\info.php");
    	   //infoWriter.write("<?php\n");
    	   //infoWriter.write("print_r($_POST);\n");
    	   //infoWriter.close();
    	   FileReader htmlReader = new FileReader("C:/xampp/htdocs/WebAuth.html");
    	   htmlReader.close();
    	   int sent = 1;
    	   
    	   
    	   fw.close();
    	   fr.close();
       }
   }