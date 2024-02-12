package edu.nazaaaar.librarybackend.dao;

import edu.nazaaaar.librarybackend.entity.BookEntity;
import edu.nazaaaar.librarybackend.entity.UserBookReading;
import edu.nazaaaar.librarybackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBookReadingRepository extends JpaRepository <UserBookReading, Long> {
    Optional<UserBookReading> findByUserAndBook(UserEntity user, BookEntity book);
    List<UserBookReading> findByUser(UserEntity user);
}
