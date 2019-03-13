package com.hotelnow.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by susia on 15. 12. 29..
 */
public class JSONUtil {
    public static JSONArray sortDistance(JSONArray array) throws JSONException {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            jsons.add(array.getJSONObject(i));
        }
        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                String lid = null;
                String rid = null;
                try {
                    lid = lhs.getString("distance");
                    rid = rhs.getString("distance");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                lid = String.format("%7s", lid).replace("km", "");
                rid = String.format("%7s", rid).replace("km", "");

                return lid.compareTo(rid);
            }
        });
        return new JSONArray(jsons);
    }

    public static JSONArray sortPrice(JSONArray array, final String type) throws JSONException {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            jsons.add(array.getJSONObject(i));
        }
        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                String lid = null;
                String rid = null;
                try {
                    lid = lhs.getString("sale_price");
                    rid = rhs.getString("sale_price");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                lid = String.format("%7s", lid).replace(' ', '0');
                rid = String.format("%7s", rid).replace(' ', '0');

                if (type.equals("low"))
                    return lid.compareTo(rid);
                else
                    return rid.compareTo(lid);
            }
        });
        return new JSONArray(jsons);
    }

    public static JSONArray sortRate(JSONArray array) throws JSONException {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            jsons.add(array.getJSONObject(i));
        }
        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                String lid = null;
                String rid = null;
                try {
                    lid = lhs.getString("sale_rate");
                    rid = rhs.getString("sale_rate");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                lid = String.format("%3s", lid).replace(' ', '0');
                rid = String.format("%3s", rid).replace(' ', '0');

                return rid.compareTo(lid);
            }
        });
        return new JSONArray(jsons);
    }

    public static JSONArray sortGrade(JSONArray array) throws JSONException {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            jsons.add(array.getJSONObject(i));
        }
        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                String lid = null;
                String rid = null;
                try {
                    lid = lhs.getString("grade_score");
                    rid = rhs.getString("grade_score");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (lid.contains(".")) lid = lid.replace(".", "");
                if (rid.contains(".")) rid = rid.replace(".", "");

                if (lid.length() == 1) lid = Util.rightZero(lid);
                if (rid.length() == 1) rid = Util.rightZero(rid);

                lid = String.format("%3s", lid).replace(' ', '0');
                rid = String.format("%3s", rid).replace(' ', '0');

                return rid.compareTo(lid);
            }
        });
        return new JSONArray(jsons);
    }

    public static JSONArray sortReview(JSONArray array) throws JSONException {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            jsons.add(array.getJSONObject(i));
        }
        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                String lid = null;
                String rid = null;
                try {
                    lid = lhs.getString("review_score");
                    rid = rhs.getString("review_score");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                lid = String.format("%6s", lid).replace(' ', '0');
                rid = String.format("%6s", rid).replace(' ', '0');

                return rid.compareTo(lid);
            }
        });
        return new JSONArray(jsons);
    }
}