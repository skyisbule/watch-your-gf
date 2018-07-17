package com.github.skyisbule.watchgf.read;

import com.github.skyisbule.watchgf.Config;
import com.github.skyisbule.watchgf.enty.Downloads;
import com.github.skyisbule.watchgf.enty.Searchs;
import com.github.skyisbule.watchgf.enty.Urls;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class Selecter {

    @SuppressWarnings("unchecked")
    public List<Urls> getUrls(int limit) {
        String sql = "select * from urls order by last_visit_time desc limit";
        if (limit != 0) {
            sql += " " + String.valueOf(limit);
        }
        return this.doselect(sql, "urls");
    }

    @SuppressWarnings("unchecked")
    public List<Urls> downloads(int limit) {
        String sql = "select * from downloads order by start_time desc limit";
        if (limit != 0) {
            sql += " " + String.valueOf(limit);
        }
        return this.doselect(sql, "downloads");
    }

    @SuppressWarnings("unchecked")
    public List<Searchs> searchs(int limit) {
        String sql = "select * from keyword_search_terms order by url_id desc limit";
        if (limit != 0) {
            sql += " " + String.valueOf(limit);
        }
        return this.doselect(sql, "searchs");
    }

    private List doselect(String sql, String type) {
        Connection connection = null;
        List result = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + Config.HISTORY_PATH);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery(sql);
            result = doBinding(type, rs);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
        return result;
    }

    private List doBinding(String type, ResultSet rs) throws SQLException {
        if (type.equals("urls")) {
            List<Urls> list = new LinkedList<Urls>();
            while (rs.next()) {
                Urls url = new Urls();
                url.setUid(rs.getInt("id"));
                url.setTitle(rs.getString("title"));
                url.setUrl(rs.getString("url"));
                url.setVisit_count(rs.getInt("visit_count"));
                list.add(url);
            }
            return list;
        }

        if (type.equals("downloads")) {
            List<Searchs> list = new LinkedList<Searchs>();
            while (rs.next()) {
                Searchs search = new Searchs();
                search.setUrlId(rs.getInt("url_id"));
                search.setTerm(rs.getString("term"));
                list.add(search);
            }
            return list;
        }

        if (type.equals("searchs")) {
            List<Downloads> list = new LinkedList<Downloads>();
            while (rs.next()) {
                Downloads download = new Downloads();
                download.setId(rs.getInt("id"));
                download.setCurrent_path(rs.getString("current_path"));
                download.setReferrer(rs.getString("referrer"));
                download.setTotal_bytes(rs.getInt("total_bytes"));
                list.add(download);
            }
            return list;
        }
        return new LinkedList();
    }

}
