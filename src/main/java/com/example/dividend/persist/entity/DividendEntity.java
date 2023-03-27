package com.example.dividend.persist.entity;


import com.example.dividend.model.Dividend;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name="DIVIDEND")
@Getter
@ToString
@NoArgsConstructor

@Table(
    uniqueConstraints = {
            @UniqueConstraint(
                    columnNames = {"companyId", "date"}
            )
    }
)

public class DividendEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;
    private LocalDateTime date;
    private  String dividend;

    public DividendEntity(Long companyId, Dividend dividend) {
        this.companyId = companyId;
        this.date = dividend.getDate();
        this.dividend = dividend.getDividend();
    }
}
