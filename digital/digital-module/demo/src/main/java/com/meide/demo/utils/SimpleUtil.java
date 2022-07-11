package com.meide.demo.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 简单常用工具
 */
public class SimpleUtil {

    public static boolean isEmpty(Object target) {
        if (null == target) {
            return true;
        }
        if (target instanceof String) {
            return target.equals("");
        }
        if (target instanceof Collection) {
            return ((Collection) target).isEmpty();
        }
        return false;
    }

    /**
     * 从当前工作目录,上下一级目录搜索dist文件夹。
     * 当前工作目录
     *
     * @return
     */
    public static File findDistFile() {
        String cwd = System.getProperty("user.dir");//工作目录。
        String dir = "dist";//工作目录，向下寻找dist目录，映射。
        File currFile = new File(cwd);
        File distFile = findFileInPathOrSubPath(currFile, dir);
        if (null == distFile) {
            distFile = findFileInPathOrSubPath(currFile.getParentFile(), dir);
        }
        return distFile;
    }

    //从当前目录 或者下级目录寻找指定的文件
    public static File findFileInPathOrSubPath(File file, String subFileName) {
        File distFile = null;
        File tmpFile = null;
        if (file != null && file.exists()) {
            tmpFile = new File(file, subFileName);
            if (tmpFile.exists()) {
                distFile = tmpFile;
            } else if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    if (!f.isDirectory()) continue;
                    tmpFile = new File(f, subFileName);
                    if (tmpFile.exists()) {
                        distFile = tmpFile;
                        break;
                    }
                }
            }
        }
        return distFile;
    }

    public static String base64(String text) {
        return Base64.getEncoder()
                .encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    public static String base64(BufferedImage bi, String format) {
        ByteArrayOutputStream bos = null;
        byte[] s = null;
        try {
            if (format == null) format = "jpg";
            if (format.startsWith(".")) format = format.substring(1);
            bos = new ByteArrayOutputStream();
            ImageIO.write(bi, format, bos);
            s = bos.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            IOUtils.closeQuietly(bos);
        }
        //data:image/ico;base64, data:image/jpg;base64, data:image/png;base64,
        return "data:image/" + format + ";base64," + Base64.getEncoder()
                .encodeToString(s);
    }

    public static String base64Decode(String encode) {
        String decode = new String(Base64.getDecoder().decode(encode), StandardCharsets.UTF_8);
        return decode;
    }

    public static void createDir(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                file.mkdirs();
            }
            return;
        }
        file.mkdirs();
    }

    //根据旧文件名后缀，替换新文件名
    public static String newFileName(String newFileName, String oldFileName) {
        int lastPIndex = oldFileName.lastIndexOf(".");
        return lastPIndex > 0 ? newFileName + oldFileName.substring(lastPIndex) : newFileName;
    }

    //自动注册静态资源路径
    public static void regStaticFileResouce(ResourceHandlerRegistry registry, String pathPattern, String filePath) {
        if (null == filePath) return;
        filePath = "file:" + new File(filePath).getAbsolutePath() + "/";
        registry.addResourceHandler(pathPattern).addResourceLocations(filePath);
    }

    public static void regStaticFileResouce(ResourceHandlerRegistration registration, String filePath) {
        if (null == filePath) return;
        filePath = "file:" + new File(filePath).getAbsolutePath() + "/";
        registration.addResourceLocations(filePath);
    }

    //获取请求根路径  http://lcoalhost:8991
    public static String getRequestRootUrl() {
        HttpServletRequest request = RunnerInfoHelper.request();
        String uri = request.getRequestURI().toString();
        String url = request.getRequestURL().toString();
        String origion = url.substring(0, url.indexOf(uri));
        return origion;
    }

    //简单url路径拼接
    public static String joinUrl(String prefix, String sub) {
        if (prefix.endsWith("/")) {
            //  http://lcaolhost:444/test
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        //   /hello
        if (!sub.startsWith("/")) {
            sub = "/" + sub;
        }
        String rs = prefix + sub;
        return rs;
    }

    //删除磁盘文件
    public static void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }
        file.delete();
    }

    //currPermission sys:user             permissions [sys,user,sys:test]
    //用于判断shiro结构的权限是否符合。
    public static boolean shiroHasPerm(String currPermission, Set<String> permissions) {
        boolean ok = false;
        for (String p : permissions) {
            if (p.equals("*")) {
                ok = true;
                break;
            }
            if (p.equals(currPermission)) {
                ok = true;
                break;
            }
            if (null != currPermission) {
                if (!p.endsWith(":*")) {
                    p += ":*";
                }
                p = p.replace(":*", ":[\\s\\S]*");
                if (currPermission.matches(p)) {
                    ok = true;
                    break;
                }
            }
        }
        return ok;
    }

    /**
     * 对于mybatis plus的分页，转换为标准输出格式
     *
     * @param page
     * @return
     */
    public static Map pageList2NormalMap(Page page) {
        HashMap map = new HashMap();
        map.put("pageNum", page.getCurrent());
        map.put("pageSize", page.getSize());
        map.put("pages", page.getPages());
        map.put("total", page.getTotal());
        map.put("list", page.getRecords());
        return map;
    }

    /**
     * 生成32位无 ‘-’ UUID
     * @return
     */
    public static String get32ID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /***
     * MD5加码 生成32位md5码
     */
//    public static String MD5(String inStr, String salt){
//        return new Md5Hash(inStr, salt).toString();
//    }

    public static void main(String[] args) {
        HashSet<String> set = new HashSet<>();
        set.add("sys");
//        set.add("*");

        System.out.println(shiroHasPerm("sys:test", set));
    }


}