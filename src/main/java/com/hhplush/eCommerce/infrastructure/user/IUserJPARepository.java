package com.hhplush.eCommerce.infrastructure.user;

import com.hhplush.eCommerce.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserJPARepository extends JpaRepository<User, Long> {

}
