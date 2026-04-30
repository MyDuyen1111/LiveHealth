package com.livehealth.shared.constant;

public class UrlConstant {
  public static class Auth {
    private static final String PRE_FIX = "/auth";

    public static final String LOGIN = PRE_FIX + "/login";
    public static final String LOGIN_WITH_GOOGLE = PRE_FIX + "/google";
    public static final String REGISTER = PRE_FIX + "/register";
    public static final String VERIFY_OTP = PRE_FIX + "/verify-otp";
    public static final String FORGOT_PASSWORD = PRE_FIX + "/forgot-password";
    public static final String VERIFY_OTP_TO_RESET_PASSWORD = PRE_FIX + "/verify-otp-to-reset-password";
    public static final String RESET_PASSWORD = PRE_FIX + "/reset-password";
    public static final String ACCOUNT_RECOVERY = PRE_FIX + "/account-recovery";
    public static final String VERIFY_OTP_TO_RECOVERY = PRE_FIX + "/verify-otp-to-recovery";
    public static final String RECOVER_ACCOUNT = PRE_FIX + "/recover-account";
    public static final String REFRESH_TOKEN = PRE_FIX + "/refresh";
    public static final String LOGOUT = PRE_FIX + "/logout";

    private Auth() {
    }
  }

  public static class User {
    private static final String PRE_FIX = "/user";

    public static final String GET_USERS = PRE_FIX;
    public static final String GET_USER = PRE_FIX + "/{userId}";
    public static final String GET_CURRENT_USER = PRE_FIX + "/current";

    public static final String FILL_PERSONAL_INFORMATION = PRE_FIX + "/personal-information";
    public static final String UPLOAD_AVATAR = PRE_FIX + "/upload-avatar";
    public static final String DELETE_MY_ACCOUNT = PRE_FIX + "/delete-my-account";

    public static final String GET_PROFILE = PRE_FIX + "/profile";
    public static final String UPDATE_PROFILE = PRE_FIX + "/update-profile";
    public static final String UPDATE_BILLING_ADDRESS = PRE_FIX + "/billing-address";
    public static final String CHANGE_PASSWORD = PRE_FIX + "/change-password";

    private User() {
    }
  }

  public static class Admin {
    private static final String PRE_FIX = "/admin";

    public static final String GET_USERS = PRE_FIX + "/users";
    public static final String GET_USER = PRE_FIX + "/users/{userId}";
    public static final String CREATE_USER = PRE_FIX + "/create-user";
    public static final String UPDATE_USER = PRE_FIX + "/update-user/{userId}";
    public static final String DELETE_USER = PRE_FIX + "/delete-user/{userId}";
    public static final String SEARCH_USER_BY_USERNAME = PRE_FIX + "/search-user-by-username";
    public static final String SEARCH_USER_BY_EMAIL = PRE_FIX + "/search-user-by-email";
    public static final String SEARCH_USER_BY_PHONE = PRE_FIX + "/search-user-by-phone";

    public static final String SEARCH_TRAINING_EXERCISE = PRE_FIX + "/search-training-exercise";

    public static final String LOCK_USER = PRE_FIX + "/lock-user/{userId}";
    public static final String UNLOCK_USER = PRE_FIX + "/unlock-user/{userId}";

    public static final String GET_ALL_USER = PRE_FIX + "/users-count";

    public static final String GET_ALL_EXERCISE = PRE_FIX + "/exercises";

    public static final String GET_USER_DAY = PRE_FIX + "/user-day";
    public static final String GET_USER_MONTH = PRE_FIX + "/user-month";

    private Admin() {
    }
  }

  public static class Feed {
    private static final String PRE_FIX_POST = "/posts";

    public static final String CREATE_POST = PRE_FIX_POST;
    public static final String GET_POST_BY_ID = PRE_FIX_POST + "/{id}";
    public static final String GET_ALL_POST = PRE_FIX_POST;
    public static final String GET_USER_POST = PRE_FIX_POST + "/user/{userId}";
    public static final String UPDATE_POST = PRE_FIX_POST + "/{id}";
    public static final String DELETE_POST = PRE_FIX_POST + "/{id}";

