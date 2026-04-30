package com.livehealth.shared.constant;

public class ErrorMessage {
  public static final String ERR_EXCEPTION_GENERAL = "exception.general";
  public static final String UNAUTHORIZED = "exception.unauthorized";
  public static final String FORBIDDEN = "exception.forbidden";
  public static final String BAD_REQUEST = "exception.bad.request";
  public static final String FORBIDDEN_UPDATE_DELETE = "exception.forbidden.update-delete";
  public static final String ERR_UPLOAD_IMAGE_FAIL = "exception.upload.image.fail";

  // error validation dto
  public static final String INVALID_SOME_THING_FIELD = "invalid.general";
  public static final String INVALID_FORMAT_SOME_THING_FIELD = "invalid.general.format";
  public static final String INVALID_SOME_THING_FIELD_IS_REQUIRED = "invalid.general.required";
  public static final String NOT_BLANK_FIELD = "invalid.general.not-blank";
  public static final String INVALID_FORMAT_PASSWORD = "invalid.password-format";
  public static final String INVALID_DATE = "invalid.date-format";
  public static final String INVALID_DATE_FEATURE = "invalid.date-future";
  public static final String INVALID_DATETIME = "invalid.datetime-format";

  public static class Auth {
    public static final String ERR_INCORRECT_EMAIL = "exception.auth.incorrect.email";
    public static final String ERR_INCORRECT_PASSWORD = "exception.auth.incorrect.password";
    public static final String ERR_INVALID_CREDENTIALS = "exception.auth.email.or.password.wrong";
    public static final String ERR_ACCOUNT_NOT_ENABLED = "exception.auth.account.not.enabled";
    public static final String ERR_ACCOUNT_LOCKED = "exception.auth.account.locked";
    public static final String INVALID_REFRESH_TOKEN = "exception.auth.invalid.refresh.token";
    public static final String EXPIRED_REFRESH_TOKEN = "exception.auth.expired.refresh.token";
    public static final String ERR_LOGIN_FAIL = "exception.auth.login.fail";
    public static final String ERR_OTP_EXPIRED_OR_NOT_FOUND = "exception.auth.OTP.expired.or.not.found";
    public static final String ERR_OTP_INVALID = "exception.auth.OTP.invalid";
    public static final String ERR_OTP_EMAIL_SEND_FAILED = "exception.auth.OTP.email.send.failed";
    public static final String ERR_GET_TOKEN_CLAIM_SET_FAIL = "exception.auth.get.token.claim.set.fail";
    public static final String ERR_TOKEN_INVALIDATED = "exception.auth.token.invalidated";
    public static final String ERR_MALFORMED_TOKEN = "exception.auth.malformed.token";
    public static final String ERR_TOKEN_ALREADY_INVALIDATED = "exception.auth.token.already.invalidated";
    public static final String ERR_INVALID_GOOGLE_TOKEN = "exception.auth.invalid.google.token";

  }

  public static class User {
    public static final String ERR_USER_NOT_EXISTED = "exception.user.user.not.existed";
    public static final String ERR_USERNAME_EXISTED = "exception.user.username.existed";
    public static final String ERR_EMAIL_EXISTED = "exception.user.email.existed";
    public static final String ERR_EMAIL_NOT_EXISTED = "exception.user.email.not.existed";
    public static final String ERR_RE_ENTER_PASSWORD_NOT_MATCH = "exception.user.re-enter.password.not.match";
    public static final String ERR_DUPLICATE_OLD_PASSWORD = "exception.user.duplicate_old_password";
    public static final String ERR_PHONE_EXISTED = "exception.user.phone.existed";
    public static final String ERR_USER_IS_LOCKED = "exception.user.is.locked";
    public static final String ERR_USER_IS_NOT_LOCKED = "exception.user.is.not.locked";
    public static final String ERR_ACCOUNT_ALREADY_DELETED = "exception.user.account.already.deleted";
    public static final String ERR_ACCOUNT_RECOVERY_EXPIRED = "exception.user.account.recovery.period.has.expired";
    public static final String ERR_ACCOUNT_NOT_DELETED = "exception.user.account.is.not.in.deleted.state";
    public static final String ERR_INCORRECT_PASSWORD_CONFIRMATION = "exception.user.incorrect.password.confirmation";
    public static final String ERR_PERSONAL_INFORMATION_NOT_COMPLETED = "exception.user.personal.information.not.completed";
  }

  public static class UserHealth {
    public static final String ERR_USER_HEALTH_NOT_FOUND = "exception.user-health.not.found";
    public static final String ERR_GENDER_REQUIRED = "exception.user-health.gender.required";
    public static final String ERR_ACTIVITY_LEVEL_REQUIRED = "exception.user-health.activity-level.required";
    public static final String ERR_HEIGHT_MIN_VALUE = "exception.user-health.height.min-value";
    public static final String ERR_HEIGHT_MAX_VALUE = "exception.user-health.height.max-value";
    public static final String ERR_WEIGHT_MIN_VALUE = "exception.user-health.weight.min-value";
    public static final String ERR_WEIGHT_MAX_VALUE = "exception.user-health.weight.max-value";
    public static final String ERR_AGE_MIN_VALUE = "exception.user-health.age.min-value";
    public static final String ERR_AGE_MAX_VALUE = "exception.user-health.age.max-value";
  }

