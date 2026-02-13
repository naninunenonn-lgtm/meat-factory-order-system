package com.meatfactory.order.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.meatfactory.order.model.Customer;

/**
 * 取引先マスタ（customer）を扱うDAO。
 * 今回は「入力画面のプルダウン表示のために一覧を取る」だけ実装する。
 */
public class CustomerDao {

    /**
     * 有効な取引先一覧を取得する
     * - is_active=true のものだけ
     * - code順で並べる
     */
    public List<Customer> findActive() throws Exception {

        String sql = """
            SELECT customer_id, code, name
            FROM customer
            WHERE is_active = true
            ORDER BY code
        """;

        List<Customer> list = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Customer c = new Customer(
                        rs.getLong("customer_id"),
                        rs.getString("code"),
                        rs.getString("name")
                );
                list.add(c);
            }
        }

        return list;
    }
    
	    /**
	     * 取引先を新規登録する
	     */
	    public void insert(String code, String name) throws Exception {
	
	        String sql = """
	            INSERT INTO customer(code, name, is_active)
	            VALUES (?, ?, true)
	        """;
	
	        try (Connection con = DBUtil.getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {
	
	            ps.setString(1, code);
	            ps.setString(2, name);
	
	            ps.executeUpdate();
	        }
	    }
	    
	    /**
	     * 主キーで1件取得
	     * 編集画面用
	     */
	    public Customer findById(int id) throws Exception {

	        String sql = "SELECT * FROM customer WHERE customer_id = ?";

	        try (Connection con = DBUtil.getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {

	            // ? に値をセット
	            ps.setInt(1, id);

	            try (ResultSet rs = ps.executeQuery()) {

	                // 1件のみ
	                if (rs.next()) {

	                    return new Customer(
	                        rs.getLong("customer_id"),
	                        rs.getString("code"),
	                        rs.getString("name"),
	                        rs.getBoolean("is_active")
	                    );
	                }

	                return null;
	            }
	        }
	    }

	    /**
	     * 既存データ更新
	     */
	    public void update(int id, String code, String name) throws Exception {

	        String sql = """
	            UPDATE customer
	            SET code = ?, name = ?, updated_at = now()
	            WHERE customer_id = ?
	        """;

	        try (Connection con = DBUtil.getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {

	            // 順番に ? を埋める
	            ps.setString(1, code);
	            ps.setString(2, name);
	            ps.setInt(3, id);

	            // 更新実行
	            ps.executeUpdate();
	        }
	    }

    

}
