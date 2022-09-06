package org.example.user.data.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@TableName("t_user")
public class User implements Serializable {
    private static final long serialVersionUID = -1L;
    private int id;
    private String name;
    private String pwd;
    private String salt;
}
