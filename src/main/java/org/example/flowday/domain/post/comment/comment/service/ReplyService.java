package org.example.flowday.domain.post.comment.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.post.comment.comment.dto.ReplyDTO;
import org.example.flowday.domain.post.comment.comment.entity.Reply;
import org.example.flowday.domain.post.comment.comment.exception.ReplyException;
import org.example.flowday.domain.post.comment.comment.repository.ReplyRepository;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.domain.post.post.exception.PostException;
import org.example.flowday.domain.post.post.repository.PostRepository;
import org.example.flowday.global.fileupload.service.GenFileService;
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
    private final GenFileService genFileService;

    @Transactional
    public ReplyDTO.createResponse saveReply(ReplyDTO.createRequest request, Long memberId, Long postId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다"));
        Reply parent = null;


        if (request.getParentId() != null) {
            parent = replyRepository.findById(request.getParentId()).orElseThrow(ReplyException.REPLY_NOT_FOUND::getReplyTaskException);
        }

        Reply reply = request.toEntity(member, parent, post);
        Reply saveReply = replyRepository.save(reply);
        post.increaseComment();


        return new ReplyDTO.createResponse(saveReply, "댓글이 생성되었습니다");


    }


    @Transactional
    public ReplyDTO.deleteResponse removeReply(Long replyId , Long memberId) {
        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyException.REPLY_NOT_FOUND::getReplyTaskException);
        Post post = postRepository.findById(reply.getPost().getId()).orElseThrow(PostException.POST_NOT_FOUND::get);
        verifyMemberAuthority(memberId, reply);
        ReplyDTO.deleteResponse deleteResponse;
        if (reply.getParent() != null) {
            reply.removeParent(reply);
            reply.removePost(reply);

            replyRepository.delete(reply);
            post.decreaseComment();
            deleteResponse = new ReplyDTO.deleteResponse("자식 댓글 삭제 완료", "댓글이 삭제되었습니다");
        } else {
            reply.updateDeleteMsg();
            deleteResponse = new ReplyDTO.deleteResponse("부모 댓글 삭제 완료", "작성자에 의해 댓글이 삭제되었습니다");
        }
        return deleteResponse;

    }

    @Transactional
    public ReplyDTO.updateResponse updateReply(ReplyDTO.updateRequest request, Long replyId, Long memberId) {
        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyException.REPLY_NOT_FOUND::getReplyTaskException);

        verifyMemberAuthority(memberId, reply);

        reply.updateContent(request.getContent());

        return new ReplyDTO.updateResponse("댓글이 수정되었습니다", request.getContent());


    }

    private void verifyMemberAuthority(Long memberId, Reply reply) {
        if (!memberId.equals(reply.getMember().getId())) {
            throw ReplyException.REPLY_AUTHORITED.getReplyTaskException();
        }
    }


    public List<ReplyDTO.Response> findAllByPost(Long postId) {
        postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다"));

        List<Reply> replies = replyRepository.findAllReplies(postId);

        Map<Long, ReplyDTO.Response> replyMap = new HashMap<>();
        List<ReplyDTO.Response> result = new ArrayList<>();

        for (Reply reply : replies) {
            String profileImg = genFileService.getFirstImageUrlByObject("member", reply.getMember().getId());
            ReplyDTO.Response dto = new ReplyDTO.Response(reply,profileImg);
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
