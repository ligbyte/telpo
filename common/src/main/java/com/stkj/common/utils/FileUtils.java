package com.stkj.common.utils;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.List;


/**
 * 文件存储工具类
 */
public class FileUtils {

    private static final long KB_DIVIDE = 1024;
    private static final long MB_DIVIDE = 1048576;
    private static final long GB_DIVIDE = 1073741824;


    /**
     * 删除file
     *
     * @param cameraFilePath 指定文件路径
     */
    public static boolean deleteFileByPath(String cameraFilePath) {
        if (TextUtils.isEmpty(cameraFilePath)) {
            return false;
        }
        File file = new File(cameraFilePath);
        if (file.exists()) {
            file.delete();
            Log.d("delete file by path %s", cameraFilePath);
            return true;
        }
        return false;
    }

    /**
     * 删除文件夹
     */
    public static void deleteDirectory(File dirFile) {
        if (dirFile == null) {
            return;
        }
        File[] files = dirFile.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }
        dirFile.delete();

    }

    /**
     * 删除文件夹
     */
    public static void deleteDirectory(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File dirFile = new File(path);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return;
        }
        if (dirFile.isFile()) {
            dirFile.delete();
            return;
        }
        deleteDirectory(dirFile);
    }


    /**
     * 获取指定文件大小
     */
    public static long getFileSize(File file) {
        if (file == null) {
            return 0;
        }
        if (!file.exists()) {
            return 0;
        }
        return file.length();
    }

    /**
     * 获取指定文件夹
     */
    public static long getDirFileSize(File f) {
        if (f == null) {
            return 0;
        }
        long size = 0;
        File fList[] = f.listFiles();
        if (fList == null) {
            return 0;
        }
        for (File aFList : fList) {
            if (aFList.isDirectory()) {
                size = size + getDirFileSize(aFList);
            } else {
                size = size + getFileSize(aFList);
            }
        }
        return size;
    }

    /**
     * 转换文件大小(b,kb,mb,g)
     */
    public static String formatFileSize(long fileS) {
        String fileSizeString;
        if (fileS < KB_DIVIDE) {
            fileSizeString = fileS + "B";
        } else if (fileS < MB_DIVIDE) {
            fileSizeString = fileS / KB_DIVIDE + "KB";
        } else if (fileS < GB_DIVIDE) {
            fileSizeString = fileS / MB_DIVIDE + "MB";
        } else {
            DecimalFormat df = new DecimalFormat("#0.00");
            fileSizeString = df.format(fileS * 1.0f / GB_DIVIDE) + "G";
        }
        return fileSizeString;
    }

    public static boolean mergeFiles(File outFile, List<File> fileList) {
        if (outFile == null || outFile.isDirectory()) {
            Log.d("FileUtils", "--mergeFiles  outFile is null--");
            return false;
        }
        if (fileList == null || fileList.isEmpty()) {
            Log.d("FileUtils", "--mergeFiles  fileList is null--");
            return false;
        }
        if (outFile.exists()) {
            outFile.delete();
        }
        FileChannel outChannel = null;
        try {
            int BUFF_SIZE = 1024 * 128;
            outChannel = new FileOutputStream(outFile).getChannel();
            for (File f : fileList) {
                FileChannel fc = new FileInputStream(f).getChannel();
                ByteBuffer bb = ByteBuffer.allocate(BUFF_SIZE);
                while (fc.read(bb) != -1) {
                    bb.flip();
                    outChannel.write(bb);
                    bb.clear();
                }
                fc.close();
            }
            Log.d("FileUtils", "--mergeFiles success--");
            return true;
        } catch (IOException ioe) {
            Log.d("FileUtils", "--mergeFiles error--");
        } finally {
            try {
                if (outChannel != null) {
                    outChannel.close();
                }
            } catch (IOException e) {
                Log.d("FileUtils", "--mergeFiles error--");
            }
        }
        return false;
    }

    public static boolean mergeFiles(FileOutputStream outputStream, List<File> fileList) {
        if (outputStream == null) {
            Log.d("FileUtils", "--mergeFiles  outFile is null--");
            return false;
        }
        if (fileList == null || fileList.isEmpty()) {
            Log.d("FileUtils", "--mergeFiles  fileList is null--");
            return false;
        }
        FileChannel outChannel = null;
        try {
            int BUFF_SIZE = 1024 * 128;
            outChannel = outputStream.getChannel();
            for (File f : fileList) {
                FileChannel fc = new FileInputStream(f).getChannel();
                ByteBuffer bb = ByteBuffer.allocate(BUFF_SIZE);
                while (fc.read(bb) != -1) {
                    bb.flip();
                    outChannel.write(bb);
                    bb.clear();
                }
                fc.close();
            }
            Log.d("FileUtils", "--mergeFiles success--");
            return true;
        } catch (IOException ioe) {
            Log.d("FileUtils", "--mergeFiles error--");
        } finally {
            try {
                if (outChannel != null) {
                    outChannel.close();
                }
            } catch (IOException e) {
                Log.d("FileUtils", "--mergeFiles error--");
            }
        }
        return false;
    }

    /**
     * 复制文件到指定路径
     */
    public static boolean copyFileToOtherFile(File file, File otherFile) {
        if (file == null || !file.exists()) {
            Log.d("FileUtils", "--file not exist--");
            return false;
        }
        if (file.isDirectory()) {
            Log.d("FileUtils", "--file is a dir--");
            return false;
        }
        if (otherFile == null || otherFile.isDirectory()) {
            Log.d("FileUtils", "--other file is null--");
            return false;
        }
        if (otherFile.exists()) {
            otherFile.delete();
        }

        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(file);
            fo = new FileOutputStream(otherFile);
            in = fi.getChannel();
            out = fo.getChannel();
            in.transferTo(0, in.size(), out);
            Log.d("FileUtils", "--copyFileToOtherFile success--");
        } catch (IOException e) {
            Log.d("FileUtils", "--copyFileToOtherFile error--");
        } finally {
            try {
                if (fi != null) {
                    fi.close();
                }
                if (in != null) {
                    in.close();
                }
                if (fo != null) {
                    fo.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                Log.d("FileUtils", "--copyFileToOtherFile error--");
            }
        }
        return true;
    }

    public static boolean copyFileToOtherFile(File file, FileOutputStream outputStream) {
        if (file == null || !file.exists()) {
            Log.i("FileUtils", "--file not exist--");
            return false;
        }
        if (file.isDirectory()) {
            Log.i("FileUtils", "--file is a dir--");
            return false;
        }

        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(file);
            fo = outputStream;
            in = fi.getChannel();
            out = fo.getChannel();
            in.transferTo(0, in.size(), out);
            Log.i("FileUtils", "--copyFileToOtherFile success--");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("FileUtils", "--copyFileToOtherFile error--");
        } finally {
            try {
                if (fi != null) {
                    fi.close();
                }
                if (in != null) {
                    in.close();
                }
                if (fo != null) {
                    fo.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("FileUtils", "--copyFileToOtherFile error--");
            }
        }
        return true;
    }

    public static boolean copyFileToOtherFile(FileInputStream inputStream, File file) {
        if (file == null) {
            Log.i("FileUtils", "--file not exist--");
            return false;
        }
        if (file.isDirectory()) {
            Log.i("FileUtils", "--file is a dir--");
            return false;
        }

        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = inputStream;
            fo = new FileOutputStream(file);
            in = fi.getChannel();
            out = fo.getChannel();
            in.transferTo(0, in.size(), out);
            Log.i("FileUtils", "--copyFileToOtherFile success--");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("FileUtils", "--copyFileToOtherFile error--");
        } finally {
            try {
                if (fi != null) {
                    fi.close();
                }
                if (in != null) {
                    in.close();
                }
                if (fo != null) {
                    fo.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("FileUtils", "--copyFileToOtherFile error--");
            }
        }
        return true;
    }

    public static byte[] file2Bytes(String filePath) {
        int byte_size = 1024;
        byte[] b = new byte[byte_size];
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
                    byte_size);
            for (int length; (length = fileInputStream.read(b)) != -1; ) {
                outputStream.write(b, 0, length);
            }
            fileInputStream.close();
            outputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            Log.d("FileUtils", "--file2Bytes error--");
        }
        return null;
    }

    static boolean deleteAllInDir(final File dir) {
        return FileUtils.deleteAllInDir(dir);
    }

    /**
     * Return the file by path.
     *
     * @param filePath The path of file.
     * @return the file
     */
    public static File getFileByPath(final String filePath) {
        return UtilsBridge.isSpace(filePath) ? null : new File(filePath);
    }

    /**
     * Return whether the file exists.
     *
     * @param file The file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFileExists(final File file) {
        if (file == null) return false;
        if (file.exists()) {
            return true;
        }
        return isFileExists(file.getAbsolutePath());
    }


    /**
     * Return whether the file exists.
     *
     * @param filePath The path of file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFileExists(final String filePath) {
        File file = getFileByPath(filePath);
        if (file == null) return false;
        if (file.exists()) {
            return true;
        }
        return isFileExistsApi29(filePath);
    }

    private static boolean isFileExistsApi29(String filePath) {
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                Uri uri = Uri.parse(filePath);
                ContentResolver cr = Utils.getApp().getContentResolver();
                AssetFileDescriptor afd = cr.openAssetFileDescriptor(uri, "r");
                if (afd == null) return false;
                try {
                    afd.close();
                } catch (IOException ignore) {
                }
            } catch (FileNotFoundException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Create a file if it doesn't exist, otherwise delete old file before creating.
     *
     * @param filePath The path of file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean createFileByDeleteOldFile(final String filePath) {
        return createFileByDeleteOldFile(getFileByPath(filePath));
    }


    /**
     * Create a file if it doesn't exist, otherwise delete old file before creating.
     *
     * @param file The file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean createFileByDeleteOldFile(final File file) {
        if (file == null) return false;
        // file exists and unsuccessfully delete then return false
        if (file.exists() && !file.delete()) return false;
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Notify system to scan the file.
     *
     * @param filePath The path of file.
     */
    public static void notifySystemToScan(final String filePath) {
        notifySystemToScan(getFileByPath(filePath));
    }

    /**
     * Notify system to scan the file.
     *
     * @param file The file.
     */
    public static void notifySystemToScan(final File file) {
        if (file == null || !file.exists()) return;
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.parse("file://" + file.getAbsolutePath()));
        Utils.getApp().sendBroadcast(intent);
    }

    static long getFsAvailableSize(String path) {
        return FileUtils.getFsAvailableSize(path);
    }

    /**
     * Return the total size of file system.
     *
     * @param anyPathInFs Any path in file system.
     * @return the total size of file system
     */
    public static long getFsTotalSize(String anyPathInFs) {
        if (TextUtils.isEmpty(anyPathInFs)) return 0;
        StatFs statFs = new StatFs(anyPathInFs);
        long blockSize;
        long totalSize;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.getBlockSizeLong();
            totalSize = statFs.getBlockCountLong();
        } else {
            blockSize = statFs.getBlockSize();
            totalSize = statFs.getBlockCount();
        }
        return blockSize * totalSize;
    }

    /**
     * Create a directory if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * Create a file if it doesn't exist, otherwise do nothing.
     *
     * @param filePath The path of file.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsFile(final String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }

    /**
     * Create a file if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsFile(final File file) {
        if (file == null) return false;
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
