package com.hhplush.eCommerce.integration.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplush.eCommerce.api.user.UserController;
import com.hhplush.eCommerce.business.coupon.CouponUseCase;
import com.hhplush.eCommerce.business.order.OrderUseCase;
import com.hhplush.eCommerce.business.payment.PaymentUseCase;
import com.hhplush.eCommerce.business.product.ProductUseCase;
import com.hhplush.eCommerce.business.user.UserUseCase;
import com.hhplush.eCommerce.common.filter.CustomLoggingFilter;
import com.hhplush.eCommerce.domain.coupon.CouponService;
import com.hhplush.eCommerce.domain.order.OrderSerivce;
import com.hhplush.eCommerce.domain.payment.PaymentService;
import com.hhplush.eCommerce.domain.product.ProductService;
import com.hhplush.eCommerce.domain.user.UserService;
import com.hhplush.eCommerce.infrastructure.coupon.CouponRepository;
import com.hhplush.eCommerce.infrastructure.coupon.ICouponJPARepository;
import com.hhplush.eCommerce.infrastructure.coupon.ICouponQuantityJPARepository;
import com.hhplush.eCommerce.infrastructure.coupon.IUserCouponJPARepository;
import com.hhplush.eCommerce.infrastructure.order.IOrderJPARepository;
import com.hhplush.eCommerce.infrastructure.order.IOrderProductJPARepository;
import com.hhplush.eCommerce.infrastructure.order.OrderRepository;
import com.hhplush.eCommerce.infrastructure.payment.IPaymentJPARepository;
import com.hhplush.eCommerce.infrastructure.payment.PaymentRepository;
import com.hhplush.eCommerce.infrastructure.product.IProductJPARepository;
import com.hhplush.eCommerce.infrastructure.product.IProductQuantityJPARepository;
import com.hhplush.eCommerce.infrastructure.product.IProductTopJPARepository;
import com.hhplush.eCommerce.infrastructure.product.ProductRepository;
import com.hhplush.eCommerce.infrastructure.redis.RedisRepository;
import com.hhplush.eCommerce.infrastructure.redis.RedisService;
import com.hhplush.eCommerce.infrastructure.user.IUserJPARepository;
import com.hhplush.eCommerce.infrastructure.user.UserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
@ActiveProfiles("test")
public class IntegrationTest {

    // 유저
    @Autowired
    protected UserController userController;
    @Autowired
    protected IUserJPARepository userJPARepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected UserUseCase userUseCase;
    @Autowired
    protected UserService userService;
    // 제품
    @Autowired
    protected ProductRepository productRepository;
    @Autowired
    protected IProductJPARepository productJPARepository;
    @Autowired
    protected IProductQuantityJPARepository productQuantityJPARepository;
    @Autowired
    protected IProductTopJPARepository productTopJPARepository;
    @Autowired
    protected ProductUseCase productUseCase;
    @Autowired
    protected ProductService productService;
    // 쿠폰
    @Autowired
    protected CouponRepository couponRepository;
    @Autowired
    protected ICouponJPARepository couponJPARepository;
    @Autowired
    protected ICouponQuantityJPARepository couponQuantityJPARepository;
    @Autowired
    protected IUserCouponJPARepository userCouponJPARepository;
    @Autowired
    protected CouponUseCase couponUseCase;
    @Autowired
    protected CouponService couponService;
    // 주문
    @Autowired
    protected OrderRepository orderRepository;
    @Autowired
    protected IOrderJPARepository orderJPARepository;
    @Autowired
    protected IOrderProductJPARepository orderProductJPARepository;
    @Autowired
    protected OrderSerivce orderSerivce;
    @Autowired
    protected OrderUseCase orderUseCase;
    // 결재
    @Autowired
    protected PaymentRepository paymentRepository;
    @Autowired
    protected IPaymentJPARepository paymentJPARepository;
    @Autowired
    protected PaymentUseCase paymentUseCase;
    @Autowired
    protected PaymentService paymentService;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected CustomLoggingFilter customLoggingFilter;

    // 레디스
    @Autowired
    protected RedisService redisService;

    @Autowired
    protected RedisRepository redisRepository;

    protected String baseUrl = "http://localhost:";
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        paymentJPARepository.deleteAll();
        orderProductJPARepository.deleteAll();
        orderJPARepository.deleteAll();
        userCouponJPARepository.deleteAll();
        couponQuantityJPARepository.deleteAll();
        couponJPARepository.deleteAll();
        productTopJPARepository.deleteAll();
        productQuantityJPARepository.deleteAll();
        productJPARepository.deleteAll();
        userJPARepository.deleteAll();
    }

}
