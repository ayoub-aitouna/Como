package com.come.live.who.Modules;

import com.google.gson.annotations.SerializedName;

public class Users {
    @SerializedName("name")
    String name;
    @SerializedName("phoneNumber")
    String phoneNumber;
    @SerializedName("gender")
    String gender;
    @SerializedName("country")
    String country;
    @SerializedName("about")
    String about;
    @SerializedName("job")
    String job;
    @SerializedName("education")
    String education;
    @SerializedName("profileImage")
    String profileImage;
    @SerializedName("email")
    String email;
    @SerializedName("password")
    String password;
    @SerializedName("_Into")
    String Into;
    @SerializedName("IsStreaming")
    int IsStreaming;
    @SerializedName("IsAvailable")
    int IsAvailable;
    @SerializedName("confirmationStatus")
    int confirmationStatus;
    @SerializedName("activationStatus")
    int activationStatus;
    @SerializedName("idUser")
    int idUser;
    @SerializedName("coins")
    int coins;
    @SerializedName("age")
    int age;

    public Users() {
    }

    public Users(String name, String phoneNumber, String gender, String country, String about, String job, String education, String profileImage, String email, String password, String into, int isStreaming, int isAvailable, int confirmationStatus, int activationStatus, int idUser, int coins, int age) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.country = country;
        this.about = about;
        this.job = job;
        this.education = education;
        this.profileImage = profileImage;
        this.email = email;
        this.password = password;
        Into = into;
        IsStreaming = isStreaming;
        IsAvailable = isAvailable;
        this.confirmationStatus = confirmationStatus;
        this.activationStatus = activationStatus;
        this.idUser = idUser;
        this.coins = coins;
        this.age = age;
    }

    public String getInto() {
        return Into;
    }

    public void setInto(String into) {
        Into = into;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIsStreaming() {
        return IsStreaming;
    }

    public void setIsStreaming(int isStreaming) {
        IsStreaming = isStreaming;
    }

    public int getIsAvailable() {
        return IsAvailable;
    }

    public void setIsAvailable(int isAvailable) {
        IsAvailable = isAvailable;
    }

    public int getConfirmationStatus() {
        return confirmationStatus;
    }

    public void setConfirmationStatus(int confirmationStatus) {
        this.confirmationStatus = confirmationStatus;
    }

    public int getActivationStatus() {
        return activationStatus;
    }

    public void setActivationStatus(int activationStatus) {
        this.activationStatus = activationStatus;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
