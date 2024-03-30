package com.zyf.script;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 表格用户信息
 */
@Data
public class UserData {
    @ExcelProperty("用户id")
    private Long id;
    @ExcelProperty("用户昵称")
    private String username;
}
