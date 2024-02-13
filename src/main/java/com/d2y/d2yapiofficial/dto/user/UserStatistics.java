package com.d2y.d2yapiofficial.dto.user;

import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonApiTypeForClass("User")
public class UserStatistics {
    @Id
    private Long id;
    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;
    private long enabledUsers;
    private long disabledUsers;
}

