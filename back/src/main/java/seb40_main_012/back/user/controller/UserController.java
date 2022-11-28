package seb40_main_012.back.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seb40_main_012.back.book.BookDto;
import seb40_main_012.back.book.BookRepository;
import seb40_main_012.back.book.entity.Book;
import seb40_main_012.back.bookCollection.dto.BookCollectionDto;
import seb40_main_012.back.bookCollection.entity.BookCollection;
import seb40_main_012.back.bookCollection.repository.BookCollectionRepository;
import seb40_main_012.back.common.mypage.MyPageRepositorySupport;
import seb40_main_012.back.common.bookmark.BookmarkRepository;
import seb40_main_012.back.common.bookmark.BookmarkType;
import seb40_main_012.back.common.comment.CommentDto;
import seb40_main_012.back.common.comment.CommentMapper;
import seb40_main_012.back.common.comment.CommentRepository;
import seb40_main_012.back.common.comment.CommentService;
import seb40_main_012.back.common.comment.entity.Comment;
import seb40_main_012.back.config.auth.dto.LoginDto;
import seb40_main_012.back.dto.ListResponseDto;
import seb40_main_012.back.dto.SingleResponseDto;
import seb40_main_012.back.email.EmailSenderService;
import seb40_main_012.back.pairing.PairingDto;
import seb40_main_012.back.pairing.PairingRepository;
import seb40_main_012.back.pairing.entity.Pairing;
import seb40_main_012.back.user.dto.UserDto;
import seb40_main_012.back.user.dto.UserInfoDto;
import seb40_main_012.back.user.entity.User;
import seb40_main_012.back.user.mapper.UserMapper;
import seb40_main_012.back.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;
    private final CommentMapper commentMapper;
    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final PairingRepository pairingRepository;
    private final BookCollectionRepository collectionRepository;
    private final BookRepository bookRepository;
    private final BookmarkRepository bookmarkRepository;
    private final MyPageRepositorySupport collectionRepositorySupport;
    private final EmailSenderService emailSenderService;


    @PostMapping("/users")
    public ResponseEntity postUser(@Valid @RequestBody UserDto.PostDto postDto) {

        User user = mapper.userPostToUser(postDto);
        User createdUser = userService.createUser(user);
        UserDto.ResponseDto response = mapper.userToUserResponse(createdUser);

        return new ResponseEntity<>(
                new SingleResponseDto<>(response), HttpStatus.CREATED);
    }

    @PostMapping("/mypage/verify/nickName")
    public boolean verifyNickName(@Valid @RequestBody UserDto.Profile request) {
        return userService.verifyNickName(request.getNickName());
    }

    @PostMapping("/mypage/verify/email")
    public boolean verifyEmail(@Valid @RequestBody UserDto.EmailDto emailDto) {
        return userService.verifyEmail(emailDto.getEmail());
    }

    @PostMapping("/mypage/password/current")
    @ResponseStatus(HttpStatus.OK)
    public boolean verifyPassword(@RequestBody UserDto.Password currentPassword) {
        return userService.verifyPassword(currentPassword.getPassword());
    }

    @PatchMapping("/mypage/password/update")
    @ResponseStatus(HttpStatus.OK)
    public void patchPassword(@RequestBody UserDto.Password request) {
        userService.updatePassword(request.getPassword());
    }

    @PatchMapping("/mypage/userInfo")
    @ResponseStatus(HttpStatus.OK)
    public UserInfoDto.Response patchUserInfo(@RequestBody UserInfoDto.Post request) {
        User editedUser = userService.editUserInfo(request.toEntity(), request.getCategory());
        userService.updateNickName(request.getNickname());
        return UserInfoDto.Response.of(editedUser);
    }

    @GetMapping("/users/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity getUser(@PathVariable("user_id") @Positive Long userId) {

        User user = userService.findUser();

        UserDto.ResponseDto response = mapper.userToUserResponse(user);

        return new ResponseEntity<>(
                new SingleResponseDto<>(response), HttpStatus.OK
        );
    }

    @DeleteMapping("/mypage")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean deleteUser() {
        return userService.deleteUser();
    }

    @DeleteMapping("/mypage/userCollection/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllUserCollection(){
        userService.deleteAllUserCollection();
    }

    /**
     * 조회 API
     */

    @GetMapping("/mypage/userInfo")
    @ResponseStatus(HttpStatus.OK)
    public UserInfoDto.Response getUserInfo() {
        User findUser = userService.getLoginUser();
        User user = userService.findVerifiedUser(findUser.getUserId());
        return UserInfoDto.Response.of(user);
    }

    @GetMapping("/mypage/userComment")
    public ResponseEntity getUserComment() {
        User findUser = userService.getLoginUser();
        List<Comment> findComments = commentService.findMyCommentAll();
        List<CommentDto.myPageResponse> responses = commentMapper.commentsToMyPageResponse(findComments);
        Long listCount = commentRepository.countByUser(findUser);
        return new ResponseEntity<>(
                new ListResponseDto<>(listCount,responses), HttpStatus.OK);
    }

