
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    scenarios: {
        constant_request_rate: {
            executor: 'constant-arrival-rate',
            rate: 100,
            timeUnit: '1s',
            duration: '1m',
            preAllocatedVUs: 50,
            maxVUs: 100,
        },
    },
};


export default function () { 
    const userId = Math.floor(Math.random() * 50000) + 1; // 1~100000 유저 아이디
    const issued = Math.floor(Math.random() * 4) + 1; // 1~4 쿠폰 ID

    const url = `http://host.docker.internal:8080/api/coupons/${issued}/issued/V2`;
    const payload = JSON.stringify({
        userId: userId,
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, payload, params);
    check(res, { 'status was 200': r => r.status == 200 });
    sleep(0.5);
}
