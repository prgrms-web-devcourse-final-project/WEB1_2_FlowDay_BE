package org.example.flowday.domain.post.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.post.comment.dto.ReplyDTO;
import org.example.flowday.domain.post.comment.entity.Reply;
import org.example.flowday.domain.post.comment.exception.ReplyException;
import org.example.flowday.domain.post.comment.exception.ReplyTaskException;
import org.example.flowday.domain.post.comment.repository.ReplyRepository;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.domain.post.post.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public ReplyDTO.createResponse saveReply(ReplyDTO.createRequest request, Long memberId, Long postId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다"));
        Reply parent = null;


        if (request.getParentId() != null) {
            parent = replyRepository.findById(request.getParentId()).orElseThrow(ReplyException.REPLY_NOT_FOUND::getReplyException);
        }

        Reply reply = request.toEntity(member, parent, post);
        Reply saveReply = replyRepository.save(reply);


        return new ReplyDTO.createResponse(saveReply, "댓글이 생성되었습니다");


    }


    @Transactional
    public void removeReply(Long replyId , Long memberId) {
        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyException.REPLY_NOT_FOUND::getReplyException);
        verifyMemberAuthority(memberId, reply);
        if (reply.getParent() != null) {
            //  reply.getParent().getChildren().remove(reply);
            replyRepository.delete(reply);
        } else {
            reply.updateDeleteMsg();
        }

    }

    @Transactional
    public ReplyDTO.updateResponse updateReply(ReplyDTO.updateRequest request, Long replyId, Long memberId) {
        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyException.REPLY_NOT_FOUND::getReplyException);

        verifyMemberAuthority(memberId, reply);

        reply.updateContent(request.getContent());

        return new ReplyDTO.updateResponse("댓글이 수정되었습니다", request.getContent());


    }

    private void verifyMemberAuthority(Long memberId, Reply reply) {
        if (!memberId.equals(reply.getMember().getId())) {
            throw ReplyException.REPLY_AUTHORITED.getReplyException();
        }
    }


    public List<ReplyDTO.Response> findAllByPost(Long postId) {
        postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다"));

        List<Reply> replies = replyRepository.findAllReplies(postId);

        Map<Long, ReplyDTO.Response> replyMap = new HashMap<>();
        List<ReplyDTO.Response> result = new ArrayList<>();

        for (Reply reply : replies) {
            ReplyDTO.Response dto = new ReplyDTO.Response(reply);
            replyMap.put(reply.getId(), dto);

            if (reply.getParent() == null) {
                result.add(dto);
            } else {
                ReplyDTO.Response parentDTO = replyMap.get(reply.getParent().getId());
                if (parentDTO != null) {
                    parentDTO.getChildren().add(dto);
                }

            }
        }

        return result;

    }


}
