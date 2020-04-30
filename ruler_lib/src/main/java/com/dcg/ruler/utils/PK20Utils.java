package com.dcg.ruler.utils;

import android.content.Context;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 张明_ on 2017/9/4.
 */

public class PK20Utils {

    /**
     * 获取设置时间的发送数据
     *
     * @param currentTimeMillis 时间
     * @return 设置时间的发送数据
     */
    public static String getSetTimeData(long currentTimeMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
        Date date = new Date(currentTimeMillis);
        String format = simpleDateFormat.format(date);
        String[] split = format.split(" ");
        String[] spHMS = split[1].split(":");
        String[] spYMD = split[0].split("-");
        Log.d("yanshuai", split[2]);
        String week = DataManageUtils.backWeek(split[2]);
        String year = spYMD[0].substring(2, 4);
        String jiaoYan = DataManageUtils.getJiaoYan(spHMS[2], year);
        return "FF0C08" + spHMS[2] + spHMS[1] + spHMS[0] + week + spYMD[2]
                + spYMD[1] + year + "0000000000000000" + jiaoYan + "00";
    }

    /**
     * 检测设置时间返回的数据是否正确
     *
     * @param data 设置时间返回的数据
     * @return 0为正确 -1数据格式不正确 -2重发指令 -3部分重发
     */
    public static int checkSetTimeBackData(String data) {
        return DataManageUtils.jiaoYanData(data, "FF", "0C");
    }

    /**
     * 获取设置比例的发送数据
     *
     * @param ratio int
     * @return 设置比例的发送数据
     */
    public static String getSetRatioData(int ratio) {
        if (ratio > 10000 || ratio < 1) {
            return null;
        }
        int high = ratio >> 8 & 0xff;
        int low = ratio >> 0 & 0xff;
        String highStr = DataManageUtils.toHexString(high);
        String lowStr = DataManageUtils.toHexString(low);
        String jiaoYan = DataManageUtils.getJiaoYan(highStr, lowStr);
        return "FF0D03" + highStr + lowStr +
                "00000000000000000000000000" + jiaoYan + "00";
    }

    /**
     * 检测设置比例返回的数据是否正确
     *
     * @param data 设置比例返回的数据
     * @return 0为正确 -1数据格式不正确 -2重发指令 -3部分重发
     */
    public static int checkSetRatioBackData(String data) {
        return DataManageUtils.jiaoYanData(data, "FF", "0D");
    }

