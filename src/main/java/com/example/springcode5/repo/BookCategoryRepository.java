package com.example.springcode5.repo;

import com.example.springcode5.domain.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {

    Optional<BookCategory> findByCategoryCode(String categoryCode);

    Optional<BookCategory> findByCategoryName(String categoryName);

    @Query("SELECT c FROM BookCategory c WHERE c.categoryName LIKE %:keyword% OR c.categoryCode LIKE %:keyword%")
    List<BookCategory> searchByKeyword(@Param("keyword") String keyword);

    boolean existsByCategoryCode(String categoryCode);
}
