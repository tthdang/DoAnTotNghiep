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

        BigDecimal rankDiscount = applyRankDiscount(order);
        BigDecimal afterRank = total.subtract(rankDiscount);

        if (promotion.getMinOrderValue() != null && afterRank.compareTo(promotion.getMinOrderValue()) < 0) {
            throw new IllegalArgumentException("Hoá đơn không đủ điều kiện!");
        }

        BigDecimal discount;

        if (promotion.getDiscountType() == DiscountType.PERCENT) {
            //xu ly %
            discount = afterRank.multiply(promotion.getDiscountValue())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        } else {
            discount = promotion.getDiscountValue();
        }
        //lay tien giam gia tối đa
        if (promotion.getMaxDiscountValue() != null) {
            discount = discount.min(promotion.getMaxDiscountValue());
        }


        BigDecimal totalDiscount = rankDiscount.add(discount);

        if (totalDiscount.compareTo(total) > 0) {
            totalDiscount = total;
        }

        order.setDiscountAmount(totalDiscount);
        order.setFinalAmount(total.subtract(totalDiscount));
        order.setPromotion(promotion);
        order.setUserRankDiscount(rankDiscount);

        promotion.setUsedCount(promotion.getUsedCount() + 1);

        if (promotion.getUsedCount() >= promotion.getUsageLimit()) {
            promotion.setStatus(PromotionStatus.OUT_OF_STOCK);
        }
        promotionRepository.save(promotion);

        orderRepository.save(order);
    }

    public void applyItemPromotion(Orders order, Promotion promotion) {

        BigDecimal total = order.getOrderTotal();

        BigDecimal rankDiscount = applyRankDiscount(order);
        BigDecimal afterRank = total.subtract(rankDiscount);

        BigDecimal discount = BigDecimal.ZERO;

        if (promotion.getMinOrderValue() != null && afterRank.compareTo(promotion.getMinOrderValue()) < 0) {
            throw new IllegalArgumentException("Hoá đơn không đủ điều kiện!");
        }

        List<OrderItems> orderItemsList = order.getItem();

        if (orderItemsList.isEmpty()) {
            throw new IllegalArgumentException("Order không có món ăn nào!");
        }

        PromotionItem promotionItem = promotionItemRepository.findByPromotion(promotion).orElseThrow(
                () -> new IllegalArgumentException("Mã khuyến mãi không áp dụng cho món ăn nào")
        );

        List<PromotionItem> promotionItems = promotionItemRepository.findAllByPromotion(promotion);

        boolean applied = false;

        for (OrderItems item : orderItemsList) {

            for (PromotionItem pItem : promotionItems) {

                if (Objects.equals(item.getProduct().getProductId(), pItem.getProduct().getProductId())
                        && item.getOrderItemQuantity() >= 1) {

                    BigDecimal itemPrice = item.getOrderItemPrice();

                    if (promotion.getDiscountType() == DiscountType.PERCENT) {
                        discount = itemPrice.multiply(promotion.getDiscountValue())
                                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    } else {
                        discount = promotion.getDiscountValue();
                    }

                    if (promotion.getMaxDiscountValue() != null) {
                        discount = discount.min(promotion.getMaxDiscountValue());
                    }

                    applied = true;
                    break; // chỉ giảm 1 món
                }
            }

            if (applied) break;
        }

        if (!applied) {
            throw new IllegalArgumentException("Không có món nào đủ điều kiện áp dụng khuyến mãi!");
        }

        BigDecimal totalDiscount = rankDiscount.add(discount);

        if (totalDiscount.compareTo(total) > 0){
            totalDiscount = total;
        }

        // apply vào order
        order.setDiscountAmount(totalDiscount);
        order.setFinalAmount(total.subtract(totalDiscount));
        order.setUserRankDiscount(rankDiscount);
        order.setPromotion(promotion);

        // update promotion
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
}
