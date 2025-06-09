package com.example.springcode5.repo;

import com.example.springcode5.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    @Query("SELECT u FROM UserEntity u WHERE u.username = :username")
    UserEntity getUserByName(@Param("username") String username);

    @Query("SELECT u FROM UserEntity u WHERE u.status != 'DELETED'")
    List<UserEntity> findAllActive();

    @Modifying
    @Query("UPDATE UserEntity u SET u.username = :username, u.phone = :phone, " +
            "u.email = :email, u.realName = :realName, u.address = :address " +
            "WHERE u.id = :id")
    int updateUserInfo(@Param("username") String username,
                       @Param("phone") String phone,
                       @Param("email") String email,
                       @Param("realName") String realName,
                       @Param("address") String address,
                       @Param("id") Long id);

    @Modifying
    @Query("UPDATE UserEntity u SET u.currentBorrowCount = :count WHERE u.id = :id")
    void updateCurrentBorrowCount(@Param("id") Long id, @Param("count") Integer count);

    @Modifying
    @Query("UPDATE UserEntity u SET u.status = :status WHERE u.id = :id")
    void updateUserStatus(@Param("id") Long id, @Param("status") UserEntity.UserStatus status);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM UserEntity u WHERE u.usertype = :usertype AND u.status != 'DELETED'")
    List<UserEntity> findByUsertype(@Param("usertype") String usertype);

    @Query("SELECT u FROM UserEntity u WHERE " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.realName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "u.phone LIKE CONCAT('%', :keyword, '%')) AND " +
            "u.status != 'DELETED'")
    List<UserEntity> searchUsers(@Param("keyword") String keyword);
}