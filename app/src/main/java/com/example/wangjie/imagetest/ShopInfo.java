package com.example.wangjie.imagetest;

public class ShopInfo {
    private int id;
    private String name;
    private float price;
    private String imagePath;

    @Override
    public String toString() {
        return "ShopInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getId() {

        return id;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public ShopInfo(int id, String name, float price, String imagePath) {

        this.id = id;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
    }
}
