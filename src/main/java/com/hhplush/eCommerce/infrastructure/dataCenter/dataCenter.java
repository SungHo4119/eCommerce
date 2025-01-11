package com.hhplush.eCommerce.infrastructure.dataCenter;

import com.hhplush.eCommerce.business.dataCenter.IDataCenter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class dataCenter implements IDataCenter {

    public void sendDataCenter() {
        // 데이터 센터 전송
    }

}
