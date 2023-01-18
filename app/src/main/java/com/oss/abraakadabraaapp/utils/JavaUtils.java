package com.oss.abraakadabraaapp.utils;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class JavaUtils {

    public static MultipartBody.Part profileImagePrepareFilePart(String filePath) {
        if (filePath.isEmpty()) {
            return MultipartBody.Part.createFormData("profile_image", "", RequestBody.create("", MediaType.parse("image/*")));
        } else {
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
            return MultipartBody.Part.createFormData("profile_image", file.getName(), requestFile);
        }
    }
    public static MultipartBody.Part profileImagePrepareFilePart1(String filePath) {
        if (filePath.isEmpty()) {
            return MultipartBody.Part.createFormData("image", "", RequestBody.create("", MediaType.parse("image/*")));
        } else {
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
            return MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }
    }

    public static RequestBody toRequestBody(String value) {
        return RequestBody.create(value, MediaType.parse("text/plain"));
    }

    public static MultipartBody.Part[] prepareFilePart(ArrayList<String> filePathList,String type) {
        MultipartBody.Part[] surveyImagesParts = new MultipartBody.Part[filePathList.size()];
        for (int i = 0; i < filePathList.size(); i++) {
            File file = new File(filePathList.get(i));
            RequestBody surveyBody = RequestBody.create(file, MediaType.parse("image/*"));
            surveyImagesParts[i] = MultipartBody.Part.createFormData(type+(i+1), file.getName(), surveyBody);
        }
        return surveyImagesParts;
    }
    public static MultipartBody.Part[] prepareFilePart1(ArrayList<String> filePathList,String type) {
        MultipartBody.Part[] surveyImagesParts = new MultipartBody.Part[filePathList.size()];
        for (int i = 0; i < filePathList.size(); i++) {
            File file = new File(filePathList.get(i));
            RequestBody surveyBody = RequestBody.create(file, MediaType.parse("image/*"));
            surveyImagesParts[i] = MultipartBody.Part.createFormData(type, file.getName(), surveyBody);
        }
        return surveyImagesParts;
    }


    public static MultipartBody.Part prepareFilePartSingle(String filePathList,String type) {
        File file = new File(filePathList);
        RequestBody surveyBody = RequestBody.create(file, MediaType.parse("image/*"));

        return MultipartBody.Part.createFormData(type, file.getName(), surveyBody);
    }

    public static MultipartBody.Part prepareImageFilePath(String filePath, String type) {
        if (filePath == null) {
            RequestBody requestFile = RequestBody.create("", MediaType.parse("image/*"));
            return MultipartBody.Part.createFormData(type, "", requestFile);
        } else {
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
            return MultipartBody.Part.createFormData(type, file.getName(), requestFile);
        }
    }
}
