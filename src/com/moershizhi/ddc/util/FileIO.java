package com.moershizhi.ddc.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileIO {
	
	/**
	 * 文件读取
	 * 
	 * @param path文件路劲
	 * @return
	 */
	public static String read() {
		return read(CONSTANT.XIN_JIN);
	}

	public static String read(String path) {
		// 获取文件地址
		path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + path;
		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}
		path += CONSTANT.CONFIG_FILE;

		FileInputStream fis = null;// 输入流
		String end = null;
		BufferedReader br;
		try {
			fis = new FileInputStream(new File(path));
			br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

			StringBuffer sb = new StringBuffer();
			String data = "";

			while ((data = br.readLine()) != null) {
				sb.append(data + "\n");
			}
			end = sb.toString();

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				fis.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return end;
	}

	/**
	 * 文件写入
	 * 
	 * @param path文件路劲
	 * @return
	 */
	public static boolean write(String str) {
		return write(str, CONSTANT.XIN_JIN);
	}

	public static boolean write(String str, String path) {
		// 获取文件地址
		boolean end = true;
		path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + path;

		FileOutputStream os = null;// 输出流
		try {
			// 创建文件夹及文件
			File file = new File(path);
			file.mkdir();
			if (!path.endsWith(File.separator)) {
				path += File.separator;
			}
			path += CONSTANT.CONFIG_FILE;
			file = new File(path);
			file.createNewFile();

			os = new FileOutputStream(file, false);
			os.write(str.getBytes());
		}
		catch (Exception e) {
			e.printStackTrace();
			end = false;
		}
		finally {
			try {
				os.close();
			}
			catch (Exception e) {
				e.printStackTrace();
				end = false;
			}
		}
		
		return end;
	}

	/**
	 * 在path路径下查找文件fileName
	 */
	public static String getFile(String path, String fileName) {
		File file = new File(path);
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				return getFile(files[i].getName(), fileName);
			}
			else if (files[i].getName().equals(fileName)) {
				if (!path.endsWith(File.separator)) {
					path += File.separator;
					return path;
				}
				path += fileName;
			}
		}
		
		return null;
	}

	/**
	 * 解析服务器信息 格式：服务器地址=192.168.1.2\n网络端口号=8080\n网络根目录=xinjin
	 */
	public static String parseServer(String server) {
		String msg = null;
		try {
			// 检查ip地址
			String servers[] = server.split("\n", 5);
			String ip = servers[0].split("=")[1];
			String ips[] = ip.split("\\.", 4);
			int a = Integer.parseInt(ips[0]);
			int b = Integer.parseInt(ips[1]);
			int c = Integer.parseInt(ips[2]);
			int d = Integer.parseInt(ips[3]);
			if (a < 0 || a > 255 || b < 0 || b > 255 || c < 0 || c > 255
					|| d < 0 || d > 255) {
				msg = CONSTANT.ERROR_MSG;
			}
			CONSTANT.SERVER = ip;
			// 读取服务器信息
			CONSTANT.HTTP_PORT = servers[1].split("=", 2)[1];
			CONSTANT.ROOT_PATH = servers[2].split("=", 2)[1];
			try {
				CONSTANT.HTTPS_PORT = servers[3].split("=", 2)[1];
				String ssl = servers[4].split("=", 2)[1];
				if (ssl.startsWith("true")) {
					CONSTANT.USE_SSL = true;
				}
				else {
					CONSTANT.USE_SSL = false;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			msg = CONSTANT.ERROR_MSG;
		}
		return msg;
	}

	/**
	 * 如果SDCard存在，将bitmap保存在SDCard上,成功返回true，否则返回false
	 * 
	 * @param bitmap 要保存的对象
	 * @param fileName 要保存的文件名
	 * @param filePath 父路径
	 * @throws IOException
	 * @throws Exception
	 */
	public static boolean saveBitmapOnSDCard(Bitmap bitmap, String fileName) {

		boolean isSuccess = false;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			if (bitmap != null && null != fileName && !"".equals(fileName)) {

				try {

					String root = Environment.getExternalStorageDirectory().getAbsolutePath();

					File file = new File(root + File.separator + fileName);
					if (file.exists()) {
						file.delete();
						file.createNewFile();
					}
					FileOutputStream fos = new FileOutputStream(file);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
					fos.flush();
					fos.close();
					isSuccess = true;
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return isSuccess;
	}

	/**
	 * 如果SDCard存在，文件存在，则返回一个Bitmap，否则返回null
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap createBitmapFromSDCard(String fileName) {
		Bitmap bitmap = null;
		File file = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			if (fileName != null && !"".equals(fileName)) {
				file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
			}

			if (file.exists()) {
				byte[] buffer = new byte[1024];

				try {
					FileInputStream fis = new FileInputStream(file);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int len = 0;
					while ((len = fis.read(buffer)) != -1) {
						baos.write(buffer, 0, len);
					}

					len=baos.toByteArray().length;
					bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(),
							0, len);
					fis.close();
					baos.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return bitmap;
	}

}
