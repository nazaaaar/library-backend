package edu.nazaaaar.librarybackend.dao;

import edu.nazaaaar.librarybackend.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Book;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    List<BookEntity> findTop3ByGenreOrderByRatingDesc(String genre);
}
