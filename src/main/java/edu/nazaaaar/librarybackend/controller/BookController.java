package edu.nazaaaar.librarybackend.controller;

import edu.nazaaaar.librarybackend.dao.BookDto;
import edu.nazaaaar.librarybackend.dao.BookRepository;
import edu.nazaaaar.librarybackend.entity.BookEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookRepository bookRepository;

    @Autowired
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public ResponseEntity<List<BookEntity>> getAllBooks(){
        List<BookEntity> allBooks = bookRepository.findAll();
        if (allBooks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else return new ResponseEntity<>(allBooks, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> addBook(@RequestBody BookDto bookDto){

        System.out.println(bookDto.getAuthor());

        BookEntity bookEntity = new BookEntity();
        bookEntity.setAuthor(bookDto.getAuthor());
        bookEntity.setYear(bookDto.getYear());
        bookEntity.setGenre(bookDto.getGenre());
        bookEntity.setTitle(bookDto.getTitle());
        bookEntity.setRating(bookDto.getRating());

        bookRepository.save(bookEntity);

        return new ResponseEntity<>("Book added success!", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return new ResponseEntity<>("Book deleted successfully!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Book not found!", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateBook(@PathVariable Long id, @RequestBody BookDto bookDto) {
        if (bookRepository.existsById(id)) {
            BookEntity bookEntity = bookRepository.findById(id).orElseThrow(); // Assuming book exists, otherwise handle as required
            bookEntity.setAuthor(bookDto.getAuthor());
            bookEntity.setYear(bookDto.getYear());
            bookEntity.setGenre(bookDto.getGenre());
            bookEntity.setTitle(bookDto.getTitle());
            bookEntity.setRating(bookDto.getRating());
            bookRepository.save(bookEntity);
            return new ResponseEntity<>("Book updated successfully!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Book not found!", HttpStatus.NOT_FOUND);
        }
    }



}
