package com.meatfactory.order.model;

import java.util.ArrayList;
import java.util.List;

public class MeatMaster {

    // 肉マスタを取得する（共通）
    public static List<Meat> getMeatList() {

        List<Meat> meatList = new ArrayList<>();

        meatList.add(new Meat("BEEF", "牛肉", 1000));
        meatList.add(new Meat("PORK", "豚肉", 700));
        meatList.add(new Meat("CHICKEN", "鶏肉", 500));
        meatList.add(new Meat("LAMB", "ラム肉", 1200));
        meatList.add(new Meat("DUCK", "鴨肉", 1500));

        return meatList;
    }
}
