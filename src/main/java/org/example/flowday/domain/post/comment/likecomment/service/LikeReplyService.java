package org.example.flowday.domain.post.comment.likecomment.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.post.comment.comment.entity.Reply;
import org.example.flowday.domain.post.comment.comment.exception.ReplyException;
import org.example.flowday.domain.post.comment.comment.repository.ReplyRepository;
import org.example.flowday.domain.post.comment.likecomment.entity.LikeReply;
import org.example.flowday.domain.post.comment.likecomment.repository.LikeReplyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeReplyService {
    private final LikeReplyRepository likeReplyRepository;
    private final MemberRepository memberRepository;
    private final ReplyRepository replyRepository;


    public void saveLikeReply(Long memberId ,Long replyId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당 멤버가 없습니다"));
        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyException.REPLY_NOT_FOUND::getReplyTaskException);
        Optional<LikeReply> like = likeReplyRepository.findByReplyAndMember(reply, member);

        if (like.isPresent()) {
            throw ReplyException.REPLY_IS_LIKE.getReplyTaskException();
        }

        LikeReply likeReply = LikeReply.builder()
                .member(member)
                .reply(reply)
                .build();
        reply.increaseLikeCount();

        likeReplyRepository.save(likeReply);


    }

    public void removeLikeReply(Long memberId ,Long replyId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당 멤버가 없습니다"));
        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyException.REPLY_NOT_FOUND::getReplyTaskException);
        LikeReply like = likeReplyRepository.findByReplyAndMember(reply, member).orElseThrow(() -> new IllegalArgumentException("좋아요가 존재하지 않습니다"));

        reply.decreaseLikeCount();
        likeReplyRepository.delete(like);

    }





}