    public static final String POST_LIKE = PRE_FIX_POST + "/{id}/like";
    public static final String POST_UNLIKE = PRE_FIX_POST + "/{id}/unlike";
    public static final String SEARCH_POST = PRE_FIX_POST + "/search";

    private static final String PRE_FIX_COMMENT = "/comments";
    public static final String CREATE_COMMENT = PRE_FIX_POST + "/{postId}" + PRE_FIX_COMMENT;
    public static final String GET_COMMENT_BY_POST_ID = PRE_FIX_POST + "/{postId}" + PRE_FIX_COMMENT;
    public static final String GET_COMMENT_BY_ID = PRE_FIX_COMMENT + "/{id}";
    public static final String GET_COMMENT_REPLIES = PRE_FIX_COMMENT + "/{id}/replies";
    public static final String UPDATE_COMMENT = PRE_FIX_COMMENT + "/{id}";
    public static final String DELETE_COMMENT = PRE_FIX_COMMENT + "/{id}";

    public static final String COMMENT_LIKE = PRE_FIX_COMMENT + "/{id}/like";
    public static final String COMMENT_UNLIKE = PRE_FIX_COMMENT + "/{id}/unlike";
  }

  public static class Category {
    private static final String PRE_FIX = "/categories";

    public static final String GET_ALL_CATEGORIES = PRE_FIX;
    public static final String GET_CATEGORY_BY_ID = PRE_FIX + "/{id}";
    public static final String GET_PRODUCTS_BY_CATEGORY = PRE_FIX + "/{id}/products";

    public static final String CREATE_CATEGORY = PRE_FIX;
    public static final String UPDATE_CATEGORY = PRE_FIX + "/{id}";
    public static final String DELETE_CATEGORY = PRE_FIX + "/{id}";

    private Category() {
    }
  }

  public static class Brand {
    private static final String PRE_FIX = "/brands";

    public static final String GET_ALL_BRANDS = PRE_FIX;
    public static final String GET_BRAND_BY_ID = PRE_FIX + "/{id}";
    public static final String GET_PRODUCTS_BY_BRAND = PRE_FIX + "/{id}/products";

    public static final String CREATE_BRAND = PRE_FIX;
    public static final String UPDATE_BRAND = PRE_FIX + "/{id}";
    public static final String DELETE_BRAND = PRE_FIX + "/{id}";

    private Brand() {
    }
  }

  public static class Tag {
    private static final String PRE_FIX = "/tags";

    public static final String GET_ALL_TAGS = PRE_FIX;
    public static final String GET_TAG_BY_ID = PRE_FIX + "/{id}";
    public static final String GET_PRODUCTS_BY_TAG = PRE_FIX + "/{id}/products";

    public static final String CREATE_TAG = PRE_FIX;
    public static final String UPDATE_TAG = PRE_FIX + "/{id}";
    public static final String DELETE_TAG = PRE_FIX + "/{id}";

    private Tag() {
    }
  }

  public static class ClientTestimonial {
    private static final String PRE_FIX = "/client-testimonials";

    public static final String GET_ALL_TESTIMONIALS = PRE_FIX;
    public static final String GET_TESTIMONIAL_BY_ID = PRE_FIX + "/{id}";

    public static final String CREATE_TESTIMONIAL = PRE_FIX;
    public static final String UPDATE_TESTIMONIAL = PRE_FIX + "/{id}";
    public static final String UPDATE_TESTIMONIAL_AVATAR = PRE_FIX + "/{id}/avatar";
    public static final String DELETE_TESTIMONIAL = PRE_FIX + "/{id}";

    private ClientTestimonial() {
    }
  }

  public static class ProfessionalMember {
    private static final String PRE_FIX = "/professional-members";

    public static final String GET_ALL_MEMBERS = PRE_FIX;
    public static final String GET_MEMBER_BY_ID = PRE_FIX + "/{id}";

    public static final String CREATE_MEMBER = PRE_FIX;
    public static final String UPDATE_MEMBER = PRE_FIX + "/{id}";
    public static final String UPDATE_MEMBER_AVATAR = PRE_FIX + "/{id}/avatar";
    public static final String DELETE_MEMBER = PRE_FIX + "/{id}";

    private ProfessionalMember() {
    }
  }

