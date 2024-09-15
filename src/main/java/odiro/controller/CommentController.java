package odiro.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import odiro.config.auth.PrincipalDetails;
import odiro.domain.Comment;
import odiro.dto.comment.CommentDetailDto;
import odiro.dto.comment.CommentRequest;
import odiro.dto.comment.CommentResponse;
import odiro.dto.comment.EditCommentRequest;
import odiro.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment/create")
    public ResponseEntity<CommentResponse> writeComment(@RequestBody CommentRequest request, @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Comment savedComment = commentService.postComment(request.getDayPlanId(),principalDetails.getMember().getId(), request.getContent());

        CommentResponse response = new CommentResponse(savedComment.getId(), savedComment.getWriteTime());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/comment/edit")
    public ResponseEntity<CommentResponse> updateComment(@RequestBody EditCommentRequest request) {

        Comment updatedComment = commentService.updateComment(request.getId(), request.getContent());

        CommentResponse response = new CommentResponse(updatedComment.getId(), updatedComment.getWriteTime());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId) {

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/comment/list")
    public ResponseEntity<Page<CommentDetailDto>> getComments(
            @RequestParam Long dayPlanId,
            @RequestParam(defaultValue = "0") int page) {

        page = (page > 0) ? page - 1 : 0;

        // CommentService를 이용하여 페이지 처리된 CommentDto 리스트를 가져옴
        Page<CommentDetailDto> comments = commentService.getCommentsByDayPlanId(dayPlanId, page);

        // 반환
        return ResponseEntity.ok(comments);
    }
}