    /**
     * 检测设置字库返回的数据是否正确
     *
     * @param data 设置字库返回的数据
     * @return 0为正确 -1数据格式不正确 -2重发指令 -4重发B1部分 -5重发B2部分
     */
    public static int checkSetZiKuBackData(String data) {
        int jiaoYanData = DataManageUtils.jiaoYanData(data, "FF", "0B");
        if (jiaoYanData == -1 || jiaoYanData == -2) {
            return jiaoYanData;
        } else if (jiaoYanData == -3) {
            String[] splitData = data.split(" ");
            if (splitData[3].equals("B1")) {
                return -4;
            } else if (splitData[3].equals("B2")) {
                return -5;
            }
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 获取清除流程发送数据
     *
     * @return String
     */
    public static String getCleanData() {
        return "FF0E010000000000000000000000000000000000";
    }

    /**
     * 检测清除流程返回的数据是否正确
     *
     * @param data 清除流程返回的数据
     * @return 0为正确 -1数据格式不正确 -2重发指令 -3部分重发
     */
    public static int checkCleanBackData(String data) {
        return DataManageUtils.jiaoYanData(data, "FF", "0E");
    }


    /**
     * 检测设置公司名返回的数据是否正确
     *
     * @param data 设置公司名返回的数据
     * @return 0为正确 -1数据格式不正确 -2重发指令 -4重发B1部分 -5重发B2部分
     */
    public static int checkSetLOGOBackData(String data) {
        int jiaoYanData = DataManageUtils.jiaoYanData(data, "FF", "10");
        if (jiaoYanData == -1 || jiaoYanData == -2) {
            return jiaoYanData;
        } else if (jiaoYanData == -3) {
            String[] splitData = data.split(" ");
            if (splitData[3].equals("B1")) {
                return -4;
            } else if (splitData[3].equals("B2")) {
                return -5;
            }
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 检测设置公司名返回的数据是否正确
     *
     * @param data 设置公司名返回的数据
     * @return 0为正确 -1数据格式不正确 -2重发指令 -4重发B1部分 -5重发B2部分
     */
    public static int checkSetNameBackData(String data) {
        int jiaoYanData = DataManageUtils.jiaoYanData(data, "FF", "13");
        if (jiaoYanData == -1 || jiaoYanData == -2) {
            return jiaoYanData;
        } else if (jiaoYanData == -3) {
            String[] splitData = data.split(" ");
            if (splitData[3].equals("B1")) {
                return -4;
            } else if (splitData[3].equals("B2")) {
                return -5;
            }
            return -1;
        } else {
            return 0;
        }
    }


    /**
     * 获取清除FLASH发送数据
     *
     * @return String
     */
    public static String getCleanFlashData() {
        return "FF11010000000000000000000000000000000000";
    }

    /**
     * 检测清除FLASH返回的数据是否正确
     *
     * @param data 清除FLASH返回的数据
     * @return 0为正确 -1数据格式不正确 -2重发指令 -3部分重发
     */
    public static int checkCleanFlashBackData(String data) {
        return DataManageUtils.jiaoYanData(data, "FF", "11");
    }


    /**
     * 获取设置最小比例的发送数据
     *
     * @param ratio int
     * @return 设置最小比例的发送数据
     */
    public static String getSetLeastRatioData(int ratio) {
        if (ratio > 100) {
            return null;
        }
        int high = ratio >> 8 & 0xff;
        int low = ratio >> 0 & 0xff;
        String highStr = DataManageUtils.toHexString(high);
        String lowStr = DataManageUtils.toHexString(low);
        String jiaoYan = DataManageUtils.getJiaoYan(highStr, lowStr);
        return "FF1203" + highStr + lowStr +
                "00000000000000000000000000" + jiaoYan + "00";
    }

    /**
     * 检测设置最小比例返回的数据是否正确
     *
     * @param data 设置最小比例返回的数据
     * @return 0为正确 -1数据格式不正确 -2重发指令 -3部分重发
     */
    public static int checkSetLeastRatioBackData(String data) {
        return DataManageUtils.jiaoYanData(data, "FF", "12");
    }

    /**
     * 获取发送操作员姓名数据
     *
     * @param name
     * @return
     */
    public static List<String> getNameSetData(String name) {
        List<String> list = new ArrayList<>();
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < name.length(); i++) {
                String substring = name.substring(i, i + 1);
                if (StringUtils.isChinese(substring)) {
                    byte[] gb18030s = substring.getBytes("gb18030");
                    String hex = ByteUtils.toHexString(gb18030s);
                    stringBuilder.append(hex);
                } else {
                    byte[] gb18030s = substring.getBytes("gb18030");
                    String hex = ByteUtils.toHexString(gb18030s);
                    stringBuilder.append(hex);
                }
            }
            String hexString = String.valueOf(stringBuilder);
            if (hexString != null) {
                int dataCount = hexString.length() / 2 + 1;
                String dataCountStr = DataManageUtils.toHexString(dataCount);
                //1、第一条指令
                StringBuilder data1 = new StringBuilder();
                data1.append("FF1301B1").append(dataCountStr);
                StringBuilder hexBuilder = new StringBuilder();
                hexBuilder.append(hexString);
                for (int i = 0; i < 32 - hexString.length(); i++) {
                    hexBuilder.append("0");
                }
                String oneStr = hexBuilder.substring(0, 28);
                data1.append(oneStr);
                data1.append("00");
                list.add(String.valueOf(data1));
                //2
                StringBuilder data2 = new StringBuilder();
                String twoStr = hexBuilder.substring(28, hexBuilder.length());
                String jiaoYan = DataManageUtils.getJiaoYan(hexString.substring(0, 2)
                        , hexString.substring(hexString.length() - 2, hexString.length()));
                data2.append("B2").append(twoStr).append("000000000000000000000000000000").append(jiaoYan).append("00");
                list.add(String.valueOf(data2));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 获取名字点阵数据
     *
     * @param context
     * @param string
     * @return
     */
    public static List<String> getNameDianZhen(Context context, String string) {
        List<String> list = new ArrayList<>();
        String hexResult = null;
        String part1 = null;
        String part2 = null;
        String part3 = null;
        String part4 = null;
        String part5 = null;
        String part6 = null;
        int j = 0;
        for (int i = 0; i < string.length(); i++) {
            String substring = string.substring(i, i + 1);
            try {
                if (StringUtils.isChinese(substring)) {
                    byte[] gb18030s = substring.getBytes("gb18030");
                    int toInt = ByteUtils.toInt(gb18030s);
                    int offset = DataManageUtils.getOffset(toInt);
                    byte[] chineseBytes = DataManageUtils.getChineseBytes(context, offset);
                    String hex = ByteUtils.toHexString(chineseBytes);
                    assert hex != null;
                    if (part5 != null) {
                        part1 = part5;
                        part2 = part6;
                        part5 = null;
                        part6 = null;
                    }

                    if (part1 == null) {
                        part1 = hex.substring(0, 16);
                        part3 = hex.substring(16, 32);
                        part2 = hex.substring(32, 48);
                        part4 = hex.substring(48, 64);
                    } else {
                        part3 = hex.substring(0, 16);
                        part5 = hex.substring(16, 32);
                        part4 = hex.substring(32, 48);
                        part6 = hex.substring(48, 64);
                    }
                } else {
                    byte[] gb18030s = substring.getBytes("gb18030");
                    int toInt = ByteUtils.toInt(gb18030s);
                    int offset = DataManageUtils.getAsciiOffset(toInt);
                    byte[] chineseBytes = DataManageUtils.getAsciiBytes(context, offset);
                    String hex = ByteUtils.toHexString(chineseBytes);
                    assert hex != null;
                    if (part5 != null) {
                        part1 = part5;
                        part2 = part6;
                        part5 = null;
                        part6 = null;
                    }

                    if (part1 == null) {
                        part1 = hex.substring(0, 16);
                        part2 = hex.substring(16, 32);
                    } else {
                        part3 = hex.substring(0, 16);
                        part4 = hex.substring(16, 32);
                    }
                }

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(part1).append(part3).append(part2).append(part4);
                hexResult = String.valueOf(stringBuilder);
                if (hexResult.length() == 64) {
                    j++;
                    part1 = null;
                    part2 = null;
                    part3 = null;
                    part4 = null;
                    //1
                    StringBuilder data1 = new StringBuilder();
                    data1.append("FF13").append(DataManageUtils.toHexString(j + 1)).append("B1");
                    String one = hexResult.substring(0, 30);
                    data1.append(one).append("00");
                    list.add(String.valueOf(data1));
                    //2
                    StringBuilder data2 = new StringBuilder();
                    String two = hexResult.substring(30, hexResult.length());
                    String jiaoYan = DataManageUtils.getJiaoYan(hexResult.substring(0, 2)
                            , hexResult.substring(hexResult.length() - 2, hexResult.length()));
                    data2.append("B2").append(two).append(jiaoYan).append("00");
                    list.add(String.valueOf(data2));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (part5 != null) {
            part1 = part5;
            part2 = part6;
            part5 = null;
            part6 = null;
        }
        if (part1 != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(part1).append("0000000000000000").append(part2).append("0000000000000000");
            hexResult = String.valueOf(stringBuilder);
            //1
            StringBuilder data1 = new StringBuilder();
            data1.append("FF13").append(DataManageUtils.toHexString(j + 2)).append("B1");
            String one = hexResult.substring(0, 30);
            data1.append(one).append("00");
            list.add(String.valueOf(data1));
            //2
            StringBuilder data2 = new StringBuilder();
            String two = hexResult.substring(30, hexResult.length());
            String jiaoYan = DataManageUtils.getJiaoYan(hexResult.substring(0, 2)
                    , hexResult.substring(hexResult.length() - 2, hexResult.length()));
            data2.append("B2").append(two).append(jiaoYan).append("00");
            list.add(String.valueOf(data2));
        }

        if (list.size() / 2 < 4) {
            for (int i = list.size() / 2; i < 4; i++) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("FF13").append(DataManageUtils.toHexString(i + 2))
                        .append("B1").append("00000000000000000000000000000000");
                list.add(String.valueOf(stringBuilder));
                list.add("B200000000000000000000000000000000000000");
            }
        }
        return list;
    }


    /**
     * 获取LOGO点阵数据
     *
     * @param context
     * @param string
     * @return
     */
    public static List<String> getLOGOData(Context context, String string) {
        List<String> list = new ArrayList<>();
        String hexResult = null;
        String part1 = null;
        String part2 = null;
        String part3 = null;
        String part4 = null;
        String part5 = null;
        String part6 = null;
        int j = 0;
        for (int i = 0; i < string.length(); i++) {
            String substring = string.substring(i, i + 1);
            try {
                if (StringUtils.isChinese(substring)) {
                    byte[] gb18030s = substring.getBytes("gb18030");
                    int toInt = ByteUtils.toInt(gb18030s);
                    int offset = DataManageUtils.getOffset(toInt);
                    byte[] chineseBytes = DataManageUtils.getChineseBytes(context, offset);
                    String hex = ByteUtils.toHexString(chineseBytes);
                    assert hex != null;
                    if (part5 != null) {
                        part1 = part5;
                        part2 = part6;
                        part5 = null;
                        part6 = null;
                    }

                    if (part1 == null) {
                        part1 = hex.substring(0, 16);
                        part3 = hex.substring(16, 32);
                        part2 = hex.substring(32, 48);
                        part4 = hex.substring(48, 64);
                    } else {
                        part3 = hex.substring(0, 16);
                        part5 = hex.substring(16, 32);
                        part4 = hex.substring(32, 48);
                        part6 = hex.substring(48, 64);
                    }
                } else {
                    byte[] gb18030s = substring.getBytes("gb18030");
                    int toInt = ByteUtils.toInt(gb18030s);
                    int offset = DataManageUtils.getAsciiOffset(toInt);
                    byte[] chineseBytes = DataManageUtils.getAsciiBytes(context, offset);
                    String hex = ByteUtils.toHexString(chineseBytes);
                    assert hex != null;
                    if (part5 != null) {
                        part1 = part5;
                        part2 = part6;
                        part5 = null;
                        part6 = null;
                    }

                    if (part1 == null) {
                        part1 = hex.substring(0, 16);
                        part2 = hex.substring(16, 32);
                    } else {
                        part3 = hex.substring(0, 16);
                        part4 = hex.substring(16, 32);
                    }
                }

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(part1).append(part3).append(part2).append(part4);
                hexResult = String.valueOf(stringBuilder);
                if (hexResult.length() == 64) {
                    j++;
                    part1 = null;
                    part2 = null;
                    part3 = null;
                    part4 = null;
                    //1
                    StringBuilder data1 = new StringBuilder();
                    data1.append("FF10").append(DataManageUtils.toHexString(j)).append("B1");
                    String one = hexResult.substring(0, 30);
                    data1.append(one).append("00");
                    list.add(String.valueOf(data1));
                    //2
                    StringBuilder data2 = new StringBuilder();
                    String two = hexResult.substring(30, hexResult.length());
                    String jiaoYan = DataManageUtils.getJiaoYan(hexResult.substring(0, 2)
                            , hexResult.substring(hexResult.length() - 2, hexResult.length()));
                    data2.append("B2").append(two).append(jiaoYan).append("00");
                    list.add(String.valueOf(data2));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }
        if (part5 != null) {
            part1 = part5;
            part2 = part6;
            part5 = null;
            part6 = null;
        }
        if (part1 != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(part1).append("0000000000000000").append(part2).append("0000000000000000");
            hexResult = String.valueOf(stringBuilder);
            //1
            StringBuilder data1 = new StringBuilder();
            data1.append("FF10").append(DataManageUtils.toHexString(j + 1)).append("B1");
            String one = hexResult.substring(0, 30);
            data1.append(one).append("00");
            list.add(String.valueOf(data1));
            //2
            StringBuilder data2 = new StringBuilder();
            String two = hexResult.substring(30, hexResult.length());
            String jiaoYan = DataManageUtils.getJiaoYan(hexResult.substring(0, 2)
                    , hexResult.substring(hexResult.length() - 2, hexResult.length()));
            data2.append("B2").append(two).append(jiaoYan).append("00");
            list.add(String.valueOf(data2));
        }

        if (list.size() / 2 < 4) {
            for (int i = list.size() / 2; i < 4; i++) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("FF10").append(DataManageUtils.toHexString(i + 1))
                        .append("B1").append("00000000000000000000000000000000");
                list.add(String.valueOf(stringBuilder));
                list.add("B200000000000000000000000000000000000000");
            }
        }
        return list;
    }
}
