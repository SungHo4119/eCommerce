USE hhplus;

create table coupon (coupon_id bigint not null auto_increment, coupon_name varchar(255), coupon_state tinyint check (coupon_state between 0 and 1), discount_amount bigint, primary key (coupon_id)) engine=InnoDB;

create table coupon_quantity (coupon_quantity_id bigint not null auto_increment, coupon_id bigint, quantity bigint, primary key (coupon_quantity_id)) engine=InnoDB;

create table order_product (order_product_id bigint not null auto_increment, order_id bigint, product_id bigint, quantity bigint, primary key (order_product_id)) engine=InnoDB;

create table orders (order_id bigint not null auto_increment, discount_amount bigint, order_amount bigint, order_at datetime(6), order_state tinyint check (order_state between 0 and 2), payment_amount bigint, user_coupon_id bigint, user_id bigint, primary key (order_id)) engine=InnoDB;

create table outbox_event (outbox_event_id bigint not null auto_increment, aggregate_id varchar(255), created_at datetime(6), event_payload varchar(255), event_type varchar(255), process_state tinyint check (process_state between 0 and 4), primary key (outbox_event_id)) engine=InnoDB;

create table payment (payment_id bigint not null auto_increment, order_id bigint, payment_at datetime(6), primary key (payment_id)) engine=InnoDB;

create table product (product_id bigint not null auto_increment, price bigint, product_name varchar(255), product_state tinyint check (product_state between 0 and 1), primary key (product_id)) engine=InnoDB;

create table product_quantity (product_quantity_id bigint not null auto_increment, product_id bigint, quantity bigint, primary key (product_quantity_id)) engine=InnoDB;

create table product_top (product_top_id bigint not null, create_at date not null, price bigint not null, product_id bigint not null, product_name varchar(50) not null, product_rank bigint not null, product_state enum ('IN_STOCK','OUT_OF_STOCK') not null, total_quantity bigint not null, primary key (product_top_id)) engine=InnoDB;

create table product_top_seq (next_val bigint) engine=InnoDB;

insert into product_top_seq values (1);

create table user_coupon (user_coupon_id bigint not null auto_increment, coupon_use bit, create_at datetime(6), use_at datetime(6), user_id bigint not null, coupon_id bigint, primary key (user_coupon_id)) engine=InnoDB;

create table users (user_id bigint not null auto_increment, point bigint, user_name varchar(255), primary key (user_id)) engine=InnoDB;

alter table user_coupon add constraint UKb7ji6tcp2d4mh0ylxvdyrv5f3 unique (coupon_id, user_id);

alter table user_coupon add constraint FK23vpkw483hhbe77dgvimcipf4 foreign key (coupon_id) references coupon (coupon_id);


-- 유저 데이터 삽입 프로시저 생성
DELIMITER //
CREATE PROCEDURE InsertUser()
BEGIN
  DECLARE i INT DEFAULT 1;
  START TRANSACTION;
  WHILE i <= 50000 DO
    INSERT INTO users (point, user_name)
    VALUES ( 1000, CONCAT('user', i));
    SET i = i + 1;
    -- 10,000건마다 커밋하여 성능 최적화
    IF (i MOD 10000 = 0) THEN
      COMMIT;
      START TRANSACTION;
    END IF;
  END WHILE;
  COMMIT;
END //
DELIMITER ;

-- 쿠폰 데이터 삽입 프로시저 생성
DELIMITER //
CREATE PROCEDURE InsertCoupon()
BEGIN
  START TRANSACTION;
  INSERT INTO coupon (coupon_name, coupon_state, discount_amount)
  VALUES
    ('coupon1', 0,  1000),
    ('coupon2', 0,  1000),
    ('coupon3', 0,  1000),
    ('coupon4', 0,  1000);
  COMMIT;
END //
DELIMITER ;

-- 프로시저 실행
CALL InsertUser();
CALL InsertCoupon();
