package com.come.live.who;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import androidx.annotation.NonNull;

import com.come.live.who.Modules.GiftModule;
import com.come.live.who.Modules.Posts;
import com.come.live.who.Modules.Stories.Stories;
import com.come.live.who.Modules.Users;
import com.come.live.who.Retrofit.Retrofit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Global {
    public static ArrayList<com.come.live.who.Modules.Users> Users = new ArrayList<>();
    public static ArrayList<Stories> stories = new ArrayList<>();
    public static Users User = new Users();
    public static int UserId = -1;
    public static ArrayList<GiftModule> GiftList = new ArrayList<>();
    public static final String appId = "234a76013200476483700abafcdbc559";
    public static final String testChannel = "123";
    public static final String testToken = "006234a76013200476483700abafcdbc559IAAV6z2ts2Dou3UybM42eLo6uFCbOUptmpjaQz+tX70YxtJjSIgAAAAAEADSvifOc+FXYQEAAQBx4Vdh";
    //    public static final String URL = "http://192.168.1.131:3000/";
    public static final String URL = "https://comoapp.herokuapp.com/";
    public static String Room = "";

    public static void AddToFavList(int UserID, int FavId, String TAG, Callback<String> callback) {
        Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
        retrofit2.Call<String> call = service.addUserToFriends(UserID, FavId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<String> call, @NonNull Response<String> response) {
                Log.d(TAG, "onResponse: " + response.code());
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.d(TAG, "onResponse: Fail" + t.getMessage());
                callback.onFailure(call, t);
            }

        });
    }

    public static int GetUserID(Activity context) {
        SharedPreferences sharedPref = context.getSharedPreferences("Auth", 0);
        UserId = sharedPref.getInt("user_id", -1);
        return UserId;
    }

    public static void CheckFavList(int FavId, String TAG, Activity activity, Callback callback) {
        Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
        retrofit2.Call<String> call = service.CheckUserToFriends(Global.GetUserID(activity), FavId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(retrofit2.Call<String> call, Response<String> response) {
                Log.d(TAG, "onResponse: " + response.code());
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onResponse: Fail" + t.getMessage());
                callback.onFailure(call, t);

            }

        });
    }

    public static void GetStories(int id, Callback<ArrayList<Stories>> callback) {
        Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
        retrofit2.Call<ArrayList<Stories>> call = service.GetStories(id);
        call.enqueue(new Callback<ArrayList<Stories>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ArrayList<Stories>> call, @NonNull Response<ArrayList<Stories>> response) {
                Log.d("GetStories", "onResponse: " + response.code());
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Stories>> call, @NonNull Throwable t) {
                Log.d("GetStories", "onResponse: Fail" + t.getMessage());
                callback.onFailure(call, t);
            }

        });
    }

    public static void GetPosts(String TAG, int PubId, Callback callback) {
        Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
        retrofit2.Call<ArrayList<Posts>> call = service.GetPost(PubId);
        call.enqueue(new Callback<ArrayList<Posts>>() {
            @Override
            public void onResponse(retrofit2.Call<ArrayList<Posts>> call, Response<ArrayList<Posts>> response) {
                Log.d(TAG, "onResponse: " + response.code());
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ArrayList<Posts>> call, Throwable t) {
                Log.d(TAG, "onResponse: Fail" + t.getMessage());
                callback.onFailure(call, t);
            }

        });
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "BlogImage" + UUID.randomUUID(), null);
        return Uri.parse(path);
    }

    public static String getRealPathFromURI(Uri uri, Context context) {
        @SuppressLint("Recycle") Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    @SuppressLint("NewApi")
    public static Bitmap blurRenderScript(Bitmap smallBitmap, int radius, Context context) {

        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    public static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    public static void GetUserData(int Id, Callback<Users> callback) {
        Users user = new Users();
        user.setIdUser(Id);
        Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
        retrofit2.Call<Users> call = service.GetProfileInfo(user);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<Users> call, @NonNull Response<Users> response) {
                if (response.code() == 200 && response.body() != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<Users> call, @NonNull Throwable t) {

            }
        });
    }

    public static void GiftList(Callback<ArrayList<GiftModule>> callback) {
        Retrofit.Data service = Retrofit.getRetrofitInstance().create(Retrofit.Data.class);
        retrofit2.Call<ArrayList<GiftModule>> call = service.GiftList();
        call.enqueue(new Callback<ArrayList<GiftModule>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ArrayList<GiftModule>> call, @NonNull Response<ArrayList<GiftModule>> response) {
                if (response.code() == 200 && response.body() != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ArrayList<GiftModule>> call, @NonNull Throwable t) {

            }
        });
    }
}
