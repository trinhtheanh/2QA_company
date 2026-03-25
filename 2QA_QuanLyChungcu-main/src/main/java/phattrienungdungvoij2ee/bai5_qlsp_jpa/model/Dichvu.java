package phattrienungdungvoij2ee.bai5_qlsp_jpa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "dichvu")
public class Dichvu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ten dich vu khong duoc de trong")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "provider", length = 150)
    private String provider;

    @Column(name = "unit", length = 50)
    private String unit;

    @Column(name = "cost_type", length = 50)
    private String costType;

    @Column(name = "cost_value", precision = 15, scale = 2)
    private BigDecimal costValue;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category_Dichvu category;

    @NotNull(message = "Gia khong duoc de trong")
    @DecimalMin(value = "0.0", message = "Gia phai lon hon hoac bang 0")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;
}