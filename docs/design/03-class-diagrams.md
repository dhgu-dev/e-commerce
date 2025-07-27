# Class Diagrams

## 브랜드 & 상품

### 상품 목록 조회

```mermaid
classDiagram
    class Product {
        -Long id
        -String name
        -Long price
        -Brand brand
    }
    class Brand {
        -Long id
        -String name
    }
    class Like {
        -Product product
    }

    Brand "1" -- "N" Product : 보유
    Product "1" -- "N" Like : 집계
```

### 브랜드 정보 조회

```mermaid
classDiagram
    class Brand {
        -Long id
        -String name
    }
    class Product {
        -Long id
        -String name
        -Brand brand
    }
    
    Brand "1" -- "N" Product : 보유
```

### 상품 상세 정보 조회

```mermaid
classDiagram
    class Product {
        -Long id
        -String name
        -Long price
    }
    class Like {
        -Product product
    }
    Product "1" -- "N" Like : 집계
```

## 좋아요 (Likes)

### 상품 좋아요 등록

```mermaid
classDiagram
    class Like {
        -Member member
        -Product product
    }
    class Member {
        -Long id
    }
    class Product {
        -Long id
        -String name
        -Long price
    }
    Member "1" -- "N" Like : 등록
    Product "1" -- "N" Like : 대상
```

### 상품 좋아요 취소

```mermaid
classDiagram
    class Like {
        -Member member
        -Product product
    }
    class Member {
        -Long id
    }
    class Product {
        -Long id
        -String name
        -Long price
    }
    Member "1" -- "N" Like : 취소
    Product "1" -- "N" Like : 대상
```

### 내가 좋아요 한 상품 목록 조회

```mermaid
classDiagram
    class Member {
        -Long id
    }
    class Like {
        -Member member
        -Product product
    }
    class Product {
        -Long id
        -String name
        -Long price
    }
    Member "1" -- "N" Like : 소유
    Product "1" -- "N" Like : 참조
```

## 주문 및 결제

### 주문 요청

```mermaid
classDiagram
    class Order {
        -Member member
        -List<OrderItem> orderItems
        -enum status
    }
    class OrderItem {
        -Order order
        -String productName
        -Long productPrice
        -int quantity
    }
    class Product {
        -Long id
        -String name
        -Long price
        -int stock
        +decreaseStock(int)
    }
    class Member {
        -Long id
        -Point point
        +usePoint(point)
    }

    Order "1" o-- "N" OrderItem : 포함
    Member "1" -- "N" Order : 생성
    OrderItem "1" -- "1" Product : 참조
```

### 유저의 주문 목록 조회

```mermaid
classDiagram
    class Member {
        -Long id
    }
    class Order {
        -Long id
        -Member member
        -enum status
        -List<OrderItem> orderItems
    }
    class OrderItem {
        -Order order
        -String productName
        -Long productPrice
        -int quantity
    }

    Member "1" -- "N" Order : 소유
    Order "1" o-- "N" OrderItem : 포함
```

### 단일 주문 상세 조회

```mermaid
classDiagram
    class Member {
        -Long id
    }
    class Order {
        -Long id
        -Member member
        -enum status
        -List<OrderItem> orderItems
        +hasOwned(member)
    }
    class OrderItem {
        -Order order
        -String productName
        -Long productPrice
        -int quantity
    }
    class Product {
        -Long id
        -String name
        -Long price
    }

    Member "1" -- "N" Order : 소유
    Order "1" o-- "N" OrderItem : 포함
    OrderItem "1" -- "1" Product : 참조
```