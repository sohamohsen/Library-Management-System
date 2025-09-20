package com.soha.librarymanagement.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSearchCriteria {

    private String q;
    private Boolean active;
}