  public static class WebInformation {
    private static final String PRE_FIX = "/web-information";

    public static final String GET_WEB_INFORMATION = PRE_FIX;

    public static final String UPDATE_WEB_INFORMATION = PRE_FIX;
    public static final String UPDATE_WEB_LOGO = PRE_FIX + "/logo";

    private WebInformation() {
    }
  }

  public static class Review {
    private static final String PRE_FIX = "/reviews";

    public static final String GET_ALL_REVIEWS_BY_PRODUCT = "/products/{productId}/reviews";
    public static final String GET_REVIEW_BY_ID = PRE_FIX + "/{id}";

    public static final String CREATE_REVIEW = "/products/{productId}/reviews";
    public static final String UPDATE_REVIEW = PRE_FIX + "/{id}";
    public static final String DELETE_REVIEW = PRE_FIX + "/{id}";

    private Review() {
    }
  }

  public static class ProductAttribute {
    private static final String PRE_FIX = "/product-attributes";

    public static final String GET_ALL_ATTRIBUTES_BY_PRODUCT = "/products/{productId}/attributes";
    public static final String GET_ATTRIBUTE_BY_ID = PRE_FIX + "/{id}";

    public static final String CREATE_ATTRIBUTE = "/products/{productId}/attributes";
    public static final String UPDATE_ATTRIBUTE = PRE_FIX + "/{id}";
    public static final String DELETE_ATTRIBUTE = PRE_FIX + "/{id}";

    private ProductAttribute() {
    }
  }

  public static class ProductPromotion {
    private static final String PRE_FIX = "/product-promotions";

    public static final String GET_ALL_PROMOTIONS_BY_PRODUCT = "/products/{productId}/promotions";
    public static final String GET_PROMOTION_BY_ID = PRE_FIX + "/{id}";

    public static final String CREATE_PROMOTION = "/products/{productId}/promotions";
    public static final String UPDATE_PROMOTION = PRE_FIX + "/{id}";
    public static final String DELETE_PROMOTION = PRE_FIX + "/{id}";

    private ProductPromotion() {
    }
  }

  public static class Product {
    private static final String PRE_FIX = "/products";

    public static final String GET_ALL_PRODUCTS = PRE_FIX;
    public static final String GET_PRODUCT_BY_ID = PRE_FIX + "/{id}";

    public static final String CREATE_PRODUCT = PRE_FIX;
    public static final String UPDATE_PRODUCT = PRE_FIX + "/{id}";
    public static final String UPLOAD_IMAGES = PRE_FIX + "/{id}/images";
    public static final String DELETE_PRODUCT = PRE_FIX + "/{id}";

    public static final String ADD_TAGS_TO_PRODUCT = PRE_FIX + "/{id}/tags";
    public static final String REMOVE_TAG_FROM_PRODUCT = PRE_FIX + "/{id}/tags/{tagId}";

    private Product() {
    }
  }

  public static class NewsCategory {
    private static final String PRE_FIX = "/news-categories";

    public static final String GET_ALL_CATEGORIES = PRE_FIX;
    public static final String GET_CATEGORY_BY_ID = PRE_FIX + "/{id}";
    public static final String GET_CATEGORY_COUNTS = "/news/category-counts"; // Đếm số lượng news

    public static final String CREATE_CATEGORY = PRE_FIX;
    public static final String UPDATE_CATEGORY = PRE_FIX + "/{id}";
    public static final String DELETE_CATEGORY = PRE_FIX + "/{id}";

    private NewsCategory() {
    }
  }

  public static class NewsTag {
    private static final String PRE_FIX = "/news-tags";

    public static final String GET_ALL_TAGS = PRE_FIX;
    public static final String GET_TAG_BY_ID = PRE_FIX + "/{id}";

    public static final String CREATE_TAG = PRE_FIX;
    public static final String UPDATE_TAG = PRE_FIX + "/{id}";
    public static final String DELETE_TAG = PRE_FIX + "/{id}";

    private NewsTag() {
    }
  }

  public static class News {
    private static final String PRE_FIX = "/news";

    public static final String GET_ALL_NEWS = PRE_FIX; // cho phép search bàng categoryId, tagId, etc.
    public static final String GET_NEWS_BY_ID = PRE_FIX + "/{id}";

