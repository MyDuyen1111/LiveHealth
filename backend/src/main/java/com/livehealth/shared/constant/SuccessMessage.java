package com.livehealth.shared.constant;

public class SuccessMessage {
    public static class Auth {
        public static final String REGISTER_SUCCESS = "Register successfully";
        public static final String SUCCESS_SEND_OTP = "OTP has sent to your email";
        public static final String SUCCESS_VERIFY_OTP = "OTP has verified successfully";
        public static final String LOGIN_SUCCESS = "Login successfully";
        public static final String LOGOUT_SUCCESS = "Logout successfully";
    }

    public static class User {
        public static final String DELETE_SUCCESS = "Delete user successfully";
        public static final String LOCKED_SUCCESS = "Locked user successfully";
        public static final String UNLOCKED_SUCCESS = "Unlocked user successfully";
        public static final String SOFT_DELETE_SUCCESS = "User account has been deleted successfully. You have 30 days to recover it.";
        public static final String RECOVERY_SUCCESS = "User account has been recovered successfully";
    }

    public static class Comment {
        public static final String DELETE_COMMENT_SUCCESS = "Comment deleted successfully";
        public static final String UNLIKE_COMMENT_SUCCESS = "Comment unliked successfully";
    }

    public static class Post {
        public static final String DELETE_POST_SUCCESS = "Post deleted successfully";
        public static final String UNLIKE_POST_SUCCESS = "Post unliked successfully";
    }

    public static class Category {
        public static final String DELETE_CATEGORY_SUCCESS = "Category deleted successfully";
    }

    public static class Brand {
        public static final String DELETE_BRAND_SUCCESS = "Brand deleted successfully";
    }

    public static class Tag {
        public static final String DELETE_TAG_SUCCESS = "Tag deleted successfully";
    }

    public static class ClientTestimonial {
        public static final String DELETE_TESTIMONIAL_SUCCESS = "Client testimonial deleted successfully";
    }

    public static class ProfessionalMember {
        public static final String DELETE_MEMBER_SUCCESS = "Professional member deleted successfully";
    }

    public static class Review {
        public static final String DELETE_REVIEW_SUCCESS = "Review deleted successfully";
    }

    public static class ProductAttribute {
        public static final String DELETE_ATTRIBUTE_SUCCESS = "Product attribute deleted successfully";
    }

    public static class ProductPromotion {
        public static final String DELETE_PROMOTION_SUCCESS = "Product promotion deleted successfully";
    }

    public static class Product {
        public static final String DELETE_PRODUCT_SUCCESS = "Product deleted successfully";
        public static final String ADD_TAG_SUCCESS = "Tag added to product successfully";
        public static final String REMOVE_TAG_SUCCESS = "Tag removed from product successfully";
    }

    public static class NewsCategory {
        public static final String DELETE_CATEGORY_SUCCESS = "News category deleted successfully";
    }

    public static class NewsTag {
        public static final String DELETE_TAG_SUCCESS = "News tag deleted successfully";
    }

    public static class News {
        public static final String DELETE_NEWS_SUCCESS = "News deleted successfully";
    }

    public static class NewsComment {
        public static final String DELETE_COMMENT_SUCCESS = "News comment deleted successfully";
    }

    public static class PaymentMethod {
        public static final String DELETE_PAYMENT_METHOD_SUCCESS = "Payment method deleted successfully";
    }

    public static class ShippingMethod {
        public static final String DELETE_SHIPPING_METHOD_SUCCESS = "Shipping method deleted successfully";
    }

    public static class Cart {
        public static final String CLEAR_CART_SUCCESS = "Cart cleared successfully";
        public static final String REMOVE_ITEM_SUCCESS = "Item removed from cart successfully";
    }

    public static class Order {
        public static final String PLACE_ORDER_SUCCESS = "Order placed successfully";
        public static final String CANCEL_ORDER_SUCCESS = "Order cancelled successfully";
    }
}
