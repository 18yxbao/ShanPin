package com.example.shanpin.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import androidx.core.content.FileProvider;


import com.example.shanpin.R;
import com.example.shanpin.bean.ImageBean;
import com.example.shanpin.ui.SetUserInfoActivity;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PictureUtil {
    private static final int REQUEST_IMAGE_GET = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SMALL_IMAGE_CUTTING = 2;
    private static final int REQUEST_BIG_IMAGE_CUTTING = 3;
    private static final String TAG="PictureUtil";

    //获取头像的文件路径，避免多处修改（返回当前登录用户的头像路径）
    public static String getIconPath(Context context){
        String filename=context.getExternalCacheDir() +File.separator+"user_icon";
        File bomb=new File(filename);
        if (!bomb.exists()){
            bomb.mkdirs();
        }
        filename=filename+File.separator +AccountUtil.getAccount(context)+".jpg";
        return filename;
    }

    //获取头像的文件路径，避免多处修改（根据userID返回路径，获取后需要自己判断头像是否存在）
    public static String getIconPath(Context context,String userID){
        String filename=context.getExternalCacheDir() +File.separator+"user_icon"
                +File.separator +userID+".jpg";
        return filename;
    }

    //拍照图片保存地址
    public static String getPicturePath(Context context){
        String filename=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                +File.separator+"ShanPin";
        return filename;
    }

    //接受一个文件名，给文件名加上8位的时间戳
    public static String changeImageName(String filename){
        Log.d("PictureUtil", "文件名: "+filename);
        String[] filenames=filename.split("\\.");
        String timestamp=String.valueOf(System.currentTimeMillis());
        timestamp=timestamp.substring(timestamp.length()-8);//系统时间后8位为时间戳
        filename=filenames[0]+timestamp+"."+filenames[1];
        return filename;
    }

    public static Uri getDcimUri(Context context,File file){
        Uri uri;
//        File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+
//                File.separator+"user_icon.jpg");
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context,"com.example.shanpin.fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }

        return uri;
    }

    //拍照
    public static void startCamera(Activity activity){
        Log.d("evan", "*****************打开相机********************");
//        String filename=getIconPath(activity);
        String path=getPicturePath(activity);
        File pathFile=new File(path);
        if(!pathFile.exists()){
            pathFile.mkdirs();
        }
        String filename=path+File.separator+AccountUtil.getAccount(activity)+".jpg";
        File file=new File(filename);
        Uri uri=getDcimUri(activity.getApplicationContext(),file);
        Log.d("photo", "要开始拍照了"+filename);
        Log.d("photo", "要开始拍照了"+uri);

//        Uri uri=Uri.fromFile(file);
        Log.d("PictureUtil", "文件名："+filename);
        Log.d("PictureUtil", "文件uri："+uri);

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//intent隐式调用启动拍照界面
        intent.putExtra("return-data",false);//该属性设置为false表示拍照后不会将数据返回到onResluet方法中（建议设置为false，这样获取的图片会比较清晰）
        ComponentName componentName=intent.resolveActivity(activity.getApplicationContext().getPackageManager());

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//该属性设置的是拍照后图片保存的位置

        //防止app启动意图时崩溃
        if (componentName!=null){
            activity.startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
        }
    }

    //从相册中选取
    public static void startPhoto(Activity activity){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//intent隐式调用启动相册界面
        intent.setType("image/*");//设置数据类型
        ComponentName componentName = intent.resolveActivity(activity.getApplicationContext().getPackageManager());
        Log.d("pic", "startPhoto: "+componentName);
        if (componentName != null) {//防止启动意图时app崩溃
            activity.startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    //裁剪选取或拍摄的图片
    public static void cropphoto(Activity activity, Uri uri){
        //设置裁剪图片保存位置
        String filename=getIconPath(activity);
        File file=new File(filename);

        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Intent intent=new Intent("com.android.camera.action.CROP");//intent隐式调用裁剪窗口

        intent.setDataAndType(uri,"image/*");//设置需要裁剪的图片地址
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);//设置图片宽高比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 240);//设置图片宽高
        intent.putExtra("outputY", 240);
        intent.putExtra("return-data", false);//该属性设置为false表示拍照后不会将数据返回到onResluet方法中（建议设置为false，这样获取的图片会比较清晰）
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));//该属性设置的是拍照后图片保存的位置
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//设置输出格式
        intent.putExtra("noFaceDetection", true);//是否取消人脸识别

        activity.startActivityForResult(intent,REQUEST_SMALL_IMAGE_CUTTING);
    }

    public static boolean readIconFromFile(Context context, ImageView imageView){
        BitmapFactory.Options op=new BitmapFactory.Options();
        op.inPreferredConfig=Bitmap.Config.ARGB_4444;
        String path=getIconPath(context);
        Bitmap icon=BitmapFactory.decodeFile(path,op);//加载本地图片，并获取大小合适的bitmap

        if (icon!=null){
            imageView.setImageBitmap(icon);//将获取的图片设置到imagerview
            return true;
        }
        return false;
    }

    public static boolean readPictureFromFile(Context context, ImageView imageView,String path){
        BitmapFactory.Options op=new BitmapFactory.Options();
        op.inPreferredConfig=Bitmap.Config.ARGB_4444;
        Bitmap icon=BitmapFactory.decodeFile(path,op);//加载本地图片，并获取大小合适的bitmap

        if (icon!=null){
            imageView.setImageBitmap(icon);//将获取的图片设置到imagerview
            return true;
        }
        return false;
    }

    public static void loadImageFromUrl(ImageView imageView,String urlString) {
        Log.d(TAG, "loadImage: "+urlString);
        try {
            URL url = new URL(urlString);
            imageView.setImageBitmap(BitmapFactory.decodeStream(url.openStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadImage(String url, final String filename) {
        Log.d(TAG, "downloadImage: url="+url+";filename="+filename);
//        RequestBody requestBody=new FormBody.Builder().build();
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: 网络不好，下载图片失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                InputStream is=response.body().byteStream();
                int len=0;
                File file=new File(filename);
                if (!file.exists()){
                    file.mkdirs();
                }
                FileOutputStream fos=new FileOutputStream(filename);
                byte[] buf=new byte[128];
                while ((len=is.read(buf))!=-1){
                    fos.write(buf);
                }
                fos.flush();
                fos.close();
                is.close();
            }
        };
        Okhttp.sentGet(url,callback);
    }

    public static void downloadImage(@NotNull String url, final String filename,final Activity activity, final ImageView imageView) {
        Log.d(TAG, "downloadImage: url="+url+";  filename="+filename);
//        RequestBody requestBody=new FormBody.Builder().build();
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: 网络不好，下载图片失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final InputStream is=response.body().byteStream();
                int len=0;
                File file=new File(filename);
//                if (!file.exists()){
//                    file.createNewFile();
//                }
                FileOutputStream fos=new FileOutputStream(file);
                byte[] buf=new byte[128];
                while ((len=is.read(buf))!=-1){
                    fos.write(buf);
                }
                fos.flush();
                fos.close();
                is.close();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        readPictureFromFile(activity,imageView,filename);
                    }
                });
            }
        };
        Okhttp.sentGet(url,callback);
    }

    public static String getFilePathByUri(Context context, Uri uri) {
        String path = null;
        // 以 file:// 开头的
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            path = uri.getPath();
            return path;
        }
        // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
            return path;
        }
        // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        path = Environment.getExternalStorageDirectory() + "/" + split[1];
                        return path;
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(id));
                    path = getDataColumn(context, contentUri, null, null);
                    return path;
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    path = getDataColumn(context, contentUri, selection, selectionArgs);
                    return path;
                }
            }
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /*
    //采样率压缩（根据路径获取图片并压缩）：
    public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        try {
//            String filePath = file.getAbsolutePath();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;//开始读入图片，此时把options.inJustDecodeBounds 设回true了
            BitmapFactory.decodeFile(filePath, options);//此时返回bm为空
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);//设置缩放比例 数值越高，图片像素越低
            options.inJustDecodeBounds = false;//重新读入图片，注意此时把options.inJustDecodeBounds 设回false了
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            //压缩好比例大小后不进行质量压缩
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到bos中
//            bos.flush();
//            bos.close();
            //压缩好比例大小后再进行质量压缩
            compressImage(bitmap,filePath);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        try {
            int height = options.outHeight;
            int width = options.outWidth;
            int inSampleSize = 1;  //1表示不缩放
            if (height > reqHeight || width > reqWidth) {
                int heightRatio = Math.round((float) height / (float) reqHeight);
                int widthRatio = Math.round((float) width / (float) reqWidth);
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }
            return inSampleSize;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    // 质量压缩法：
    public static Bitmap compressImage(Bitmap imageBitmap, String filepath) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > 500) {    //循环判断如果压缩后图片是否大于500kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                options -= 10;//每次都减少10
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

            }
            //压缩好后写入文件中
            FileOutputStream fos = new FileOutputStream(filepath);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            return imageBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

     */

}
