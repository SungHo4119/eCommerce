package com.hhplush.eCommerce.integration.config;


import com.hhplush.eCommerce.business.coupon.CouponLoader;
import com.hhplush.eCommerce.business.coupon.CouponService;
import com.hhplush.eCommerce.business.coupon.UserCouponLoader;
import com.hhplush.eCommerce.business.order.OrderLoader;
import com.hhplush.eCommerce.business.order.OrderService;
import com.hhplush.eCommerce.business.payment.PaymentLoader;
import com.hhplush.eCommerce.business.payment.PaymentService;
import com.hhplush.eCommerce.business.product.ProductLoader;
import com.hhplush.eCommerce.business.product.ProductService;
import com.hhplush.eCommerce.business.user.UserLoader;
import com.hhplush.eCommerce.business.user.UserService;
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
import com.hhplush.eCommerce.infrastructure.user.IUserJPARepository;
import com.hhplush.eCommerce.infrastructure.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Execution(ExecutionMode.SAME_THREAD)
@SpringBootTest
@ActiveProfiles("test")
public class IntegrationTest {

    // 유저
    @Autowired
    protected IUserJPARepository userJPARepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserLoader userLoader;

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
    protected ProductService productService;
    @Autowired
    protected ProductLoader productLoader;
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
    protected CouponService couponService;
    @Autowired
    protected CouponLoader couponLoader;
    @Autowired
    protected UserCouponLoader userCouponLoader;
    // 주문
    @Autowired
    protected OrderRepository orderRepository;
    @Autowired
    protected IOrderJPARepository orderJPARepository;
    @Autowired
    protected IOrderProductJPARepository orderProductJPARepository;
    @Autowired
    protected OrderLoader orderLoader;
    @Autowired
    protected OrderService orderService;
    // 결재
    @Autowired
    protected PaymentRepository paymentRepository;
    @Autowired
    protected IPaymentJPARepository paymentJPARepository;
    @Autowired
    protected PaymentService paymentService;
    @Autowired
    protected PaymentLoader paymentLoader;

    @BeforeEach
    void setUp() {
        userJPARepository.deleteAll();
        productJPARepository.deleteAll();
        couponJPARepository.deleteAll();
        orderJPARepository.deleteAll();
        paymentJPARepository.deleteAll();
    }
}
