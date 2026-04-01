package com.restaurant.BeefChefBackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restaurant.BeefChefBackend.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "tableId")
    private Tables table;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<OrderItems> item;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private BigDecimal orderTotal;

    private LocalDateTime paidAt;

    @ManyToOne
    @JoinColumn(name = "shiftId")
    private Shift shift;

}
