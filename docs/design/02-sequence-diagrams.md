# Sequence Diagrams

## 브랜드 & 상품

### 상품 목록 조회

GET /api/v1/products?brandId={brandId}&sort={sort}&page={page}&size={size}

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as ProductController
    participant BS as BrandService
    participant PS as ProductService
    participant LS as LikeService

    C->>PC: GET /api/v1/products?brandId=...&sort=...&page=...&size=...
    PC->>PC: 유효성 검증 (page, size, sort)
    opt 유효하지 않은 파라미터
        PC-->>C: 400 Bad Request
    end
    alt brandId 가 없는 경우
        PC->>PS: 상품 목록 조회 (pageable)
        loop 상품 목록
            PC->>LS: 총 좋아요 수 조회 (product)
        end
    else brandId 가 있는 경우
        PC->>BS: 브랜드 조회 (brandId)
        alt 존재하지 않는 브랜드
            BS-->>PC: 404 Not Found
        else 브랜드가 존재하는 경우
            BS-->>PC: 브랜드 정보 반환
            PC->>PS: 브랜드 상품 목록 조회 (brand, pageable)
            loop 상품 목록
                PC->>LS: 총 좋아요 수 조회(product)
            end
        end
    end
```

### 브랜드 정보 조회

GET /api/v1/brands/{brandId}

```mermaid
sequenceDiagram
    participant C as Client
    participant BC as BrandController
    participant BS as BrandService
    participant PS as ProductService

    C->>BC: GET /api/v1/brands/{brandId}
    BC->>BS: 브랜드 조회 (brandId)
    alt 존재하지 않는 브랜드
        BS-->>BC: 404 Not Found
    else 브랜드가 존재하는 경우
        BS-->>BC: 브랜드 정보 반환
        BC->>PS: 브랜드 대표 상품 목록 조회 (brand)
        BC-->>C: 200 OK + 브랜드 정보와 대표 상품 목록
    end
```

### 상품 상세 정보 조회

GET /api/v1/products/{productId}

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as ProductController
    participant PS as ProductService
    participant LS as LikeService


    C->>PC: GET /api/v1/products/{productId}
    PC->>PS: 상품 상세 정보 조회 (productId)
    alt 존재하지 않는 상품 ID
        PS-->>PC: 404 Not Found
    else 상품이 존재하는 경우
        PS-->>PC: 상품 정보 반환
        PC->>LS: 특정 상품의 총 좋아요 수 조회 (product)
        PC-->>C: 200 OK + 상품 상세 정보와 총 좋아요 수
    end
```

## 좋아요 (Likes)

### 상품 좋아요 등록

POST /api/v1/products/{productId}/likes

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as ProductController
    participant MS as MemberService
    participant PS as ProductService
    participant LS as LikeService
    participant LR as LikeRepository

    C->>PC: POST /api/v1/products/{productId}/likes -H X-USER-ID
    PC->>MS: 사용자 인증 처리 (X-USER-ID)
    alt 인증 실패: 헤더 없음 | 사용자 미 존재
        MS-->>PC: 401 Unauthorized
    else 인증 성공
        MS-->>PC: 사용자 정보 반환
        PC->>PS: 상품 정보 조회 (productId)
        alt 존재하지 않는 상품
            PS-->>PC: 404 Not Found
        else 상품이 존재하는 경우
            PS-->>PC: 상품 정보 반환
            PC->>LS: 좋아요 등록 요청 (member, product)
            LS->>LR: 좋아요 등록 이력 조회 (member, product)
            alt 회원이 해당 상품에 좋아요를 누른 이력이 없는 경우
                LS->>LR: 좋아요 등록 (member, product)
                LR-->>LS: 좋아요 정보 반환
            else 회원이 해당 상품에 좋아요를 누른 이력이 있는 경우
                LR-->>LS: 좋아요 정보 반환
            end
        end
    end
