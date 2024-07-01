package odiro.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import odiro.domain.Comment;
import odiro.domain.Plan;
import odiro.dto.*;
import odiro.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/plan/{dayPlanId}/comments/create")
    public ResponseEntity<CommentResponse> writeComment(@PathVariable("dayPlanId") Long dayPlanId, @RequestBody CommentRequest request) {

        Comment savedComment = commentService.postComment(dayPlanId, request.getMemberId(), request.getContent());

        CommentResponse response = new CommentResponse(savedComment.getId(), savedComment.getWriteTime());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable("commentId") Long commentId, @RequestBody CommentRequest request) {
        Comment updatedComment = commentService.updateComment(commentId, request.getContent());
        CommentResponse response = new CommentResponse(updatedComment.getId(), updatedComment.getWriteTime());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

}
