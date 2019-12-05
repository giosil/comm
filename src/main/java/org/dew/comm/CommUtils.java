package org.dew.comm;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public 
class CommUtils 
{
	public static
	String getString(byte[] buffer)
	{
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < buffer.length; i++) {
			byte b = buffer[i];
			if(b < 1) break;
			if(b ==  1) sb.append("<SOH>"); else
			if(b ==  2) sb.append("<STX>"); else
			if(b ==  3) sb.append("<ETX>"); else
			if(b ==  4) sb.append("<EOT>"); else
			if(b ==  5) sb.append("<ENQ>"); else
			if(b ==  6) sb.append("<ACK>"); else
			if(b ==  7) sb.append("<BEL>"); else
			if(b ==  8) sb.append("<BS>");  else
			if(b ==  9) sb.append("<TAB>"); else
			if(b == 10) sb.append("<LF>");  else
			if(b == 11) sb.append("<VT>");  else
			if(b == 12) sb.append("<FF>");  else
			if(b == 13) sb.append("<CR>");  else
			if(b == 14) sb.append("<SO>");  else
			if(b == 15) sb.append("<SI>");  else
			if(b == 16) sb.append("<DLE>"); else
			if(b == 17) sb.append("<DC1>"); else
			if(b == 18) sb.append("<DC2>"); else
			if(b == 19) sb.append("<DC3>"); else
			if(b == 20) sb.append("<DC4>"); else
			if(b == 21) sb.append("<NAK>"); else
			if(b == 22) sb.append("<SYN>"); else
			if(b == 23) sb.append("<ETB>"); else
			if(b == 24) sb.append("<CAN>"); else
			if(b == 25) sb.append("<EM>");  else
			if(b == 26) sb.append("<SUB>"); else
			if(b == 27) sb.append("<ESC>"); else
			if(b == 28) sb.append("<FS>");  else
			if(b == 29) sb.append("<GS>");  else
			if(b == 30) sb.append("<RS>");  else
			if(b == 31) sb.append("<US>");  else 
				sb.append((char) b);
		}
		return sb.toString();
	}
	
	public static
	byte[] getBytes(String sBuffer)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if(sBuffer != null && sBuffer.length() > 0) {
			int iBufferLength = sBuffer.length();
			for(int i = 0; i < iBufferLength; i++) {
				char c = sBuffer.charAt(i);
				if(c == '<') {
					String sCod = "";
					// Carattere di controllo
					if(i + 3 < iBufferLength) {
						if(sBuffer.charAt(i + 3) == '>') {
							sCod = sBuffer.substring(i, i + 4);
							i = i + 3;
						}
					}
					else
					if(i + 4 < iBufferLength) {
						if(sBuffer.charAt(i + 4) == '>') {
							sCod = sBuffer.substring(i, i + 5);
							i = i + 4;
						}
					}
					if(sCod.length() == 0)   baos.write(c);  else
					if(sCod.equals("<SOH>")) baos.write(1);  else
					if(sCod.equals("<STX>")) baos.write(2);  else
					if(sCod.equals("<ETX>")) baos.write(3);  else
					if(sCod.equals("<EOT>")) baos.write(4);  else
					if(sCod.equals("<ENQ>")) baos.write(5);  else
					if(sCod.equals("<ACK>")) baos.write(6);  else
					if(sCod.equals("<BEL>")) baos.write(7);  else
					if(sCod.equals("<BS>"))  baos.write(8);  else
					if(sCod.equals("<TAB>")) baos.write(9);  else
					if(sCod.equals("<LF>"))  baos.write(10); else
					if(sCod.equals("<VT>"))  baos.write(11); else
					if(sCod.equals("<FF>"))  baos.write(12); else
					if(sCod.equals("<CR>"))  baos.write(13); else
					if(sCod.equals("<SO>"))  baos.write(14); else
					if(sCod.equals("<SI>"))  baos.write(15); else
					if(sCod.equals("<DLE>")) baos.write(16); else
					if(sCod.equals("<DC1>")) baos.write(17); else
					if(sCod.equals("<DC2>")) baos.write(18); else
					if(sCod.equals("<DC3>")) baos.write(19); else
					if(sCod.equals("<DC4>")) baos.write(20); else
					if(sCod.equals("<NAK>")) baos.write(21); else
					if(sCod.equals("<SYN>")) baos.write(22); else
					if(sCod.equals("<ETB>")) baos.write(23); else
					if(sCod.equals("<CAN>")) baos.write(24); else
					if(sCod.equals("<EM>"))  baos.write(25); else
					if(sCod.equals("<SUB>")) baos.write(26); else
					if(sCod.equals("<ESC>")) baos.write(27); else
					if(sCod.equals("<FS>"))  baos.write(28); else
					if(sCod.equals("<GS>"))  baos.write(29); else
					if(sCod.equals("<RS>"))  baos.write(30); else
					if(sCod.equals("<US>"))  baos.write(31);
				}
				else {
					baos.write(c);
				}
			}
		}
		return baos.toByteArray();
	}

	public static
	byte[] getASTMFrame(int iFrameSequenceNumber, byte bET, byte[] abRecord, int iOffset, int iLength)
			throws Exception
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write((byte) 2); // STX
		baos.write(String.valueOf(iFrameSequenceNumber).getBytes()); // Frame Sequence Number
		baos.write(abRecord, iOffset, iLength);
		baos.write(bET); // ETB | ETX
		baos.write('0'); // Check Sum 1
		baos.write('0'); // Check Sum 2
		baos.write((byte) 13); // CR
		baos.write((byte) 10); // LF
		byte[] abResult = baos.toByteArray();
		// Calcolo check sum
		int iCheckSum = 0;
		for(int i = 1; i < abResult.length - 4; i++) {
			iCheckSum = (byte) iCheckSum + abResult[i];
		}
		String sHex = Integer.toHexString(iCheckSum).toUpperCase();
		if(sHex.length() == 0) sHex = "00"; else
			if(sHex.length() == 1) sHex = "0" + sHex; else
				if(sHex.length() > 2) sHex = sHex.substring(sHex.length() - 2, sHex.length());
		// Impostazione check sum
		byte[] abCheckSum = sHex.getBytes();
		abResult[abResult.length - 4] = abCheckSum[0];
		abResult[abResult.length - 3] = abCheckSum[1];
		return abResult;
	}
	
	public static
	void append(OutputStream os, byte[] buffer)
		throws Exception
	{
		for(int i = 0; i < buffer.length; i++) {
			byte b = buffer[i];
			if(b == 0) break;
			os.write(b);
		}
	}	
}
