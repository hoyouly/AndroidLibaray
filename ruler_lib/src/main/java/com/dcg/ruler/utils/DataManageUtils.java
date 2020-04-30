package com.dcg.ruler.utils;


import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by 张明_ on 2017/7/5.
 */

public class DataManageUtils {
    /**
     * byte[]->String {0x23,0x32,0x12}-->"233212" 比如从卡里解析出身份证
     *
     * @param src
     * @return
     */
    public static String byteArrayToString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * byte[] -> ascii String
     * {0x71,0x72,0x73,0x41,0x42}->"qrsAB"
     *
     * @param byteArray byte[]
     * @return String
     */
    public static String toAsciiString(byte[] byteArray) {
        int byteLength = byteArray.length;
        StringBuilder tStringBuf = new StringBuilder();
        String nRcvString;
        char[] tChars = new char[byteLength];
        for (int i = 0; i < byteLength; i++) {
            tChars[i] = (char) byteArray[i];
        }
        tStringBuf.append(tChars);
        nRcvString = tStringBuf.toString();
        return nRcvString;
    }

    public static int L3 = 3;
    public static int W3 = 5;
    public static int H3 = 7;

    //信道3数据获取长宽高
    public static String getLWH(byte[] data, int srcPos) {
        byte[] result = new byte[2];
        try {
            System.arraycopy(data, srcPos, result, 0, 2);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return byteArrayToString(result);
    }


    //获取网点代码
    public static String getBranchCode(byte[] data) {
        byte b = data[3];
        if (b == (byte) 0xB1) {
            byte[] result = new byte[10];
            try {
                System.arraycopy(data, 4, result, 0, 10);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return byteArrayToString(result);
        }
        return null;
    }

    //获取中心条码
    public static String getCenterCode(byte[] data, byte[] data2) {
        byte b = data[3];
        byte c = data2[0];
        if (b == (byte) 0xB1 && c == (byte) 0xB2) {
            byte[] result = new byte[10];
            try {
                System.arraycopy(data, 14, result, 0, 5);
                System.arraycopy(data2, 1, result, 5, 5);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return byteArrayToString(result);
        }
        return null;
    }

    //获取目的条码
    public static String getMuDiCode(byte[] data) {
        byte b = data[0];
        if (b == (byte) 0xB2) {
            byte[] result = new byte[10];
            try {
                System.arraycopy(data, 6, result, 0, 10);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return byteArrayToString(result);
        }
        return null;
    }


    public static int L = 1;
    public static int W = 3;
    public static int H = 5;
    public static int G = 11;

    //获取长宽高重量
    public static String getLWHG(byte[] data, int srcPos) {
        byte b = data[0];
        if (b == (byte) 0xB3) {
            byte[] result = new byte[2];
            try {
                System.arraycopy(data, srcPos, result, 0, 2);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return byteArrayToString(result);
        }
        return null;
    }

    //获取体积
    public static String getV(byte[] data) {
        byte b = data[0];
        if (b == (byte) 0xB3) {
            byte[] result = new byte[4];
            try {
                System.arraycopy(data, 7, result, 0, 4);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return byteArrayToString(result);
        }
        return null;
    }

    //获取时间日期
    public static String getTime(byte[] data) {
        byte b = data[0];
        if (b == (byte) 0xB3) {
            byte[] result = new byte[6];
            try {
                System.arraycopy(data, 13, result, 0, 6);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return byteArrayToString(result);
        }
        return null;
    }

    //获取用途
    public static String getUse(byte[] data) {
        byte b = data[0];
        if (b == (byte) 0xB2) {
            byte[] result = new byte[1];
            try {
                System.arraycopy(data, 16, result, 0, 1);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return byteArrayToString(result);
        }
        return null;
    }

    //获取快递单号
    public static String getExpressCode(byte[] data, byte[] data2) {
        byte b = data[0];
        byte c = data2[0];
        if (b == (byte) 0xB4 && c == (byte) 0xB5) {
            byte[] result = new byte[20];
            try {
                System.arraycopy(data, 1, result, 0, 18);
                System.arraycopy(data2, 1, result, 18, 2);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return byteArrayToString(result);
        }
        return null;
    }

    //获取主单号
    public static String getMainCode(byte[] data, byte[] data2) {
        byte b = data[0];
        byte c = data2[0];
        if (b == (byte) 0xB5 && c == (byte) 0xB6) {
            byte[] result = new byte[20];
            try {
                System.arraycopy(data, 3, result, 0, 16);
                System.arraycopy(data2, 1, result, 16, 4);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return byteArrayToString(result);
        }
        return null;
    }

    //获取子单号
    public static String getSonCode(byte[] data, byte[] data2) {
        byte b = data[0];
        byte c = data2[0];
        if (b == (byte) 0xB6 && c == (byte) 0xB7) {
            byte[] result = new byte[20];
            try {
                System.arraycopy(data, 5, result, 0, 14);
                System.arraycopy(data2, 1, result, 14, 6);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return byteArrayToString(result);
        }
        return null;
    }

    //获取Mac
    public static String getMac(byte[] data, byte[] data2) {
        byte b = data[0];
        byte c = data2[0];
        if (b == (byte) 0xB7 && c == (byte) 0xB8) {
            byte[] result = new byte[17];
            try {
                System.arraycopy(data, 7, result, 0, 12);
                System.arraycopy(data2, 1, result, 12, 5);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return toAsciiString(result);
        }
        return null;
    }


    //获取标识位
    public static String getIdentify(byte[] data) {
        byte b = data[0];
        if (b == (byte) 0xB8) {
            byte[] result = new byte[1];
            try {
                System.arraycopy(data, 6, result, 0, 1);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return byteArrayToString(result);
        }
        return null;
    }


    //获取标记
    public static String getFlag(byte[] data) {
        byte b = data[0];
        if (b == (byte) 0xB9) {
            byte[] result = new byte[1];
            try {
                System.arraycopy(data, 17, result, 0, 1);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return byteArrayToString(result);
        }
        return null;
    }


    //获取操作员姓名
    public static String getName(byte[] data, byte[] data2) {
        byte b = data[0];
        byte c = data2[0];
        if (b == (byte) 0xB8 && c == (byte) 0xB9) {
            byte[] result = new byte[17];
            try {
                System.arraycopy(data, 7, result, 0, 12);
                System.arraycopy(data2, 1, result, 12, 5);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            int indexOf = ByteIndexOf(result, new byte[]{0x00}, 0);
            byte[] finalResult = new byte[indexOf];
            System.arraycopy(result, 0, finalResult, 0, indexOf);
            try {
                return new String(finalResult, "gb18030");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * byte数组中搜索byte[]
     *
     * @param searched
     * @param find
     * @param start
     * @return
     */
    public static int ByteIndexOf(byte[] searched, byte[] find, int start) {
        boolean matched = false;
        int end = find.length - 1;
        int skip = 0;
        for (int index = start; index <= searched.length - find.length; ++index) {
            matched = true;
            if (find[0] != searched[index] || find[end] != searched[index + end]) {
                continue;
            } else {
                skip++;
            }
            if (end > 10)
                if (find[skip] != searched[index + skip] || find[end - skip] != searched[index + end - skip]) {
                    continue;
                } else {
                    skip++;
                }
            for (int subIndex = skip; subIndex < find.length - skip; ++subIndex) {
                if (find[subIndex] != searched[index + subIndex]) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                return index;
            }
        }
        return -1;
    }


    //10进制转16进制
    public static String IntToHex(int n) {
        char[] ch = new char[20];
        int nIndex = 0;
        while (true) {
            int m = n / 16;
            int k = n % 16;
            if (k == 15) {
                ch[nIndex] = 'F';
            } else if (k == 14) {
                ch[nIndex] = 'E';
            } else if (k == 13) {
                ch[nIndex] = 'D';
            } else if (k == 12) {
                ch[nIndex] = 'C';
            } else if (k == 11) {
                ch[nIndex] = 'B';
            } else if (k == 10) {
                ch[nIndex] = 'A';
            } else {
                ch[nIndex] = (char) ('0' + k);
            }
            nIndex++;
            if (m == 0) {
                break;
            }
            n = m;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(ch, 0, nIndex);
        sb.reverse();
        return sb.toString();
    }

    //16进制转10进制
    public static int HexToInt(String strHex) {
        int nResult = 0;
        if (!IsHex(strHex))
            return nResult;
        String str = strHex.toUpperCase();
        if (str.length() > 2) {
            if (str.charAt(0) == '0' && str.charAt(1) == 'X') {
                str = str.substring(2);
            }
        }
        int nLen = str.length();
        for (int i = 0; i < nLen; ++i) {
            char ch = str.charAt(nLen - i - 1);
            try {
                nResult += (GetHex(ch) * GetPower(16, i));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return nResult;
    }

    //计算16进制对应的数值
    public static int GetHex(char ch) throws Exception {
        if (ch >= '0' && ch <= '9')
            return (int) (ch - '0');
        if (ch >= 'a' && ch <= 'f')
            return (int) (ch - 'a' + 10);
        if (ch >= 'A' && ch <= 'F')
            return (int) (ch - 'A' + 10);
        throw new Exception("error param");
    }

    //计算幂
    public static int GetPower(int nValue, int nCount) throws Exception {
        if (nCount < 0) {
            throw new Exception("nCount can't small than 1!");
        }
        if (nCount == 0) {
            return 1;
        }
        int nSum = 1;
        for (int i = 0; i < nCount; ++i) {
            nSum = nSum * nValue;
        }
        return nSum;
    }

    //判断是否是16进制数
    public static boolean IsHex(String strHex) {
        int i = 0;
        if (strHex.length() > 2) {
            if (strHex.charAt(0) == '0' && (strHex.charAt(1) == 'X' || strHex.charAt(1) == 'x')) {
                i = 2;
            }
        }
        for (; i < strHex.length(); ++i) {
            char ch = strHex.charAt(i);
            if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') || (ch >= 'a' && ch <= 'f')) {
                continue;
            }
            return false;
        }
        return true;
    }


    //获取星期代码
    public static String backWeek(String week) {
        String result = "";
        switch (week) {
            case "周日":
                result = "00";
                break;
            case "周一":
                result = "01";
                break;
            case "周二":
                result = "02";
                break;
            case "周三":
                result = "03";
                break;
            case "周四":
                result = "04";
                break;
            case "周五":
                result = "05";
                break;
            case "周六":
                result = "06";
                break;
            case "Sun":
                result = "00";
                break;
            case "Mon":
                result = "01";
                break;
            case "Tue":
                result = "02";
                break;
            case "Wed":
                result = "03";
                break;
            case "Thu":
                result = "04";
                break;
            case "Fri":
                result = "05";
                break;
            case "Sat":
                result = "06";
                break;
            default:
                break;

        }
        return result;
    }

    //获取校验位
    public static String getJiaoYan(String s1, String s2) {
        int i = Integer.parseInt(s1, 16);
        int i2 = Integer.parseInt(s2, 16);
        int intResult = i + i2;
        String jiaoYanStr = Integer.toHexString(intResult);
        if (jiaoYanStr.length() == 1) {
            jiaoYanStr = "0" + jiaoYanStr;
        }
        if (jiaoYanStr.length() > 2) {
            jiaoYanStr = jiaoYanStr.substring(1, 3);
        }
        return jiaoYanStr;
    }

    /**
     * 检验数据是否正确
     *
     * @param data    数据
     * @param tou     数据头
     * @param zhiling 指令
     * @return
     */
    public static int jiaoYanData(String data, String tou, String zhiling) {
        try {
            String[] split = data.split(" ");
            if (!split[0].equals(tou)) {
                return -1;
            }
            if (!split[1].equals(zhiling)) {
                if (split[1].equals("01")) {
                    return -2;
                } else if (split[1].equals("02")) {
                    return -3;
                }
                return -1;
            }
            String dataLength = split[2];
            int dataLengthInt = Integer.parseInt(dataLength, 16) - 1;
            int result = 0;
            if (dataLengthInt == 0) {
                result = Integer.parseInt(split[3], 16);
            } else {
                result = Integer.parseInt(split[3], 16) + Integer.parseInt(split[2 + dataLengthInt], 16);
            }

            String toHexString = toHexString(result);
            if (!split[18].equals(toHexString)) {
                return -1;
            }
            if (!split[3].equals("01")) {
                return -1;
            }
            return 0;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 检验数据是否正确
     *
     * @param data    数据
     * @param tou     数据头
     * @param zhiling 指令
     * @return
     */
    public static int jiaoYanLWHData(String data, String tou, String zhiling) {
        try {
            String[] split = data.split(" ");
            if (!split[0].equals(tou)) {
                return -1;
            }
            if (!split[1].equals(zhiling)) {
                if (split[1].equals("01")) {
                    return -2;
                } else if (split[1].equals("02")) {
                    return -3;
                }
                return -1;
            }
            String dataLength = split[2];
            int dataLengthInt = Integer.parseInt(dataLength, 16) - 1;
            int result = 0;
            if (dataLengthInt == 0) {
                result = Integer.parseInt(split[3], 16);
            } else {
                result = Integer.parseInt(split[3], 16) + Integer.parseInt(split[2 + dataLengthInt], 16);
            }

            String toHexString = toHexString(result);
            if (!split[18].equals(toHexString)) {
                return -1;
            }
            return 0;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @NonNull
    public static String toHexString(int result) {
        String toHexString = Integer.toHexString(result).toUpperCase();
        if (toHexString.length() == 1) {
            toHexString = "0" + toHexString;
        }
        if (toHexString.length() > 2) {
            toHexString = toHexString.substring(1, 3);
        }
        return toHexString;
    }

    public static int jiaoYan6(byte[] bytes1, byte[] bytes7) {
        if ((byte) 0xAA != bytes1[0]) {
            return -1;
        }
        if ((byte) 0x0A != bytes1[1]) {
            return -1;
        }
        int result = bytes1[4] + bytes7[17];
        String toHexString = Integer.toHexString(result).toUpperCase();
        if (toHexString.length() == 1) {
            toHexString = "0" + toHexString;
        }
        if (toHexString.length() > 2) {
            toHexString = toHexString.substring(toHexString.length() - 2, toHexString.length());
        }
        byte[] bytes = HexString2Bytes(toHexString);
        if (bytes[0] != bytes7[18]) {
            return -1;
        }

        return 0;
    }


    //16进制转换为ASCII
    public static String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);

            temp.append(decimal);
        }

        return sb.toString();
    }

    //ASCII码转换为16进制
    public String convertStringToHex(String str) {

        char[] chars = str.toCharArray();

        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }

        return hex.toString();
    }


    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
     * 0xD9}
     *
     * @param temp String
     * @return byte[]
     */
    public static byte[] HexString2Bytes(String temp) {
        String src = temp.replace(" ", "");
        System.out.println(" src= " + src);
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < src.length() / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    /**
     * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
     *
     * @param src0 byte
     * @param src1 byte
     * @return byte
     */
    public static byte uniteBytes(byte src0, byte src1) {
        try {
            byte _b0 = Byte.decode("0x" + new String(new byte[]{src0}))
                    .byteValue();
            _b0 = (byte) (_b0 << 4);
            byte _b1 = Byte.decode("0x" + new String(new byte[]{src1}))
                    .byteValue();
            byte ret = (byte) (_b0 ^ _b1);
            return ret;
        } catch (Exception e) {
            // TODO: handle exception
            return 0;
        }

    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    /* 中文字符地址偏移算法 */
    public static int getOffset(int c) {
        int c1, c2;
        int addr = -1;
        c1 = (c >> 8) & 0xFF;
        c2 = c & 0xFF;
        if (c1 >= 0xb0 && c2 >= 0xa1) {
            addr = ((c1 - 0xb0) * 94 + (c2 - 0xa1)) * 32;
        }

        if (c1 > 0x80 && c1 < 0xa1 && c2 >= 0x40) {
            addr = ((c1 - 0x81) * 190 + (c2 - 0x40)) * 32 + 6767 * 32;
        }

        if (c1 >= 0xaa && c2 >= 0x40 && c2 < 0xa1) {
            addr = ((c1 - 0xaa) * 96 + (c2 - 0x40)) * 32 + (6767 + 6080) * 32;
        }
        return addr;

    }

    /* 字符地址偏移算法 */
    public static int getAsciiOffset(int ch) {
        return ch * 16;     //get fontdata
    }


    /**
     * 根据偏移量获取中文矩阵点
     *
     * @param offset
     * @return
     */
    public static byte[] getChineseBytes(Context context, int offset) {
        byte[] frameBuffer = new byte[offset + 32];
        try {
            AssetManager assetManager = context.getResources().getAssets();
            InputStream inputStream = null;
            try {
                inputStream = assetManager.open("Font_GB18030.bin");
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
            if (inputStream != null) {
//                inputStream.read(frameBuffer, offset, frameBuffer.length);
                inputStream.read(frameBuffer);
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }

        return arrayCopy(frameBuffer, offset, 32);
    }

    /**
     * 根据偏移量获取中文矩阵点
     *
     * @param offset
     * @return
     */
    public static byte[] getAsciiBytes(Context context, int offset) {
        byte[] frameBuffer = new byte[offset + 16];
        try {
            AssetManager assetManager = context.getResources().getAssets();
            InputStream inputStream = null;
            try {
                inputStream = assetManager.open("Font816.bin");
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
            if (inputStream != null) {
                inputStream.read(frameBuffer);
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }

        return arrayCopy(frameBuffer, offset, 16);
    }


    /**
     * 从bytes上截取一段
     *
     * @param bytes  母体
     * @param off    起始
     * @param length 个数
     * @return byte[]
     */
    public static byte[] arrayCopy(byte[] bytes, int off, int length) {
        byte[] bytess = new byte[length];
        System.arraycopy(bytes, off, bytess, 0, length);
        return bytess;
    }
}
