package com.meatfactory.order.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 肉マスタを提供するクラス。
 *
 * 今は学習用として「DBではなく、Javaのリストで固定データを返す」形になっています。
 * （本番や実務では、MeatDao でDBから取得するのが一般的です）
 *
 * このクラスを使うメリット：
 * - どの画面/サーブレットから呼んでも同じ肉一覧を取得できる（共通化）
 */
public class MeatMaster {

    /**
     * 肉マスタを取得する（共通）
     * @return 肉マスタ一覧
     */
    public static List<Meat> getMeatList() {

        // ① 肉マスタの入れ物（List）を作る
        List<Meat> meatList = new ArrayList<>();

        // ② 学習用の固定データを追加する
        //    ※ code はシステム内部の識別子、name/price は表示・計算で使う
        meatList.add(new Meat("BEEF", "牛肉", 1000));
        meatList.add(new Meat("PORK", "豚肉", 700));
        meatList.add(new Meat("CHICKEN", "鶏肉", 500));
        meatList.add(new Meat("LAMB", "ラム肉", 1200));
        meatList.add(new Meat("DUCK", "鴨肉", 1500));

        // ③ 作った一覧を返す
        return meatList;
    }
}
