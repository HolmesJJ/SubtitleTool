package com.example.subtitletool.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    // /storage/emulated/0/Android/data/com.example.subtitletool/files/
    public static final String SDCARD_PATH = ContextUtils.getContext().getExternalFilesDir(null).getAbsolutePath() + File.separator;
    // /data/user/0/com.example.subtitletool/files/
    public static final String DATA_PATH = ContextUtils.getContext().getFilesDir().getAbsolutePath() + File.separator;
    public static final String APP_DIR = SDCARD_PATH + "SubtitleTool" + File.separator;
    public static final String DATA_APP_DIR = DATA_PATH + "SubtitleTool" + File.separator;
    public static final String USERS_DATA_DIR = DATA_APP_DIR + "users" + File.separator;

    public static void init() {
        File app = new File(APP_DIR);
        if (!app.exists()) {
            app.mkdirs();
        }
        //保存用户信息
        File users = new File(USERS_DATA_DIR);
        if (!users.exists()) {
            users.mkdirs();
        }
    }

    /**
     * 保存byte数组
     *
     * @param context context
     * @param path    保存路径
     * @param data    byte数组
     */
    public static void saveFile(Context context, String path, byte[] data) {
        OutputStream os = null;
        try {
            File file = new File(path);
            File parentFile = file.getParentFile();
            if (parentFile == null || !parentFile.exists() || !parentFile.isDirectory()) {
                parentFile.mkdirs();
            }
            os = new FileOutputStream(file);
            os.write(data, 0, data.length);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 保存字符串
     *
     * @param context context
     * @param path    文件 保存路径
     * @param content 待保存的内容
     */
    public static void saveFile(Context context, String path, String content) {
        if (TextUtils.isEmpty(path) || content == null || context == null) {
            return;
        }
        BufferedOutputStream bf = null;
        try {
            File file = new File(path);
            File parentFile = file.getParentFile();
            if (parentFile == null || !parentFile.exists() || !parentFile.isDirectory()) {
                parentFile.mkdirs();
            }
            bf = new BufferedOutputStream(new FileOutputStream(file));
            bf.write(content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从assets目录下获取文件的byte数组
     *
     * @param context context
     * @param path    文件在assets目录下的 路径
     *
     * @return 返回文件的byte数组
     */
    public static byte[] getAssetsData(Context context, String path) {
        InputStream stream = null;
        try {
            stream = context.getAssets().open(path);
            int length = stream.available();
            byte[] data = new byte[length];
            stream.read(data);
            stream.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param path 文件路径
     */
    public static void deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        try {
            File file = new File(path);
            if (file != null) {
                file.delete();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 删除目录
     *
     * @param path 目录路径
     */
    public static void deleteDirectory(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        try {
            File dir = new File(path);
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (String child : children) {
                    new File(dir, child).delete();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static byte[] getFileBytes(File file) throws Exception {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            return bytes;
        } finally {
            IoUtils.closeQuietly(is);
        }
    }
}
