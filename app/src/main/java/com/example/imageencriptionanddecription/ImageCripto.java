package com.example.imageencriptionanddecription;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

public class ImageCripto extends AppCompatActivity {

    public static final String TAG = "Shank";
    TextView encrypt, decrypt;
    String image;
    ClipboardManager clipboardManager;
    ImageView imageView;
    EditText encImg;
    Button copybutton;
    private static final int REQUEST_CODE_SELECT_IMAGE = 100;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 101;


    // AES encryption key (16 bytes)
    private static final String AES_KEY = "YourSecretKey123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_cripto);
        initialisation();
        clickevents();
    }



    private void initialisation() {
        encrypt = findViewById(R.id.encription_btn);
        decrypt = findViewById(R.id.decription_btn);
        encImg = findViewById(R.id.bytecode_textview);
        encImg.setEnabled(false);
        imageView = findViewById(R.id.cripto_imageview);
        copybutton=findViewById(R.id.copy_button);
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }




    private void clickevents() {
        encrypt.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(ImageCripto.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ImageCripto.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            } else {
                selectPhoto();
            }
        });

        decrypt.setOnClickListener(view -> {
            try {
                String encryptedBase64Text = readFromCacheDir();
                byte[] encryptedBytes = android.util.Base64.decode(encryptedBase64Text, android.util.Base64.DEFAULT);
                byte[] decryptedBytes = AESEncryptDecrypt.decryptAES(encryptedBytes, AES_KEY);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decryptedBytes, 0, decryptedBytes.length);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        copybutton.setOnClickListener(view -> clickMe_To_CopyCode(view));
    }
    private String readFromCacheDir() {
        try {
            // Create a file object for the cache file
            File file = new File(getCacheDir(), "encrypted_image.txt");

            // Read the contents of the file
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();

            // Return the Base64 string
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void selectPhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        } else {
            openImagePicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            Bitmap bitmap;
            ImageDecoder.Source source = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Log.d(TAG, "onActivityResult: ");
//                source = ImageDecoder.createSource(this.getContentResolver(), uri);
                try {
                     bitmap = decodeUriToBitmap(uri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bytes = stream.toByteArray();
                    byte[] encryptedBytes = AESEncryptDecrypt.encryptAES(bytes, AES_KEY);
                    String imageBase64 = Base64.getEncoder().encodeToString(encryptedBytes);

                    // Save the Base64 string to the cache file
                    saveToCacheDir(imageBase64);
                    Toast.makeText(this, "Image is encrypted successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "onActivityResult: " + e.getMessage(), e);
                }
            }
            else {
                Log.d(TAG, "onActivityResult: not calling");
            }
        }
    }
    private Bitmap decodeUriToBitmap(Uri uri) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
            return ImageDecoder.decodeBitmap(source);
        } else {
            return MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        }
    }
    public void clickMe_To_CopyCode(View view) {
        String codes = encImg.getText().toString().trim();
        if (!codes.isEmpty()) {
            ClipData temp = ClipData.newPlainText("text", codes);
            clipboardManager.setPrimaryClip(temp);
            Toast.makeText(this, "Copied to the clipboard", Toast.LENGTH_SHORT).show();
        }
    }
//    // AES for  encryption
//    private byte[] encryptAES(byte[] input, String key) throws Exception {
//        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
//        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//        return cipher.doFinal(input);
//    }
//
//    //for decryption use AES
//    private byte[] decryptAES(byte[] input, String key) throws Exception {
//        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
//        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//        cipher.init(Cipher.DECRYPT_MODE, secretKey);
//        return cipher.doFinal(input);
//    }

    private void saveToCacheDir(String base64String) {
        try {
            // Create a file in the cache directory
            File file = new File(getCacheDir(), "encrypted_image.txt");

            // Write the Base64 string to the file
            FileWriter writer = new FileWriter(file);
            writer.write(base64String);
            writer.flush();
            writer.close();

            // Show a toast message indicating the file path
            Toast.makeText(getApplicationContext(), "Image Base64 string saved to cache directory: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}




































//package com.example.imageencriptionanddecription;
//
//import static java.text.DateFormat.DEFAULT;
//import static java.util.Base64.*;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import android.Manifest;
//import android.content.ClipData;
//import android.content.ClipboardManager;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.ImageDecoder;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.io.ByteArrayOutputStream;
//import java.util.Base64;
//
//public class ImageCripto extends AppCompatActivity {
//
//    TextView encrypt,decrypt;
//    String image;
//    ClipboardManager clipboardManager;
//    ImageView imageView;
//    EditText encImg;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_image_cripto);
//        initialisation();
//        clickevents();
//
//    }
//    private void initialisation() {
//      encrypt=findViewById(R.id.encription_btn);
//      decrypt=findViewById(R.id.decription_btn);
//      encImg=findViewById(R.id.bytecode_textview);
//      encImg.setEnabled(false);
//      imageView=findViewById(R.id.cripto_imageview);
//      clipboardManager=(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//    }
//
//    private void clickevents() {
//   encrypt.setOnClickListener(view -> {
//       if (ContextCompat.checkSelfPermission(ImageCripto.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
//           ActivityCompat.requestPermissions(ImageCripto.this, new String[]{
//                   Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
//       }
//
//       else {
//           selectPhoto();
//       }
//   });
//   decrypt.setOnClickListener(view -> {
//
//       try {
//           byte[] bytes = android.util.Base64.decode(encImg.getText().toString(), android.util.Base64.DEFAULT);
//           Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//           imageView.setImageBitmap(bitmap);
//
//       } catch (IllegalArgumentException e) {
//           e.printStackTrace();
//       }
//   });
//    }
//
//    private void selectPhoto() {
//
//        Intent intent =new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("image/*");
//        startActivityForResult(Intent.createChooser(intent,"select picture"),100);
//    }
//
//    // this is for encription
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode==100 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
//            selectPhoto();
//        }
//        else {
//            Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
//        }
//    }
//    // this is for decription
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode==100 && resultCode ==RESULT_OK && data !=null){
//            Uri uri=data.getData();
//            Bitmap bitmap;
//            ImageDecoder.Source source=null;
//
//            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.P){
//                source =ImageDecoder.createSource(this.getContentResolver(),uri);
//                try{
//                bitmap=ImageDecoder.decodeBitmap(source);
//                ByteArrayOutputStream stream =new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
//                byte[] bytes=stream.toByteArray();
//                image= android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
//                encImg.setText(image);
//                    Toast.makeText(this, "Image is encrypted successfully", Toast.LENGTH_SHORT).show();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
////for copying the code to clipcode
//    public void clickMe_To_CopyCode(View view){
//        String codes=encImg.getText().toString().trim();
//        if (!codes.isEmpty()){
//            ClipData temp = ClipData.newPlainText("text",codes);
//            clipboardManager.setPrimaryClip(temp);
//            Toast.makeText(this, "copied to the clipboard", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//}