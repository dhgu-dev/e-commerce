import http from 'k6/http';
import {check, group, sleep} from 'k6';

export const options = {
    vus: 500, // 가상 사용자 10명
    duration: '60s', // 30초 동안 테스트 실행
    thresholds: {
        http_req_failed: ['rate<0.01'], // 실패율 1% 미만
        http_req_duration: ['p(95)<300'], // 95%의 요청이 200ms 이내에 응답
    }
};

export default function () {
    const baseUrl = "http://172.22.112.1:8080";

    group('API v1 Products - List', () => {
        const brandId = Math.floor(Math.random() * 20) + 1;
        const page = Math.floor(Math.random() * 10) + 1;
        const res = http.get(`${baseUrl}/api/v1/products?brandId=${brandId}&sort=likes_desc&page=${page}&size=10`);
        check(res, {
            'is status 200': (r) => r.status === 200,
        });
    });

    sleep(1);

    group('API v1 Products - Get by ID', () => {
        const productId = Math.floor(Math.random() * 20) + 1;
        const res = http.get(`${baseUrl}/api/v1/products/${productId}`);
        check(res, {
            'is status 200': (r) => r.status === 200,
        });
    });

    sleep(1);
}