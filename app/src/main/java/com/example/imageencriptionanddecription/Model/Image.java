package com.example.imageencriptionanddecription.Model;



import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Images")
public class Image {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String base64;

    @DatabaseField
    private String fileName;

    // Default constructor (required by ORMLite)
    public Image() {
    }

    public Image(String base64, String fileName) {
        this.base64 = base64;
        this.fileName = fileName;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

