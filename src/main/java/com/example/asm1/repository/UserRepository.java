package com.example.asm1.repository;

// Nhớ import class User (Entity) của em vào đây, bỏ import RegisterForm đi
import com.example.asm1.Entity.User; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
// Sửa RegisterForm -> User
public interface UserRepository extends JpaRepository<User, Long> {

    // Sửa kiểu trả về: RegisterForm -> User
    User findByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Users SET password = :newPassword WHERE email = :email", nativeQuery = true)
    void updatePassword(@Param("email") String email, @Param("newPassword") String newPassword);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Users SET first_name = :fname, surname = :sname, shopping_preference = :pref WHERE email = :email", nativeQuery = true)
    void updateProfile(@Param("fname") String fname, @Param("sname") String sname, @Param("pref") String pref, @Param("email") String email);

    
}