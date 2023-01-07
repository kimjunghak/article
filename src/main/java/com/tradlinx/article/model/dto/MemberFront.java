package com.tradlinx.article.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberFront {

    private String userid;

    private String username;

    private int point;

    private LocalDateTime lastLoggedAt;
}