//    @GetMapping("/mypage/userComment")
//    @ResponseStatus(HttpStatus.OK)
//    public CommentDto.CommentList getUserComment() {
//        List<Comment> comments = userService.getUserComment();
//        List<CommentDto.BookComment> bookComments = new ArrayList<>();
//        List<CommentDto.PairingComment> pairingComments = new ArrayList<>();
//        List<CommentDto.CollectionComment> collectionComments = new ArrayList<>();
//
//        comments.forEach(
//                x -> {
//                    if (x.getCommentType() == CommentType.BOOK) {
//                        bookComments.add(CommentDto.BookComment.of(x));
//                    } else if (x.getCommentType() == CommentType.PAIRING) {
//                        pairingComments.add(CommentDto.PairingComment.of(x));
//                    } else if (x.getCommentType() == CommentType.BOOK_COLLECTION) {
//                        collectionComments.add(CommentDto.CollectionComment.of(x));
//                    }
//                }
//        );
//        Long listCount = commentRepository.countBy();
//        return CommentDto.CommentList.of(bookComments, pairingComments, collectionComments);
//    }

    @GetMapping("/mypage/userPairing")
    @ResponseStatus(HttpStatus.OK)
    public SingleResponseDto getUserPairing(@PathParam("lastId") Long lastId) {
        Slice<Pairing> pairings = userService.getUserPairing(lastId);
        Slice<PairingDto.UserPairing> pairingDto = new SliceImpl<>(pairings.stream().map(x -> PairingDto.UserPairing.of(x)).collect(Collectors.toList()));
        return new SingleResponseDto<>(pairingDto);
    }

    /** 무한스크롤 queryDsl test */
    @GetMapping("/mypage/userCollectionDsl")
    @ResponseStatus(HttpStatus.OK)
    public SingleResponseDto getUserBookCollectionDsl(@PathParam("lastId") Long lastId) {
        Slice<BookCollection> collections = userService.getUserCollectionDsl(lastId);
        Slice<BookCollectionDto.UserCollection> collectionDto = new SliceImpl<>(collections.stream().map(x -> BookCollectionDto.UserCollection.of(x)).collect(Collectors.toList()));
        return new SingleResponseDto<>(collectionDto);
    }

    @GetMapping("/mypage/userCollection")
    @ResponseStatus(HttpStatus.OK)
    public SingleResponseDto getUserBookCollection(@PathParam("lastId") Long lastId) {
        Slice<BookCollection> collections = userService.getUserCollection(lastId);
        Slice<BookCollectionDto.UserCollection> collectionDto = new SliceImpl<>(collections.stream().map(x -> BookCollectionDto.UserCollection.of(x)).collect(Collectors.toList()));
        return new SingleResponseDto(collectionDto);
    }


    @GetMapping("/mypage/bookmark/collection")
    @ResponseStatus(HttpStatus.OK)
    public SingleResponseDto getBookmarkByBookCollection(@PathParam("lastId") Long lastId) {
        Slice<BookCollection> collections = userService.getBookmarkByBookCollection(lastId);
        Slice<BookCollectionDto.BookmarkedCollection> bookmarkedCollectionDto = new SliceImpl<>(collections.stream().map(x -> BookCollectionDto.BookmarkedCollection.of(x)).collect(Collectors.toList()));
        return new SingleResponseDto<>(bookmarkedCollectionDto);
    }

    @GetMapping("/mypage/bookmark/pairing")
    @ResponseStatus(HttpStatus.OK)
    public SingleResponseDto getBookMarkByPairing(@PathParam("lastId") Long lastId) {
        Slice<Pairing> pairings = userService.getBookmarkByPairing(lastId);
        Slice<PairingDto.BookmarkedPairing> pairingDto = new SliceImpl<>(pairings.stream().map(x -> PairingDto.BookmarkedPairing.of(x)).collect(Collectors.toList()));
        return new SingleResponseDto<>(pairingDto);
    }

    @GetMapping("/mypage/bookmark/book")
    @ResponseStatus(HttpStatus.OK)
    public SingleResponseDto getBookMarkByBook(@PathParam("lastId") Long lastId) {
        Slice<Book> books = userService.getBookmarkByBook(lastId);
        Slice<BookDto.BookmarkedBook> bookDto = new SliceImpl<>(books.stream().map(x -> BookDto.BookmarkedBook.of(x)).collect(Collectors.toList()));
        return new SingleResponseDto<>(bookDto);
    }


    @PatchMapping("/users/firstLogin")
    public ResponseEntity patchUserOnFirstLogin(@Valid @RequestBody LoginDto.PatchDto patchDto) {
        User user = userService.updateOnFirstLogin(patchDto);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.userToFirstLoginResponse(user)), HttpStatus.OK);
    }

}
