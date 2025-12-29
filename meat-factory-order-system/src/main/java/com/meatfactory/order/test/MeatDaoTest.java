package com.meatfactory.order.test;

import com.meatfactory.order.dao.MeatDao;
import com.meatfactory.order.model.Meat;

public class MeatDaoTest {

    public static void main(String[] args) {

        MeatDao dao = new MeatDao();


        for (Meat meat : dao.findAll()) {
            System.out.println(
                meat.getCode() + " / " +
                meat.getName() + " / " +
                meat.getPrice()
            );
        }
    }
}
