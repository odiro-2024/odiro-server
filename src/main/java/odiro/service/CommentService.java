package odiro.service;

import lombok.RequiredArgsConstructor;
import odiro.domain.*;
import odiro.domain.member.Member;
import odiro.dto.comment.CommentDetailDto;
import odiro.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberService memberService;
    private final DayPlanService dayPlanService;

    public Comment postComment( Long dayPlanId, Long memberId, String content) {

        // DayPlan 검색
        DayPlan dayPlan = dayPlanService.findById(dayPlanId)
                .orElseThrow(() -> new RuntimeException("DayPlan not found"));

        // 멤버 검색
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // Comment 저장
        Comment comment = new Comment(dayPlan, member, content);
        commentRepository.save(comment);

        //comment 반환
        return comment;
    }

    public Comment updateComment(Long commentId, String newContent) {

        //수정할 comment 찾기
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        //comment 수정 후 저장
        comment.setContent(newContent);
        commentRepository.save(comment); // Save updated comment

        //comment 반환
        return comment;
    }


    public void deleteComment(Long commentId) {

        //수정할 comment 찾기
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        //삭제
        commentRepository.delete(comment);
    }
    public Page<CommentDetailDto> getCommentsByDayPlanId(Long dayPlanId, int page) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<Comment> commentPage = commentRepository.findByDayPlanIdOrderByWriteTimeDesc(dayPlanId, pageRequest);

        // Comment 엔티티를 CommentDto로 변환
        List<CommentDetailDto> commentDtos = commentPage.getContent().stream()
                .map(CommentDetailDto::fromEntity)
                .collect(Collectors.toList());

        // Page 객체로 변환하여 반환
        return new PageImpl<>(commentDtos, pageRequest, commentPage.getTotalElements());
    }
}
