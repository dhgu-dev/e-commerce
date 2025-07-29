package com.loopers.domain.brand;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "brand")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrandModel extends BaseEntity {

    private String name;

    private String description;

    public BrandModel(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Brand name cannot be null or blank.");
        }
        if (description != null && description.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Brand description cannot be blank.");
        }
        this.name = name;
        this.description = description;
    }
}
