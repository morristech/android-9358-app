package com.xmd.technician.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sdcm on 16-4-12.
 */
public class FileUtils {

    public static boolean checkFolderExists(String filePath, boolean create) {
        return checkFileOrFoderExists(filePath, true, create);
    }

    /**
     * 判断文件是否存在，并根据create标志创建文件
     *
     * @param filePath
     * @param create
     * @return
     */
    public static boolean checkFileExist(String filePath, boolean create) {
        return checkFileOrFoderExists(filePath, false, create);
    }

    private static boolean checkFileOrFoderExists(String filePath, boolean isFolder, boolean create) {
        File file = new File(filePath);
        boolean isExist = file.exists();
        if (!isExist && create) {
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            try {
                if (isFolder) {
                    file.mkdir();
                } else {
                    file.createNewFile();
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isExist;
    }

    /**
     * 将文件流写到具体文件上
     *
     * @param is
     * @param target
     */
    public static void writeStream(InputStream is, String target) {
        boolean isExist = checkFileExist(target, true);
        if (isExist) {
            try {
                FileOutputStream fos = new FileOutputStream(target);
                byte[] buffer = new byte[64 * 1024];
                int read = 0;
                while ((read = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, read);
                }
                fos.close();
                is.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyFromAssetsToTarget(Context context, String assetFilePath, String targetFile) {
        try {
            InputStream is = context.getAssets().open(assetFilePath);
            FileUtils.writeStream(is, targetFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle){
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0,i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {

            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }
}
