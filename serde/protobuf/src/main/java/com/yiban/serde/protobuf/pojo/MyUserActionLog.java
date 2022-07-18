package com.yiban.serde.protobuf.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyUserActionLog implements Serializable {
    private static final long serialVersionUID = 830023071885933675L;
    private String userName;
    private String actionType;
    private String ipAddress;
    private int gender;
    private String provience;

}
