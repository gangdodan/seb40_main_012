package seb40_main_012.back.bookCollection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import seb40_main_012.back.book.BookDto;
import seb40_main_012.back.book.bookInfoSearchAPI.BookInfoSearchDto;
import seb40_main_012.back.bookCollection.entity.BookCollection;
import seb40_main_012.back.bookCollection.entity.BookCollectionTag;
import seb40_main_012.back.pairing.PairingDto;
import seb40_main_012.back.pairing.entity.Pairing;

import java.awt.print.Book;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BookCollectionDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post{
        private String title;
        private String content;
        private List<String> tags;
        private List<String> bookIsbns;

        public BookCollection toEntity(){
            BookCollection collection = new BookCollection(
                    title,content,bookIsbns
            );
            return collection;
        }
    }
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response{
        private String title;
        private String content;
        private LocalDate createdAt;
        private LocalDate lastModifiedAt;
        private Long likeCount;
        private boolean userLike;
        private boolean userBookmark;
        private String collectionAuthor;
        private List<String> tags;
        private List<String> isbns;

        public static Response of(BookCollection collection){
            return Response.builder()
                    .title(collection.getTitle())
                    .content(collection.getContent())
                    .createdAt(LocalDate.now())
                    .lastModifiedAt(collection.getLastModifiedAt())
                    .likeCount(collection.getLikeCount())
                    .userLike(!collection.getUser().getCollectionLikes().isEmpty())
                    .userBookmark(!collection.getUser().getCollectionBookmarks().isEmpty())
                    .collectionAuthor(collection.getUser().getNickName())
                    .tags(collection.getCollectionTags().stream().map(x -> x.getTag().getTagName()).collect(Collectors.toList()))
                    .isbns(collection.getBookIsbn13())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CollectionDetails{
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private LocalDate lastModifiedAt;
        private Long likeCount;
        private Long view;
        private boolean userLike;
        private boolean userBookmark;
        private String collectionAuthor;
        private List<String> tags;
        private List<BookInfoSearchDto.CollectionBook> books;


        //collection book
        public static CollectionDetails of(BookCollection collection,List<BookInfoSearchDto.CollectionBook> books ){
            return CollectionDetails.builder()
                    .title(collection.getTitle())
                    .content(collection.getContent())
                    .createdAt(collection.getCreatedAt())
                    .lastModifiedAt(collection.getLastModifiedAt())
                    .likeCount(collection.getLikeCount())
                    .view(collection.getView())
                    .userLike(!collection.getUser().getCollectionLikes().isEmpty())
                    .userBookmark(!collection.getUser().getCollectionBookmarks().isEmpty())
                    .collectionAuthor(collection.getUser().getNickName())
                    .tags(collection.getCollectionTags().stream().map(x -> x.getTag().getTagName()).collect(Collectors.toList()))
                    .books(books)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserCollection{
        private String content;
        private String title;
        private Long collectionLike;

        public static BookCollectionDto.UserCollection of(BookCollection collection){
            return BookCollectionDto.UserCollection.builder()
                    .content(collection.getContent())
                    .title(collection.getTitle())
                    .collectionLike(collection.getCollectionLikes().stream().count())
                    .build();
        }
    }
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookmarkedCollection{
        private String title;
        private String content;
        private String userName;
        private Long collectionLike;
//        private Image bookCover;

        public static BookCollectionDto.BookmarkedCollection of(BookCollection collection){
            return BookCollectionDto.BookmarkedCollection.builder()
                    .title(collection.getTitle())
                    .content(collection.getContent())
                    .userName(collection.getUser().getNickName())
                    .collectionLike(collection.getCollectionLikes().stream().count())
                    .build();
        }
    }
//    @Getter
//    @Builder
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class CategoryCollection{
//        private String cover;
//        private String title;
//        private String author;
//
//        public static BookCollectionDto.CategoryCollection of(BookCollection collection){
//            return BookCollectionDto.CategoryCollection.builder()
////                    .cover(collection.getIsbn13().)
//                    .title(collection.getTitle())
//                    .build();
//        }
//    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TagCollection{
        private List<BookDto.CollectionBook> books;
        private String title;
        private String author;

        public static BookCollectionDto.TagCollection of(BookCollection collection){
            return TagCollection.builder()
                    .books(collection.getCollectionBooks().stream().map(x -> BookDto.CollectionBook.of(x.getBook())).collect(Collectors.toList()))
                    .title(collection.getTitle())
                    .author(collection.getUser().getNickName())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthorCollection{
        private String title;
        private List<BookDto.CollectionBook> books;


        //collection book
        public static AuthorCollection of(BookCollection collection){
            return AuthorCollection.builder()
                    .title(collection.getTitle())
                    .books(collection.getCollectionBooks().stream().map(x -> BookDto.CollectionBook.of(x.getBook())).collect(Collectors.toList()))
                    .build();
        }
    }

}
