# Class Diagrams

## 브랜드 & 상품

### 상품 목록 조회

```mermaid
classDiagram
    class Product {
        -Long id
        -String name
        -Integer price
        -Brand brand
    }
    class Brand {
        -Long id
        -String name
    }
    class Like {
        -Member member
        -Product product
    }
    class Member {
        -Long id
    }

    Brand "1" -- "N" Product : 포함
    Product "1" -- "N" Like : 소유
    Member "1" -- "N" Like : 소유
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
    Brand "1" -- "N" Product : 포함
```

### 상품 상세 정보 조회

```mermaid
classDiagram
    class Product {
        -Long id
        -String name
        -Integer price
    }
    class Like {
        -Member member
        -Product product
    }
    Product "1" -- "N" Like : 소유
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
        +like(Product)
    }
    class Product {
        -Long id
        -String name
        -Long price
    }
    Member "1" -- "N" Like : 소유
    Product "1" -- "N" Like : 소유
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
        +unlike(Product)
    }
    class Product {
        -Long id
        -String name
        -Long price
    }
    Member "1" -- "N" Like : 소유
    Product "1" -- "N" Like : 소유
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
    Like "N" -- "1" Product : 참조
```

## 주문 및 결제

### 주문 요청

```mermaid
classDiagram
    class Order {
        -Member member
        -List<OrderItem> orderItems
        -enum status
        +create(Member, List<OrderItem>)
    }
    class OrderItem {
        -Product product
        -int quantity
    }
    class Product {
        -Long id
        -int stock
        +decreaseStock(int)
    }
    class Member {
        -Long id
        -Point point
        +usePoint(point)
    }

    Order "1" o-- "N" OrderItem : 포함
    OrderItem "N" -- "1" Product : 참조
    Member "1" -- "N" Order : 생성
    Member "1" -- "1" Point : 소유
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
        -Product product
        -int quantity
    }
    class Product {
        -Long id
        -String name
        -Long price
    }

    Member "1" -- "N" Order : 소유
    Order "1" o-- "N" OrderItem : 포함
    OrderItem "N" -- "1" Product : 참조
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
    }
    class OrderItem {
        -Product product
        -int quantity
    }
    class Product {
        -Long id
        -String name
        -Long price
    }

    Member "1" -- "N" Order : 소유
    Order "1" o-- "N" OrderItem : 포함
    OrderItem "N" -- "1" Product : 참조
```