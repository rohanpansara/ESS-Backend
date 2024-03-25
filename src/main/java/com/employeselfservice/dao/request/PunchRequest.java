package com.employeselfservice.dao.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PunchRequest {

    private Long employeeId;
    private String punchType;

}
