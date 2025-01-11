-- 유저
CREATE TABLE user (
  user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_name VARCHAR(50) NOT NULL,
  point BIGINT NOT NULL default 0
);

-- 쿠폰
CREATE TABLE coupon (
  coupon_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  coupon_name VARCHAR(50) NOT NULL,
  discount_amount BIGINT NOT NULL default 0,
  coupon_state enum('ISSUABLE', 'NOT_ISSUABLE') NOT NULL default 'NOT_ISSUABLE'
);

-- 쿠폰 수량
CREATE TABLE coupon_quantity (
  coupon_quantity_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  coupon_id Bigint NOT NULL,
  quantity BIGINT NOT NULL default 0
);

-- 유저 쿠폰
CREATE TABLE user_coupon (
  user_coupon_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id Bigint NOT NULL,
  coupon_id Bigint NOT NULL,
  coupon_use boolean NOT NULL default false,
  use_at TIMESTAMP,
  create_at TIMESTAMP NOT NULL default CURRENT_TIMESTAMP
);

CREATE TABLE product (
  product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_name VARCHAR(50) NOT NULL,
  price BIGINT NOT NULL,
  product_state enum('IN_STOCK', 'OUT_OF_STOCK') NOT NULL default 'IN_STOCK'
);

CREATE TABLE product_quantity (
  product_quantity_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id Bigint NOT NULL,
  quantity BIGINT NOT NULL default 0
);

CREATE TABLE product_top (
  product_top_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  product_name VARCHAR(50) NOT NULL,
  price BIGINT NOT NULL,
  product_state ENUM('IN_STOCK', 'OUT_OF_STOCK') NOT NULL DEFAULT 'IN_STOCK',
  total_quantity BIGINT NOT NULL,
  product_rank BIGINT NOT NULL,
  create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
  order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  user_coupon_id BIGINT,
  order_amount BIGINT NOT NULL,
  discount_amount BIGINT NOT NULL DEFAULT 0,
  payment_amount BIGINT NOT NULL,
  order_state ENUM('PENDING', 'COMPLETED', 'FAILED') NOT NULL DEFAULT 'PENDING',
  order_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_product (
  order_product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity BIGINT NOT NULL
)

CREATE TABLE payment (
  payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  payment_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)