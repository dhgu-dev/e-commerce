# Sequence Diagrams

## 브랜드 & 상품

### 상품 목록 조회

GET /api/v1/products?brandId={brandId}&sort={sort}&page={page}&size={size}

```mermaid
sequenceDiagram
    participant Client
    participant ProductController
    participant ProductFacade
    participant BrandFacade
    participant ProductService
    participant LikeService
    participant ProductRepository
    participant LikeRepository

    Client->>ProductController: GET /api/v1/products?brandId=...&sort=...&page=...&size=...
    ProductController->>ProductController: validate(query)
    opt 유효하지 않은 파라미터
        ProductController-->>Client: 400 Bad Request
    end
    alt brandId가 없는 경우
        ProductController->>ProductFacade: getAllProduct(pageable)
        ProductFacade->>ProductService: getAll(pageable)
        ProductService->>ProductRepository: findAll(pageable)
        ProductFacade->>LikeService: getLikeCount(products)
        LikeService->>LikeRepository: countLikesByProductId(productIds)
    else brandId가 있는 경우
        ProductController->>BrandFacade: existsByBrandId(brandId)
        opt 존재하지 않는 브랜드
            ProductController-->>Client: 404 Not Found
        end
        ProductController->>BrandFacade: getAllProducts(brandId, pageable)
        BrandFacade->>ProductService: getAll(brandId, pageable)
        ProductService->>ProductRepository: findAll(brandId, pageable)
        BrandFacade->>LikeService: getLikeCount(products)
        LikeService->>LikeRepository: countLikesByProductId(productIds)
    end
```

### 브랜드 정보 조회

GET /api/v1/brands/{brandId}

```mermaid
sequenceDiagram
    participant Client
    participant BrandController
    participant BrandFacade
    participant BrandRepository

    Client->>BrandController: GET /api/v1/brands/{brandId}
    BrandController->>BrandFacade: getBrand(brandId)
    opt 존재하지 않는 브랜드 ID
        BrandController-->>Client: 404 Not Found
    end
    BrandFacade->>BrandService: get(brandId)
    BrandService-->>BrandRepository: find(brandId)
```

### 상품 상세 정보 조회

GET /api/v1/products/{productId}

```mermaid
sequenceDiagram
    participant Client
    participant ProductController
    participant ProductFacade
    participant BrandFacade
    participant ProductService
    participant LikeService
    participant ProductRepository
    participant LikeRepository

    Client->>ProductController: GET /api/v1/products/{productId}
    ProductController->>ProductFacade: getProduct(productId)
    opt 존재하지 않는 상품 ID
        ProductController-->>Client: 404 Not Found
    end
    ProductFacade->>ProductService: get(productId)
    ProductService->>ProductRepository: find(productId)
    ProductFacade->>LikeService: getLikeCount(product)
    LikeService->>LikeRepository: countLikesByProductId(productId)
```

## 좋아요 (Likes)

### 상품 좋아요 등록

POST /api/v1/products/{productId}/likes

```mermaid
sequenceDiagram
    participant Client
    participant ProductController
    participant MemberFacade
    participant ProductFacade
    participant LikeFacade
    participant LikeService
    participant LikeRepository

    Client->>ProductController: POST /api/v1/products/{productId}/likes -H X-USER-ID
    opt X-USER-ID 헤더가 없는 경우
        ProductController-->>Client: 400 Bad Request
    end
    ProductController->>MemberFacade: getMember(memberId)
    opt 존재하지 않는 회원인 경우 
        ProductController-->>Client: 401 Unauthorized
    end
    ProductController->>ProductFacade: getProduct(productId)
    opt 존재하지 않는 상품 ID
        ProductController-->>Client: 404 Not Found
    end
    ProductController->>LikeFacade: likeProduct(memberInfo, productInfo)
    LikeFacade->>LikeService: like(member, product)
    LikeService->>LikeRepository: exists(memberId, productId)
    opt 회원이 해당 상품에 좋아요를 누른 이력이 없는 경우 
        LikeService-->>LikeRepository: save(like)
    end
```

### 상품 좋아요 취소

DELETE /api/v1/products/{productId}/likes

```mermaid
sequenceDiagram
    participant Client
    participant ProductController
    participant MemberFacade
    participant ProductFacade
    participant LikeFacade
    participant LikeService
    participant LikeRepository

    Client->>ProductController: DELETE /api/v1/products/{productId}/likes -H X-USER-ID
    opt X-USER-ID 헤더가 없는 경우
        ProductController-->>Client: 400 Bad Request
    end
    ProductController->>MemberFacade: getMember(memberId)
    opt 존재하지 않는 회원인 경우 
        ProductController-->>Client: 401 Unauthorized
    end
    ProductController->>ProductFacade: getProduct(productId)
    opt 존재하지 않는 상품 ID
        ProductController-->>Client: 404 Not Found
    end
    ProductController->>LikeFacade: unlikeProduct(memberInfo, productInfo)
    LikeFacade->>LikeService: unlike(member, product)
    LikeService->>LikeRepository: exists(memberId, productId)
    opt 회원이 해당 상품에 좋아요를 누른 이력이 있는 경우 
        LikeService-->>LikeRepository: delete(like)
    end
```

### 내가 좋아요 한 상품 목록

GET /api/v1/users/{userId}/likes?page={page}&size={size}

