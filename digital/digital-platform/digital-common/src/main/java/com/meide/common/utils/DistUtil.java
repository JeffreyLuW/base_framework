package com.meide.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 自动寻找前端静态文件dist目录
 * @author jiay
 */
@Slf4j
public class DistUtil {
    /**
     * 从当前工作目录,上下一级目录搜索dist文件夹。
     * 当前工作目录
     *
     * @param dirNames 寻找的前端静态资源文件夹，按照顺序依次寻找
     * @return 前端静态资源文件夹
     */
    public static File findFileInWorkDir(String... dirNames) {
        String cwd = System.getProperty("user.dir");//工作目录。
        log.info("当前工作目录：{}",cwd);
        File currFile = new File(cwd);
        if (currFile.exists() && currFile.isDirectory()) {
            for (String dirName : dirNames) {
                File file = new File(currFile, dirName);
                if (file.exists() && file.isDirectory()) {
                    return file;
                }
            }
        }
        return null;
    }
}
