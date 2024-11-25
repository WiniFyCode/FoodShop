package com.thanh.foodshop;

public class SERVER {

    public static String ip = "http://192.168.0.102/foodshop/";

//    PRODUCT
    public static String bestfood_php = ip + "product/bestselling.php";
    public static String exclusive_offer_php = ip + "product/exclusive_offer.php";
    public static String product_categories_php = ip + "product/product_categories.php";
    public static String category_php = ip + "product/category.php";
    public static String get_all_product_php = ip + "product/get_all_product.php";

//    IMAGE
    public static String layanhslide_php = ip + "slide/layslide.php";
    public static String get_images_php = ip + "image/get_images.php";
    public static String food_url = ip + "image/";
    public static String anhslide_url = ip + "slide/";

//    AUTHENTICATION
    public static String login_php = ip + "authentication/login.php";
    public static String register_php = ip + "authentication/register.php";
    public static String update_user_info_php = ip + "authentication/update_user_info.php";

//    CART
    public static String get_cart_php = ip + "cart/get_cart.php";
    public static String add_to_cart_php = ip + "cart/add_to_cart.php";
    public static String update_cart_php = ip + "cart/update_cart.php";
    public static String delete_from_cart_php = ip + "cart/delete_from_cart.php";
    public static String clear_cart_php = ip + "cart/clear_cart.php";

//    FAVORITE
    public static String get_favorite_php = ip + "favorite/get_favorite.php";
    public static String add_to_favorite_php = ip + "favorite/add_to_favorite.php";
    public static String delete_from_favorite_php = ip + "favorite/delete_favorite.php";
    public static String check_favorite_php = ip + "favorite/check_favorite.php";

    //TEST
//    public static String add_to_cart_test_php = ip + "cart/add_to_cart_test.php";


}