```mermaid  
sequenceDiagram
    participant C as Client
    participant MC as MemberController
    participant MS as MemberService
    participant LS as LikeService
    participant PS as ProductService

    C->>MC: GET /api/v1/users/{userId}/likes?page={page}&size={size} -H X-USER-ID
    MC->>MS: 사용자 인증 확인 (X-USER-ID)
    alt 인증 실패 (헤더 없음 | 사용자 미 존재)
        MS-->>MC: 401 Unauthorized
    else 인증 성공
        MS-->>MC: 사용자 정보 반환
        MC->>LS: 사용자의 '좋아요' 목록 조회 (memberId, pageable)
        alt 사용자의 '좋아요' 목록이 없는 경우
            LS-->>MC: 빈 목록 반환
            MC-->>C: 200 OK + 빈 목록
        else 사용자의 '좋아요' 목록이 있는 경우
            LS-->>MC: 좋아요 목록 반환
            MC->>PS: 상품 목록 조회 (productIds)
            PS-->>MC: 상품 목록 반환
            MC-->>C: 200 OK + 상품 목록
        end
    end
```

## 주문 및 결제

### 주문 요청

POST /api/v1/orders

```mermaid
sequenceDiagram
    participant C as Client
    participant OC as OrderController
    participant OS as OrderService
    participant PS as ProductService
    participant MS as MemberService
    participant OR as OrderRepository
    participant PR as ProductRepository
    participant ES as ExternalSystem

    C->>OC: POST /api/v1/orders -H X-USER-ID (productIds, quantities)
    OC->>MS: 사용자 인증 처리
    alt 인증 실패
        MS-->>OC: 401 Unauthorized
    else 인증 성공
        MS-->>OC: 사용자 정보 반환 (X-USER-ID)
        OC->>PS: 상품 목록 상태 조회 (productIds)
        alt 상품 정보를 찾을 수 없음
            PS-->>OC: 404 Not Found
        else 상품 목록이 유효한 경우
            activate OS
            OC->>OS: 주문 요청 (memberId, productIds, quantities)
            loop 각 주문 상품
                OS->>PS: 재고 차감 요청 (productId, quantity)
                PS->>PR: 재고 현황 조회 (productId)
                alt 상품 없거나 재고 부족
                    PS-->>OS: 409 Conflict
                    OS-->>OS: 트랜잭션 롤백
                else 재고 충분한 경우
                    PS->>PR: 재고 차감 (productId, quantity)
                    PR-->>PS: 재고 차감 결과
                end
            end
            OS->>PS: 상품들의 총 가격 합계 조회 (productIds, quantities)
            PS-->>OS: 상품들의 총 합산된 가격 반환
            OS->>MS: 포인트 차감 요청 (memberId, points)
            MS->>MR: 사용자 포인트 조회 (memberId)
            alt 포인트 부족
                MS-->>OS: 409 Conflict
                OS->>OS: 트랜잭션 롤백
            else 포인트 충분한 경우
                MS->>MR: 포인트 차감 (memberId, points)
                MR-->>MS: 포인트 차감 결과
                OS->>OR: 주문 정보 저장 (memberId, productIds, quantities)
                OS->>ES: 주문 정보 전송 (memberId, productIds, quantities)
                alt 외부 시스템 전송 실패
                    ES-->>OS: 500 Internal Server Error
                    OS->>OS: 트랜잭션 롤백
                else 외부 시스템 전송 성공
                    ES-->>OS: 주문 정보 전송 결과
                    OS-->>OC: 주문 정보 반환
                end
            end
            deactivate OS
        end
    end
```

### 유저의 주문 목록 조회

GET /api/v1/users/{userId}/orders

```mermaid
sequenceDiagram
    participant C as Client
    participant MC as MemberController
    participant MS as MemberService
    participant OS as OrderService

    C->>MC: GET /api/v1/users/{userId}/orders -H X-USER-ID
    MC->>MS: 사용자 인증 처리 (X-USER-ID)
    alt 인증 실패: 헤더 없음 | 사용자 미 존재 | 쿼리와 헤더 값 다름
        MS-->>MC: 401 Unauthorized
    else 인증 성공
        MS-->>MC: 사용자 정보 반환
        MC->>OS: 사용자의 주문 목록 조회 (memberId)
        alt 주문 목록이 없는 경우
            OS-->>MC: 빈 목록 반환
        else 주문 목록이 있는 경우
            OS-->>MC: 주문 목록 반환
            MC-->>C: 200 OK + 주문 목록
        end
    end
```

### 단일 주문 상세 조회

GET /api/v1/orders/{orderId}

```mermaid
sequenceDiagram
    participant C as Client
    participant OC as OrderController
    participant MS as MemberService
    participant OS as OrderService
    participant PS as ProductService
    participant O as Order

    C->>OC: GET /api/v1/orders/{orderId} -H X-USER-ID
    OC->>MS: 사용자 인증 처리 (X-USER-ID)
    alt 인증 실패: 헤더 없음 | 사용자 미 존재
        MS-->>OC: 401 Unauthorized
    else 인증 성공
        MS-->>OC: 사용자 정보 반환
        OC->>OS: 주문 상세 조회 요청 (orderId)
        alt 주문 정보가 존재하지 않는 경우
            OS-->>OC: 404 Not Found
        else 주문 정보가 존재하는 경우
            OS-->>OC: 주문 정보 반환
            OC->>O: 주문자와 사용자 일치 여부 확인 (memberId)
            alt 다른 사용자의 주문인 경우
                O-->>OC: 403 Forbidden
            else 일치하는 경우
                OC->>O: 주문 상품 목록과 주문 수량 조회
                O-->>OC: 주문한 상품 목록과 각 상품 별 주문한 수량 반환
            end
        end
    end
```