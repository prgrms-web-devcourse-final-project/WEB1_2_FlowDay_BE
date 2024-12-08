package org.example.flowday.domain.notification.repository;

import jakarta.transaction.Transactional;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id")
    void updateIsRead(@Param("id") Long id);

    @Query("SELECT n FROM Notification n WHERE n.receiverId = :receiverId ORDER BY n.createdAt DESC")
    Page<Notification> findByReceiverId(Member receiverId, Pageable pageable);

}
