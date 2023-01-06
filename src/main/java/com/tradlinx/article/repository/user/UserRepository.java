package com.tradlinx.article.repository.user;

import com.tradlinx.article.model.entity.User;
import com.tradlinx.article.repository.BaseRepository;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Long> {

    Optional<User> findByUserid(String userid);

    Optional<User> findByUsername(String username);
}
