package com.example.imageencriptionanddecription;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imageencriptionanddecription.Model.Image;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private ArrayList<Image> galleryItems;
    private Context context;

    ImageView imageView;
    private static final String AES_KEY = "YourSecretKey123";


    public GalleryAdapter(Context context, ArrayList<Image> galleryItems) {
        this.context = context;
        this.galleryItems = galleryItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Image galleryItem = null;
        try {
            galleryItem = galleryItems.get(position);
            byte[] encryptedbyteCode = Base64.decode(galleryItem.getBase64(), Base64.DEFAULT);
            byte[] decryptedImageByte = AESEncryptDecrypt.decryptAES(encryptedbyteCode, AES_KEY);
            Bitmap decryptedImage = BitmapFactory.decodeByteArray(decryptedImageByte, 0, decryptedImageByte.length);



            holder.imageView.setImageBitmap(decryptedImage);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        /* galleryItem.getBase64();
       * 1. decode from base64
       * 2. decrypt
       * 3. convert to bitmap
       * 4. set to imageview
       * */

    }

    @Override
    public int getItemCount() {
        return galleryItems.size();
    }

    public void setItems(List<Image> galleryItems) {
        this.galleryItems.clear();
        this.galleryItems.addAll(galleryItems);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageItemView_recycler);
        }
    }
}

