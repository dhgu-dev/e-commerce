package com.loopers.domain.member;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.member.enums.Gender;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Entity
@Table(name = "member")
public class MemberModel extends BaseEntity {

    @Getter
    private String userId;

    @Enumerated(EnumType.STRING)
    @Getter
    private Gender gender;

    @Getter
    private LocalDate birthdate;

    @Getter
    private String email;

    @Getter
    private Long points;

    @Version
    @Getter
    @ColumnDefault("0")
    private Long version;

    protected MemberModel() {
    }

    public MemberModel(String userId, Gender gender, String birthdate, String email) {
        this(userId, gender, birthdate, email, 0L);
    }

    public MemberModel(String userId, Gender gender, String birthdate, String email, Long points) {
        if (userId == null || userId.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이름은 비어있을 수 없습니다.");
        }
        if (userId.length() > 10 || !userId.matches("^[a-zA-Z0-9]+$")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 형식의 아이디입니다.");
        }
        if (gender == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "성별은 비어있을 수 없습니다.");
        }
        if (birthdate == null || birthdate.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일은 비어있을 수 없습니다.");
        }
        LocalDate parsedBirthdate = null;
        try {
            parsedBirthdate = LocalDate.parse(birthdate);
        } catch (DateTimeParseException e) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 형식의 생년월일입니다.");
        }
        if (email == null || email.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일은 비어있을 수 없습니다.");
        }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 형식의 이메일입니다.");
        }
        if (points == null || points < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트는 0 이상이어야 합니다.");
        }

        this.userId = userId;
        this.gender = gender;
        this.birthdate = parsedBirthdate;
        this.email = email;
        this.points = points;
        this.version = 0L;
    }

    public Long chargePoints(Long amount) {
        if (amount == null || amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "충전 금액은 0보다 커야 합니다.");
        }
        this.points += amount;
        return this.points;
    }

    public void usePoints(long amount) {
        if (amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용 금액은 0보다 커야 합니다.");
        }
        if (this.points < amount) {
            throw new CoreException(ErrorType.CONFLICT, "포인트가 부족합니다.");
        }
        this.points -= amount;
    }
}
