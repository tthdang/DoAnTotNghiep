package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.entity.Products;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Products, Integer> {
    Optional<Products> findByProductId(Integer productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Products p WHERE p.id = :id")
    Products findByIdForUpdate(@Param("id") Integer id);

    //get top 5 san pham noi bat
//    @Query("""
//    SELECT p.productName, p.productSold as sold, p.productImage
//    FROM Products p
//    GROUP BY p.productName, p.productImage
//    ORDER BY sold DESC
//    LIMIT 5""")
//    List<Object[]> getTop5Products(PageRequest pageRequest);

    List<Products> findTop5ByOrderByProductSoldDesc();

    //lấy combo theo danh mục
    @Query(value = """
                SELECT * FROM products p
                JOIN categories c ON p.category_id = c.category_id
                WHERE c.category_name = :categoryName
                ORDER BY RAND()
                LIMIT 1
            """, nativeQuery = true)
    Products findRandomByCategory(@Param("categoryName") String categoryName);

    //xử lý combo sắp hết hạn
    @Query("""
            SELECT DISTINCT p FROM Products p
            JOIN p.recipes r
            WHERE r.ingredient.ingredientId IN :ids
            """)
    List<Products> findByIngredientIds(@Param("ids") List<Integer> ids);
}
