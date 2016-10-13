package com.kalis.keys;
/**
 * Created by Kalis on 12/29/2015.
 */
public class KeySource {
    public static final String BASE_MAP_URL = "http://maps.googleapis.com";


    public static final String BASE_JSON_URL = "http://nkshop.coolpage.biz";
    public static final String LINK_PRODUCT = "http://nkshop.coolpage.biz/connect.php?load_product_with_cateid=";
    public static final String SEARCH_WITH_KEY = "http://nkshop.coolpage.biz/connect.php?key=";
    public static final String REPONSE ="http://nkshop.coolpage.biz/connect.php";
    // dâta
    public static final String BUNDLE_PUT_DATA = "DATA";
    public static final String BUNDLE_PUT_PRODUCTS = "DATA_PRODUCT";
    public static final String BUNDLE_PUT_IMAGEVIEW = "DATA_IMAGE";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    public static final String LINK_CATEGORY = "http://nkshop.coolpage.biz/connect.php?load_category=";
    public static String LINK_CATEGORIES = "http://nkshop.coolpage.biz/connect.php?load=category";


    public static final String DESCRIPTION_ITEM_ERROR = "Connection Lost";
    public static String NO_WAY_RESUTLS = "Không tìm thấy dường đi";
    public static double SHOP_LATITUDE = 10.783641;
    public static double SHOP_LONGITUDE = 106.671175;

    //DATABASE
    public static final String DATABASE_NAME = "CatsaShop.sqlite";
    public static final String DB_PATH_SUFFIX = "/databases/";
    public static final String TABLES[] = {"Favorite","ShoppingCart","province"};


    //Product table fildes
    public static final String PRODUCT_FIELD[] = {"cateid","id","code","price","discount","description","src"};


    //Popup Menu
    public static final String f1 = "Trang Chủ";
    public static final String f2 = "Danh Sách Yêu Thích";


    public static int ADD_TO_FAVORITE_TABLE = 1111;
    public static int ADD_TO_SHOPPING_CART_TABLE = 2222;


    public static String DATA_SIZE = "DATA_SIZE";
}
