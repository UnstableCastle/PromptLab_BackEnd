package com.promptlab.server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.promptlab.server.entity.Follow;
import com.promptlab.server.entity.Follow.FollowStatus;
import com.promptlab.server.entity.User;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(
            User follower,
            User following);

    Optional<Follow> findByFollowerAndFollowing(
            User follower,
            User following);

    @Modifying
    @Query("DELETE FROM Follow f WHERE f.follower = :follower AND f.following = :following")
    void deleteByFollowerAndFollowing(
            @Param("follower") User follower,
            @Param("following") User following);

    long countByFollowerAndFollowStatus(
            User follower,
            FollowStatus status);

    long countByFollowingAndFollowStatus(
            User following,
            FollowStatus status);

    List<Follow> findByFollowerAndFollowStatus(
            User follower,
            FollowStatus status);

    List<Follow> findByFollowingAndFollowStatus(
            User following,
            FollowStatus status);

}