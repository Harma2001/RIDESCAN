package com.example.tvsridescan.Library;


import static com.example.tvsridescan.Library.DataConversion.bytesToHex;
import static com.example.tvsridescan.Library.DataConversion.hexStringToByteArray;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class TeAES_Lib
{
	final protected static String mTmlPropData = AppVariables.prop_data;
	final protected static String mTmlRefData = AppVariables.ref_data;
	public static byte[] genkey = null;
	public static short randomnumber = 0;

	public static byte[] GetEncryptionKey(String fKeyInput)
	{
		byte[] lAesKey = hexStringToByteArray(fKeyInput);
		genkey = lAesKey;
		return lAesKey;
	}

	public static byte[] encrypt_data(byte[] plainData, byte[] key) throws Exception
	{
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");

		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] lEncryptedBytes = cipher.doFinal(plainData);

		return lEncryptedBytes;
	}

	public static byte[] decrypt_data(byte[] encData, byte[] key)
	{
		byte[] lDecryptedData = new byte[0];
		try
		{
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");

			cipher.init(Cipher.DECRYPT_MODE, skeySpec);

			lDecryptedData = cipher.doFinal(encData);


		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return lDecryptedData;
	}

	public static String CalculateMD5Hash(String fSno, String fTmlPropData)
	{
		String lTempString = fSno + fTmlPropData;
		String Md5HashString = "";

		try
		{
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(lTempString.getBytes());
			byte[] digest = m.digest();

			Md5HashString = bytesToHex(digest);
		}
		catch (NoSuchAlgorithmException e) {};

		return Md5HashString;
	}

	public static String GenAuthRequest()
	{
			String lQueryString = null;
			// Get Device Serial Number
			String serialNum = AppVariables.Serialnumber;
			// Calculate MD5 Hash
			String Md5Hash = CalculateMD5Hash(serialNum, mTmlPropData);
			//Generate Encryption Key
			byte[] lAesKey = GetEncryptionKey(Md5Hash);
			// Encrypt Ref Data
			byte[] EncryptedBytes = null;
			try
			{
				EncryptedBytes = encrypt_data(mTmlRefData.getBytes(), lAesKey);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Generate Random Number
			Random lRand= new Random();
			int Low = 35535;
			int High = 65535;

			short lRandNum = (short)(lRand.nextInt(High-Low) + Low);//(short) 0xA5A5;//
			randomnumber = lRandNum;
			byte lRandMSB = (byte)((lRandNum >> 8) & 0xFF);
			byte lRandLSB = (byte)((lRandNum) & 0xFF);;

			// XOR the RANDOM Number with Encrypted Data
			byte[] lEnRandBytes = new byte[16];
			for(int i = 0; i < 8; i++)
			{
				lEnRandBytes[i * 2] = (byte)(EncryptedBytes[i * 2] ^ lRandMSB);
				lEnRandBytes[i * 2 + 1] = (byte)(EncryptedBytes[i * 2 + 1] ^ lRandLSB);
			}

			String lEnRandString = bytesToHex(lEnRandBytes);
			System.out.println("EnRand Text : " + lEnRandString);

			// Append Random Number
			byte[] lQueryBytes = new byte[18];
			lQueryBytes[0] = lRandMSB;
			lQueryBytes[17] = lRandLSB;
			for(int i = 0; i < 16; i++)
			{
				lQueryBytes[i + 1] = lEnRandBytes[i];
			}

			lQueryString = bytesToHex(lQueryBytes);
			//System.out.println("Query String Text : " + lQueryString);

		return lQueryString;
	}
	public static String Generatedencdata() throws Exception
	{
		String authReqString = GenAuthRequest();
		return authReqString;
	}
}