    public static final String CREATE_NEWS = PRE_FIX;
    public static final String UPDATE_NEWS = PRE_FIX + "/{id}";
    public static final String UPLOAD_THUMBNAIL = PRE_FIX + "/{id}/thumbnail";
    public static final String UPLOAD_IMAGES = PRE_FIX + "/{id}/images";
    public static final String DELETE_NEWS = PRE_FIX + "/{id}";

    public static final String ADD_TAGS_TO_NEWS = PRE_FIX + "/{id}/tags";
    public static final String REMOVE_TAG_FROM_NEWS = PRE_FIX + "/{id}/tags/{tagId}";

    private News() {
    }
  }

  public static class NewsComment {
    private static final String PRE_FIX = "/news/{newsId}/comments";

    public static final String GET_ALL_COMMENTS = PRE_FIX;
    public static final String GET_COMMENT_BY_ID = "/news-comments/{id}";

    public static final String CREATE_COMMENT = PRE_FIX;
    public static final String UPDATE_COMMENT = "/news-comments/{id}";
    public static final String DELETE_COMMENT = "/news-comments/{id}";

    private NewsComment() {
    }
  }

  public static class PaymentMethod {
    private static final String PRE_FIX = "/payment-methods";

    public static final String GET_ALL_PAYMENT_METHODS = PRE_FIX;
    public static final String GET_PAYMENT_METHOD_BY_ID = PRE_FIX + "/{id}";

    public static final String CREATE_PAYMENT_METHOD = PRE_FIX;
    public static final String UPDATE_PAYMENT_METHOD = PRE_FIX + "/{id}";
    public static final String DELETE_PAYMENT_METHOD = PRE_FIX + "/{id}";

    private PaymentMethod() {
    }
  }

  public static class ShippingMethod {
    private static final String PRE_FIX = "/shipping-methods";

    public static final String GET_ALL_SHIPPING_METHODS = PRE_FIX;
    public static final String GET_SHIPPING_METHOD_BY_ID = PRE_FIX + "/{id}";

    public static final String CREATE_SHIPPING_METHOD = PRE_FIX;
    public static final String UPDATE_SHIPPING_METHOD = PRE_FIX + "/{id}";
    public static final String DELETE_SHIPPING_METHOD = PRE_FIX + "/{id}";

    private ShippingMethod() {
    }
  }

  public static class Cart {
    private static final String PRE_FIX = "/cart";

    public static final String GET_MY_CART = PRE_FIX;
    public static final String ADD_ITEM = PRE_FIX + "/items";
    public static final String UPDATE_ITEM = PRE_FIX + "/items/{itemId}";
    public static final String REMOVE_ITEM = PRE_FIX + "/items/{itemId}";
    public static final String CLEAR_CART = PRE_FIX + "/clear";
    public static final String APPLY_PROMOTION = PRE_FIX + "/promotion/{promotionId}";
    public static final String SELECT_SHIPPING_METHOD = PRE_FIX + "/shipping-method/{shippingMethodId}";
    public static final String SELECT_PAYMENT_METHOD = PRE_FIX + "/payment-method/{paymentMethodId}";

    private Cart() {
    }
  }

  public static class Order {
    private static final String PRE_FIX = "/orders";

    public static final String GET_MY_ORDERS = PRE_FIX + "/me";
    public static final String GET_ALL_ORDERS = PRE_FIX;
    public static final String GET_ORDER_BY_ID = PRE_FIX + "/{id}";

    public static final String PLACE_ORDER = PRE_FIX;
    public static final String UPDATE_ORDER_STATUS = PRE_FIX + "/{id}/status";
    public static final String CANCEL_ORDER = PRE_FIX + "/{id}/cancel";

    private Order() {
    }
  }

  public static class Payment {
    private static final String PRE_FIX = "/payment";

    public static final String CREATE_PAYMENT = PRE_FIX + "/create-payment";
    public static final String PAYMENT_RETURN = PRE_FIX + "/vnpay/return";
    public static final String PAYMENT_IPN = PRE_FIX + "/vnpay/ipn";
    public static final String PAYMENT_STATUS = PRE_FIX + "/status/{txnRef}";

    private Payment() {
    }
  }

}
