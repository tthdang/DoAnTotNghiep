package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.PromotionCreateProductRequest;
import com.restaurant.BeefChefBackend.dto.request.PromotionCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.PromotionUpdateRequest;
import com.restaurant.BeefChefBackend.dto.response.PromotionResponse;
import com.restaurant.BeefChefBackend.entity.*;
import com.restaurant.BeefChefBackend.enums.DiscountType;
import com.restaurant.BeefChefBackend.enums.PromotionStatus;
import com.restaurant.BeefChefBackend.enums.PromotionType;
import com.restaurant.BeefChefBackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class PromotionService {
    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotionItemRepository promotionItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    private PromotionResponse toResponse(Promotion promotion) {

        return PromotionResponse.builder()
                .promotionId(promotion.getPromotionId())
                .code(promotion.getCode())
                .promotionType(promotion.getPromotionType())
                .discountType(promotion.getDiscountType())
                .discountValue(promotion.getDiscountValue())
                .minOrderValue(promotion.getMinOrderValue())
                .maxDiscountValue(promotion.getMaxDiscountValue())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .status(promotion.getStatus())
                .usageLimit(promotion.getUsageLimit())
                .usedCount(promotion.getUsedCount())
                .build();
    }

    //create
    public PromotionResponse createOrder(PromotionCreateRequest request) {
        Promotion promotion = new Promotion();

        promotion.setCode(request.getCode());
        promotion.setPromotionType(request.getPromotionType());
        promotion.setDiscountType(request.getDiscountType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setMinOrderValue(request.getMinOrderValue());
        promotion.setMaxDiscountValue(request.getMaxDiscountValue());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setStatus(PromotionStatus.AVAILABLE);
        promotion.setUsageLimit(request.getUsageLimit());
        promotion.setUsedCount(0);

        Promotion save = promotionRepository.save(promotion);

        return toResponse(save);
    }

    public PromotionResponse createForproduct(PromotionCreateProductRequest request) {


        Promotion promotion = new Promotion();

        promotion.setCode(request.getCode());
        promotion.setPromotionType(PromotionType.ITEM);
        promotion.setDiscountType(request.getDiscountType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setMinOrderValue(request.getMinOrderValue());
        promotion.setMaxDiscountValue(request.getMaxDiscountValue());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setStatus(PromotionStatus.AVAILABLE);
        promotion.setUsageLimit(request.getUsageLimit());
        promotion.setUsedCount(0);

        Promotion save = promotionRepository.save(promotion);

        Products product = productRepository.findById(request.getProductId()).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thây món ăn!")
        );

        PromotionItem promotionItem = PromotionItem.builder()
                .promotion(save)
                .product(product)
                .build();

        promotionItemRepository.save(promotionItem);

        return toResponse(save);
    }

    //update
    public PromotionResponse update(Long id, PromotionUpdateRequest request) {
        Promotion promotion = promotionRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy Khuyến mãi!")
        );

        promotion.setCode(request.getCode());
        promotion.setPromotionType(request.getPromotionType());
        promotion.setDiscountType(request.getDiscountType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setMinOrderValue(request.getMinOrderValue());
        promotion.setMaxDiscountValue(request.getMaxDiscountValue());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setStatus(request.getStatus());
        promotion.setUsageLimit(request.getUsageLimit());
        promotion.setUsedCount(request.getUsedCount());

        Promotion save = promotionRepository.save(promotion);
        return toResponse(save);

    }

    public List<PromotionResponse> getAll() {
        return promotionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PromotionResponse getById(Long id) {
        Promotion promotion = promotionRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy Khuyến mãi!")
        );

        return toResponse(promotion);
    }

    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }

    //ap dung khuyen mai
    public void applyPromotion(Integer orderId, String code) {
        Promotion promotion = promotionRepository.findByCode(code).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy mã khuyến mãi!")
        );

        if (promotion.getStatus() == PromotionStatus.OUT_OF_STOCK) {
            throw new IllegalArgumentException("Mã khuyến mãi không hoạt động!");
        }

        //ktra han su dung
        if (LocalDate.now().isBefore(promotion.getStartDate()) || LocalDate.now().isAfter(promotion.getEndDate())) {
            throw new IllegalArgumentException("Mã khuyến mãi đã hết hạn!");
        }

        if (promotion.getUsedCount() >= promotion.getUsageLimit()) {
            throw new IllegalArgumentException("Mã khuyến mãi đã đạt tối đa lượt sử dụng");
        }

        Orders order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy order: " + orderId)
        );


        if (promotion.getPromotionType() == PromotionType.ORDER) {
            applyOrderPromotion(order, promotion);
        }

        if (promotion.getPromotionType() == PromotionType.ITEM) {
            applyItemPromotion(order, promotion);
        }


    }

    public void applyOrderPromotion(Orders order, Promotion promotion) {
        BigDecimal total = order.getOrderTotal();

        BigDecimal rankDiscount = calculateRankDiscount(order);

        if (promotion.getMinOrderValue() != null && total.compareTo(promotion.getMinOrderValue()) < 0) {
            throw new IllegalArgumentException("Hoá đơn không đủ điều kiện áp dụng khuyến mãi!");
        }

        BigDecimal promoDiscount = BigDecimal.ZERO;

        if (promotion.getDiscountType() == DiscountType.PERCENT) {
            promoDiscount = total.multiply(promotion.getDiscountValue())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        } else {
            promoDiscount = promotion.getDiscountValue();
        }

        //lay tien giam gia tối đa
        if (promotion.getMaxDiscountValue() != null) {
            promoDiscount = promoDiscount.min(promotion.getMaxDiscountValue());
        }

        if (promoDiscount.compareTo(total) > 0) {
            promoDiscount = total;
        }

        order.setPromotion(promotion);
        order.setDiscountAmount(promoDiscount);
        order.setUserRankDiscount(rankDiscount);
        order.setFinalAmount(total.subtract(promoDiscount).subtract(rankDiscount));

        promotion.setUsedCount(promotion.getUsedCount() + 1);
        if (promotion.getUsedCount() >= promotion.getUsageLimit()) {
            promotion.setStatus(PromotionStatus.OUT_OF_STOCK);
        }

        promotionRepository.save(promotion);
        orderRepository.save(order);
    }

    public void applyItemPromotion(Orders order, Promotion promotion) {
        BigDecimal total = order.getOrderTotal() != null ? order.getOrderTotal() : BigDecimal.ZERO;
        BigDecimal rankDiscount = calculateRankDiscount(order);

        if (promotion.getMinOrderValue() != null && total.compareTo(promotion.getMinOrderValue()) < 0) {
            throw new IllegalArgumentException("Hoá đơn không đủ điều kiện!");
        }

        // Tìm món được áp dụng
        BigDecimal promoDiscount = calculateItemPromoDiscount(order, promotion);

        if (promoDiscount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Không có món nào đủ điều kiện áp dụng khuyến mãi!");
        }

        if (promoDiscount.compareTo(total) > 0) {
            promoDiscount = total;
        }

        order.setPromotion(promotion);
        order.setDiscountAmount(promoDiscount);           // chỉ promotion
        order.setUserRankDiscount(rankDiscount);
        order.setFinalAmount(total.subtract(promoDiscount).subtract(rankDiscount));

        // update promotion usage
        promotion.setUsedCount(promotion.getUsedCount() + 1);
        if (promotion.getUsedCount() >= promotion.getUsageLimit()) {
            promotion.setStatus(PromotionStatus.OUT_OF_STOCK);
        }

        promotionRepository.save(promotion);
        orderRepository.save(order);
    }

    public BigDecimal applyRankDiscount(Orders orders) {
        BigDecimal total = orders.getOrderTotal();

        if (orders.getUser() == null || orders.getUser().getRank() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal percent = orders.getUser().getRank().getDiscount();

        if (percent == null || percent.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return total.multiply(percent)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    // Tính giảm giá theo hạng thành viên
    public BigDecimal calculateRankDiscount(Orders order) {
        if (order.getUser() == null || order.getUser().getRank() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal percent = order.getUser().getRank().getDiscount();
        if (percent == null || percent.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return order.getOrderTotal()
                .multiply(percent)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateItemPromoDiscount(Orders order, Promotion promotion) {
        BigDecimal discount = BigDecimal.ZERO;
        List<PromotionItem> promotionItems = promotionItemRepository.findAllByPromotion(promotion);

        for (OrderItems item : order.getItem()) {
            for (PromotionItem pItem : promotionItems) {
                if (Objects.equals(item.getProduct().getProductId(), pItem.getProduct().getProductId())
                        && item.getOrderItemQuantity() >= 1) {

                    BigDecimal itemTotal = item.getOrderItemPrice()
                            .multiply(BigDecimal.valueOf(item.getOrderItemQuantity()));

                    if (promotion.getDiscountType() == DiscountType.PERCENT) {
                        discount = itemTotal.multiply(promotion.getDiscountValue())
                                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    } else {
                        discount = promotion.getDiscountValue();
                    }

                    if (promotion.getMaxDiscountValue() != null) {
                        discount = discount.min(promotion.getMaxDiscountValue());
                    }
                    return discount;
                }
            }
        }
        return BigDecimal.ZERO;
    }

}
