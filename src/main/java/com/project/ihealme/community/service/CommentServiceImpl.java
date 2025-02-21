package com.project.ihealme.community.service;

import com.project.ihealme.community.domain.Comment;
import com.project.ihealme.community.domain.Criteria;
import com.project.ihealme.community.domain.Post;
import com.project.ihealme.community.dto.CommentDto;
import com.project.ihealme.community.dto.CommentPageDto;
import com.project.ihealme.community.repository.CommentRepository;
import com.project.ihealme.community.repository.PostRepository;
import com.project.ihealme.user.entity.User;
import com.project.ihealme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Long save(CommentDto commentDto) {
        Post post = postRepository.findByPostNo(commentDto.getPostNo())
                .orElseThrow(()-> new IllegalArgumentException("해당 게시글이 없습니다."));

//        user 완료되면 교체
//        User user = userRepository.findByEmail(commentDto.getEmail());
        User user = userRepository.findById(1L)
                .orElseThrow(()-> new IllegalArgumentException("해당 유저가 없습니다."));

        Comment comment = Comment.builder()
                .commNo(commentDto.getCommNo())
                .content(commentDto.getContent())
                .user(user)
                .post(post)
                .build();

        commentRepository.save(comment);

        return comment.getCommNo();
    }

    @Override
    @Transactional
    public List<CommentDto> getList(Long postNo) {
        Post post = postRepository.findByPostNo(postNo)
                .orElseThrow(()-> new IllegalArgumentException("해당 게시글이 없습니다."));
        List<Comment> comments = commentRepository
                .findByPost(post);

        return comments.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentPageDto getListPage(Criteria criteria, Long postNo){
        Post post = postRepository.findByPostNo(postNo)
                .orElseThrow(()-> new IllegalArgumentException("해당 게시글이 없습니다."));
        return CommentPageDto.createCommentPageDto(
                commentRepository.countByPostNo(postNo),
                commentRepository.getListWithPaging(criteria.getAmount(), criteria.getPageNum(), post));
    }

    @Override
    @Transactional
    public void update(CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentDto.getCommNo())
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));

        comment.update(commentDto.getContent());
    }

    @Override
    @Transactional
    public void delete(Long commNo) {
        commentRepository.deleteById(commNo);
    }

    /*
    private void validateComment(Comment comment, User user) {
        if (!comment.isOwnComment(user)) {
            throw new MemberNotEqualsException();
        }
    }
*/
}
