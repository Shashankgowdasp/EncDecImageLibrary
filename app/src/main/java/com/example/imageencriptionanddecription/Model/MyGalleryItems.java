package com.example.imageencriptionanddecription.Model;

import android.graphics.Bitmap;



public class MyGalleryItems {
    private Bitmap decryptedImage;
    private String filePath;

    public MyGalleryItems(Bitmap decryptedImage, String filePath) {
        this.decryptedImage = decryptedImage;
        this.filePath = filePath;
    }

    public MyGalleryItems(String myImagePath) {
    }

    public Bitmap getDecryptedImage() {
        return decryptedImage;
    }

    public String getFilePath() {
        return filePath;
    }
}
















