package edu.nazaaaar.librarybackend.controller;

import edu.nazaaaar.librarybackend.dao.BookRepository;
import edu.nazaaaar.librarybackend.dao.UserBookReadingRepository;
import edu.nazaaaar.librarybackend.dao.UserRepository;
import edu.nazaaaar.librarybackend.entity.BookEntity;
import edu.nazaaaar.librarybackend.entity.UserBookReading;
import edu.nazaaaar.librarybackend.entity.UserEntity;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final UserBookReadingRepository userBookReadingRepository;

    @Autowired
    public UserController(UserRepository userRepository,
                          BookRepository bookRepository,
                          UserBookReadingRepository userBookReadingRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.userBookReadingRepository = userBookReadingRepository;
    }

    @GetMapping("/{userId}/books/read")
    public ResponseEntity<List<BookEntity>> getReadBooksByUser(@PathVariable Long userId) {
        List<BookEntity> readBooks = new ArrayList<>();

        Optional<UserEntity> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            Set<UserBookReading> userBookReadings = user.getUserBookReadings();

            for (UserBookReading userBookReading : userBookReadings) {
                readBooks.add(userBookReading.getBook());
            }

            if (readBooks.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(readBooks, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{userId}/books/{bookId}/read")
    public ResponseEntity<String> markBookAsReadByUser(@PathVariable Long userId, @PathVariable Long bookId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        Optional<BookEntity> bookOptional = bookRepository.findById(bookId);

        if (userOptional.isPresent() && bookOptional.isPresent()) {
            UserEntity user = userOptional.get();
            BookEntity book = bookOptional.get();

            // Check if the book is already marked as read by the user
            if (userHasReadBook(user, book)) {
                return new ResponseEntity<>("Book is already marked as read by the user.", HttpStatus.BAD_REQUEST);
            }

            UserBookReading userBookReading = new UserBookReading();

            userBookReading.setBook(book);
            userBookReading.setUser(user);

            userBookReadingRepository.save(userBookReading);
            return new ResponseEntity<>("Book marked as read by the user.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User or book not found.", HttpStatus.NOT_FOUND);
        }
    }

    private boolean userHasReadBook(UserEntity user, BookEntity book) {
        Optional<UserBookReading> userBookReadings = userBookReadingRepository.findByUserAndBook(user, book);
        return userBookReadings.isPresent();
    }

    @DeleteMapping("/{userId}/books/{bookId}/read")
    public ResponseEntity<String> markBookAsUnreadByUser(@PathVariable Long userId, @PathVariable Long bookId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        Optional<BookEntity> bookOptional = bookRepository.findById(bookId);

        if (userOptional.isPresent() && bookOptional.isPresent()) {
            UserEntity user = userOptional.get();
            BookEntity book = bookOptional.get();

            // Find the UserBookReading entry for the given user and book
            Optional<UserBookReading> userBookReadingOptional = userBookReadingRepository.findByUserAndBook(user, book);

            if (userBookReadingOptional.isPresent()) {
                // Delete the UserBookReading entry
                userBookReadingRepository.delete(userBookReadingOptional.get());
                return new ResponseEntity<>("Book marked as unread by the user.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User has not marked this book as read.", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("User or book not found.", HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/{userId}/favorite-genres")
    public ResponseEntity<List<String>> getUserFavoriteGenres(@PathVariable Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            List<UserBookReading> userBookReadings = userBookReadingRepository.findByUser(user);
            Map<String, Integer> genreCount = new HashMap<>();

            // Count occurrences of each genre
            for (UserBookReading userBookReading : userBookReadings) {
                String genre = userBookReading.getBook().getGenre();
                genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1);
            }

            // Sort genres by occurrence count in descending order
            List<String> favoriteGenres = genreCount.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .map(Map.Entry::getKey)
                    .limit(2) // Get top 2 favorite genres
                    .collect(Collectors.toList());

            return new ResponseEntity<>(favoriteGenres, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{userId}/book-recommendations")
    public ResponseEntity<Map<String, List<BookEntity>>> getBookRecommendations(@PathVariable Long userId) {
        // Get user's favorite genres
        ResponseEntity<List<String>> responseEntity = getUserFavoriteGenres(userId);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            List<String> favoriteGenres = responseEntity.getBody();

            // Fetch recommendations for each favorite genre
            Map<String, List<BookEntity>> recommendations = new HashMap<>();
            for (String genre : favoriteGenres) {
                List<BookEntity> genreRecommendations = bookRepository.findTop3ByGenreOrderByRatingDesc(genre); // Assuming you have a method in your BookRepository to fetch top 3 books by genre
                recommendations.put(genre, genreRecommendations);
            }

            return new ResponseEntity<>(recommendations, HttpStatus.OK);
        } else {
            return ResponseEntity.status(responseEntity.getStatusCode()).build();
        }
    }


}
