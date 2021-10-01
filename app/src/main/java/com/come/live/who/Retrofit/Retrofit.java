package com.come.live.who.Retrofit;

import com.come.live.who.Modules.CountriesNumberCode;
import com.come.live.who.Modules.GiftModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.come.live.who.Global;
import com.come.live.who.Modules.ChatContent;
import com.come.live.who.Modules.ChatModule;
import com.come.live.who.Modules.Countries;
import com.come.live.who.Modules.Posts;
import com.come.live.who.Modules.Stories.Stories;
import com.come.live.who.Modules.Token;
import com.come.live.who.Modules.Users;


import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public class Retrofit {

    private static retrofit2.Retrofit retrofit;

    public static retrofit2.Retrofit getRetrofitInstance() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(Global.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public interface GetDataService {
        @HTTP(method = "GET", path = "Token")
        Call<Token> getToken(@Query("isOutGoing") boolean isOutGoing, @Query("channelName") String channelName);

    }

    public interface Auth {
        @POST("Auth/Singin")
        Call<Users> SingIn(@Body Users task);

        @POST("Auth/SingUp")
        Call<String> SingUp(@Body Users task);

        @POST("Auth/GoogleAuth")
        Call<String> GoogleAuth(@Body Users task);
    }

    public interface Data {
        @POST("data/GetUserInfo")
        Call<Users> GetProfileInfo(@Body Users users);

        @POST("data/UpdateUser")
        Call<Users> UpdateUser(@Body Users users);

        @HTTP(method = "GET", path = "data/PurchaseCoins", hasBody = true)
        Call<String> PurchaseCoins(@Query("UserId") int id, @Query("amount") int amount);

        @GET("data/GetUsers")
        Call<ArrayList<Users>> GetUsers(@Query("country") String country, @Query("userId") int id);

        @GET("data/GetAllUsers")
        Call<ArrayList<Users>> GetAllUsers();

        @HTTP(method = "GET", path = "data/NumberOfFlowers")
        Call<String> NumberOfFlowers(@Query("userid") int userId);

        @GET("data/GetAllCountries")
        Call<ArrayList<Countries>> GetAllCountries(@Query("UserId") int id);

        @GET("data/NumberCodes")
        Call<ArrayList<CountriesNumberCode>> GetCountriesNumberCode();

        @GET("data/GetPosts")
        Call<ArrayList<Posts>> GetPost(@Query("Pub") int Pub);

        @POST("data/GetUserFriends")
        Call<ArrayList<Users>> GetUserFriends(@Body Users user);

        @HTTP(method = "DELETE", path = "data/RemoveUserToFriends", hasBody = true)
        Call<String> RemoveUserToFriends(@Query("User") int UserID, @Query("Fav") int FavId);

        @HTTP(method = "GET", path = "data/addUserToFriends")
        Call<String> addUserToFriends(@Query("User") int UserID, @Query("Fav") int FavId);

        @HTTP(method = "GET", path = "data/CheckFriends")
        Call<String> CheckUserToFriends(@Query("User") int UserID, @Query("Fav") int FavId);

        @HTTP(method = "GET", path = "data/GetStories")
        Call<ArrayList<Stories>> GetStories(@Query("UserId") int id);

        @HTTP(method = "GET", path = "data/Enter_Exit_CallQueue")
        Call<String> Enter_Exit_CallQueue(@Query("isEnter") boolean isEnter, @Query("UserId") int id, @Query("into") String into);

        @HTTP(method = "GET", path = "data/MakeTransaction")
        Call<String> MakeTransaction(@Query("SenderId") int SenderId, @Query("ReciverId") int ReciverId, @Query("amount") int amount);

        @HTTP(method = "GET", path = "data/MakeTransactionToSystem")
        Call<String> MakeTransactionToSystem(@Query("SenderId") int SenderId, @Query("amount") int amount);

        @HTTP(method = "GET", path = "data/matchHistory")
        Call<ArrayList<Users>> matchHistory(@Query("userId") int userId);

        @HTTP(method = "GET", path = "data/GiftList")
        Call<ArrayList<GiftModule>> GiftList();

        @Multipart
        @POST("data/UploadImages")
        Call<String> uploadImage(@Part MultipartBody.Part part, @Part("id") RequestBody id);

        @Multipart
        @POST("data/UpdateProfileImage")
        Call<String> UpdateProfileImage(@Part MultipartBody.Part part, @Part("id") RequestBody id);


        @Multipart
        @POST("data/UploadStories")
        Call<String> uploadStory(@Part MultipartBody.Part part, @Part("id") RequestBody id, @Part("mimeType") RequestBody mimeType);

    }

    public interface UpdateUserStates {
        @HTTP(method = "GET", path = "UpdateState/UpdateUserToLive")
        Call<String> UpdateUserToLive(@Query("UserId") int id, @Query("IsStreaming") boolean IsStreaming);

        @HTTP(method = "GET", path = "UpdateState/UpdateUserToOnline")
        Call<String> UpdateUserToOnline(@Query("UserId") int id, @Query("IsOnline") boolean IsOnline);

    }

    public interface Messages {
        @HTTP(method = "GET", path = "Messages/ChatRoms")
        Call<ArrayList<ChatModule>> Messages(@Query("id") int id);

        @HTTP(method = "GET", path = "Messages/Chat")
        Call<ArrayList<ChatContent>> MessagesContent(@Query("Hash_id") String Hash_id, @Query("SenderId")
                int SenderId, @Query("receiverId") int receiverId);

        @HTTP(method = "GET", path = "Messages/Read")
        Call<String> MessagesRead(@Query("Hash_id") String Hash_id);

        @POST("Messages/send")
        Call<String> SendMessages(@Body ChatContent chatContent);

        @Multipart
        @POST("Messages/UploadImages")
        Call<ChatContent> uploadImage(@Part MultipartBody.Part part, @Part("Type") RequestBody Type);


    }


}