  public static class Post {
    public static final String ERR_POST_NOT_FOUND = "exception.post.not.found";
    public static final String ERR_POST_NOT_BELONG_TO_USER = "exception.post.not.belong.to.user";
  }

  public static class Comment {
    public static final String ERR_COMMENT_NOT_FOUND = "exception.comment.not.found";
    public static final String ERR_COMMENT_NOT_BELONG_TO_USER = "exception.comment.not.belong.to.user";
    public static final String ERR_PARENT_COMMENT_NOT_FOUND = "exception.comment.parent.not.found";
  }

  public static class Admin {
    public static final String ERR_NOT_ADMIN = "exception.admin.not.admin";
  }

  public static class Category {
    public static final String ERR_CATEGORY_NOT_FOUND = "exception.category.not.found";
    public static final String ERR_CATEGORY_NAME_EXISTED = "exception.category.name.existed";
    public static final String ERR_CATEGORY_HAS_PRODUCTS = "exception.category.has.products";
  }

  public static class Brand {
    public static final String ERR_BRAND_NOT_FOUND = "exception.brand.not.found";
    public static final String ERR_BRAND_NAME_EXISTED = "exception.brand.name.existed";
    public static final String ERR_BRAND_HAS_PRODUCTS = "exception.brand.has.products";
  }

  public static class Tag {
    public static final String ERR_TAG_NOT_FOUND = "exception.tag.not.found";
    public static final String ERR_TAG_NAME_EXISTED = "exception.tag.name.existed";
  }

  public static class ClientTestimonial {
    public static final String ERR_TESTIMONIAL_NOT_FOUND = "exception.client-testimonial.not.found";
  }

  public static class ProfessionalMember {
    public static final String ERR_MEMBER_NOT_FOUND = "exception.professional-member.not.found";
  }

  public static class WebInformation {
    public static final String ERR_WEB_INFO_NOT_FOUND = "exception.web-information.not.found";
  }

  public static class Product {
    public static final String ERR_PRODUCT_NOT_FOUND = "exception.product.not.found";
  }

  public static class Review {
    public static final String ERR_REVIEW_NOT_FOUND = "exception.review.not.found";
  }

  public static class ProductAttribute {
    public static final String ERR_ATTRIBUTE_NOT_FOUND = "exception.product-attribute.not.found";
    public static final String ERR_ATTRIBUTE_KEY_EXISTED = "exception.product-attribute.key.existed";
  }

  public static class ProductPromotion {
    public static final String ERR_PROMOTION_NOT_FOUND = "exception.product-promotion.not.found";
    public static final String ERR_PROMOTION_EXISTS_FOR_PRODUCT = "exception.product-promotion.exists";
  }

  public static class NewsCategory {
    public static final String ERR_CATEGORY_NOT_FOUND = "exception.news-category.not.found";
    public static final String ERR_NAME_EXISTED = "exception.news-category.name.existed";
  }

  public static class NewsTag {
    public static final String ERR_TAG_NOT_FOUND = "exception.news-tag.not.found";
    public static final String ERR_NAME_EXISTED = "exception.news-tag.name.existed";
  }

  public static class News {
    public static final String ERR_NEWS_NOT_FOUND = "exception.news.not.found";
  }

  public static class NewsComment {
    public static final String ERR_COMMENT_NOT_FOUND = "exception.news-comment.not.found";
  }

  public static class PaymentMethod {
    public static final String ERR_PAYMENT_METHOD_NOT_FOUND = "exception.payment-method.not.found";
    public static final String ERR_NAME_EXISTED = "exception.payment-method.name.existed";
  }

  public static class ShippingMethod {
    public static final String ERR_SHIPPING_METHOD_NOT_FOUND = "exception.shipping-method.not.found";
  }

  public static class Cart {
    public static final String ERR_CART_NOT_FOUND = "exception.cart.not.found";
    public static final String ERR_CART_ITEM_NOT_FOUND = "exception.cart-item.not.found";
    public static final String ERR_CART_EMPTY = "exception.cart.empty";
    public static final String ERR_SHIPPING_METHOD_NOT_SELECTED = "exception.cart.shipping-method.not.selected";
    public static final String ERR_PAYMENT_METHOD_NOT_SELECTED = "exception.cart.payment-method.not.selected";
    public static final String ERR_PRODUCT_OUT_OF_STOCK = "Sản phẩm đã hết hàng.";
    public static final String ERR_PRODUCT_QUANTITY_EXCEEDED = "Số lượng vượt quá số lượng hiện có.";
  }

  public static class Order {
    public static final String ERR_ORDER_NOT_FOUND = "exception.order.not.found";
    public static final String ERR_ORDER_CANNOT_BE_CANCELLED = "exception.order.cannot.be.cancelled";
  }

  public static class Payment {
    public static final String ERR_TRANSACTION_NOT_FOUND = "exception.payment.transaction.not.found";
    public static final String ERR_INVALID_CHECKSUM = "exception.payment.invalid.checksum";
    public static final String ERR_INVALID_AMOUNT = "exception.payment.invalid.amount";
  }

}