```

### 상품 좋아요 취소

DELETE /api/v1/products/{productId}/likes

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as ProductController
    participant MS as MemberService
    participant PS as ProductService
    participant LS as LikeService
    participant LR as LikeRepository

    C->>PC: DELETE /api/v1/products/{productId}/likes -H X-USER-ID
    PC->>MS: 사용자 인증 처리 (X-USER-ID)
    alt 인증 실패: 헤더 없음 | 사용자 미 존재
        MS-->>PC: 401 Unauthorized
    else 인증 성공
        MS-->>PC: 사용자 정보 반환
        PC->>PS: 상품 정보 조회 (productId)
        alt 존재하지 않는 상품
            PS-->>PC: 404 Not Found
        else 상품이 존재하는 경우
            PS-->>PC: 상품 정보 반환
            PC->>LS: 좋아요 취소 요청 (member, product)
            LS->>LR: 좋아요 등록 이력 조회 (member, product)
            alt 회원이 해당 상품에 좋아요를 누른 이력이 있는 경우
                LR-->>LS: 좋아요 정보 반환
                LS->>LR: 좋아요 삭제 (like)
                PC-->C: 204 No Content
            else 회원이 해당 상품에 좋아요를 누른 이력이 없는 경우
                PC-->>C: 204 No Content
            end
        end
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
    MC->>MC: 유효성 검증 (page, size)
    opt 유효하지 않은 파라미터
        MC-->>C: 400 Bad Request
    end
    MC->>MS: 사용자 인증 확인 (X-USER-ID)
    alt 인증 실패 (헤더 없음 | 사용자 미 존재)
        MS-->>MC: 401 Unauthorized
    else 인증 성공
        MS-->>MC: 사용자 정보 반환
        MC->>LS: 사용자의 '좋아요' 목록 조회 (member, pageable)
        alt 사용자의 '좋아요' 목록이 없는 경우
            LS-->>MC: 빈 목록 반환
            MC-->>C: 200 OK + 빈 목록
        else 사용자의 '좋아요' 목록이 있는 경우
            LS-->>MC: 좋아요 목록 반환
            MC->>PS: 좋아요 목록 기반으로 상품 목록 조회 (productIds)
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
    participant P as Product
    participant M as Member

    C->>OC: POST /api/v1/orders -H X-USER-ID (productIds, quantities)
    OC->>MS: 사용자 인증 처리 (X-USER-ID)
    alt 인증 실패 (헤더 없음 | 사용자 미 존재)
        MS-->>OC: 401 Unauthorized
    else 인증 성공
        MS-->>OC: 사용자 정보 반환 
        OC->>OS: 주문 요청 (member, productIds, quantities)
        activate OS
        loop 각 주문 상품
            OS->>PS: 재고 차감 요청 (productId, quantity)
            alt 존재하지 않는 상품
                PS-->>OS: 404 Not Found
            else 존재하는 상품
                PS->>P: 재고 차감 (quantity)
                alt 재고 부족
                    P-->>PS: 409 Conflict
                else 재고 충분한 경우
                    P-->>PS: 남은 재고 반환
                end
                PS-->>OS: 남은 재고 반환
            end
        end
        OS->>PS: 상품들의 총 가격 합계 조회 (productIds, quantities)
        PS-->>OS: 상품들의 총 합산된 가격 반환
        OS->>MS: 포인트 차감 요청 (member, points)
        MS->>M: 포인트 차감 (points)
        alt 포인트 부족
            M-->>MS: 409 Conflict
        else 포인트 충분한 경우
            M-->>MS: 남은 포인트 반환
            OS->>OR: 주문 시점의 상품 정보 기록 (products, quantities)
            OR-->>OS: 주문 상품 정보 반환
            OS->>OR: 주문 정보 저장 (member, orderItems, totalPrice)
            OR-->>OS: 주문 정보 저장 결과
            OS->>ES: 주문 정보 전송 (order)
            alt 외부 시스템 전송 실패
                ES-->>OS: 500 Internal Server Error
            else 외부 시스템 전송 성공
                ES-->>OS: 주문 정보 전송 결과
                OS-->>OC: 주문 정보 반환
            end
        end
        deactivate OS
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
        MC->>OS: 사용자의 주문 목록 조회 (member)
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
        OC->>OS: 주문 상세 조회 (orderId)
        alt 주문 정보가 존재하지 않는 경우
            OS-->>OC: 404 Not Found
        else 주문 정보가 존재하는 경우
            OS-->>OC: 주문 정보 반환
            OC->>O: 주문자와 사용자 일치 여부 확인 (member)
            alt 다른 사용자의 주문인 경우
                O-->>OC: 403 Forbidden
            else 일치하는 경우
                OC->>O: 주문 상품 목록과 주문 수량 조회
                O-->>OC: 주문한 상품 목록과 각 상품 별 주문한 수량 반환
            end
        end
    end
```