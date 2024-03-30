package com.zyf.script;

import com.alibaba.excel.EasyExcelFactory;

/**
 * 读取表格用户信息
 */
public class UserReadExcel {
    public static void main(String[] args) {
        String fileName = "D:\\JavaWork\\projects\\partner-match\\partner-match-backend\\src\\main\\resources\\用户信息表.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcelFactory.read(fileName, UserData.class,  new UserDataListener()).sheet().doRead();
    }
}
