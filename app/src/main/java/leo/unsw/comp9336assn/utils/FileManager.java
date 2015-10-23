package leo.unsw.comp9336assn.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.LogManager;

import android.R;
import android.content.Context;
import android.util.Log;

public class FileManager {

    /**
     * 读文件
     *
     * @param src
     * @return
     */
    public static String readInStream(String src) {
        try {
            File file = new File(src);
            FileInputStream inStream = new FileInputStream(file);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }
            outStream.close();
            inStream.close();
            return outStream.toString();
        } catch (IOException e) {
            Log.i("test", e.toString());
        }
        return null;
    }

    /**
     * 写文件
     *
     * @param filePath
     * @param name
     * @param content
     */
    public static void writeFile(String filePath, String name, String content) {
        //如果filePath是传递过来的参数，可以做一个后缀名称判断； 没有指定的文件名没有后缀，则自动保存为.txt格式
//    	if(!filePath.endsWith(".txt") && !filePath.endsWith(".log")) 
//    		filePath += ".txt";
        //保存文件

        File tmpFile = new File(filePath);
        if (!tmpFile.exists()) {
            boolean isMk = tmpFile.mkdir();
            System.out.println(isMk);
        }
        File file = new File(filePath + "/" + name);
        if (!file.exists()) {

            try {
                //Log.i(tag, "-----------------创建日志");
                boolean iscr = file.createNewFile();
                System.out.println(iscr);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                Log.v("test", e1.toString());
            }
        }
        FileWriter writer;
        try {
            writer = new FileWriter(filePath + "/" + name, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static void Copy(InputStream in, String DesPath, String DesName, Context context) throws Exception {
        int length = 2097152;
        File file = mkFile(DesPath, DesName, true);
        FileOutputStream out = new FileOutputStream(DesPath + "/" + DesName);
        byte[] buffer = new byte[length];
        while (true) {
            int ins = in.read(buffer);
            if (ins == -1) {
                in.close();
                out.flush();
                out.close();
            } else
                out.write(buffer, 0, ins);
        }
    }

    /**
     * 建立新的路径
     *
     * @param path      路径结尾不加/
     * @param name      文件名
     * @param OverWrite 如果存在是否覆盖？
     */
    public static File mkFile(String path, String name, boolean OverWrite) {
        File tmpFloder = new File(path);
        if (!tmpFloder.exists()) {
            tmpFloder.mkdirs();
        }
        File tmpFile = new File(path + "/" + name);
        if (tmpFile.exists()) {
            if (OverWrite) {
                tmpFile.delete();
                try {
                    tmpFile.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.v("test", e.toString());
                }
            }

        } else {
            try {
                tmpFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.v("test", e.toString());
            }
        }
        return tmpFile;

    }

}
