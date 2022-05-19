package com.ziroom.framework.example.controller;

import com.fasterxml.classmate.GenericType;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.ziroom.framework.common.api.pojo.ResponseData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class User {

    @ApiModelProperty
    private String username;

    @ApiModelProperty
    private String password;

    public static void main(String[] args) {
        TypeResolver typeResolver1 = new TypeResolver();

        ResolvedType t1 = typeResolver1.resolve(new GenericType<ResponseData<User>>() {
        });

        ResolvedType t2 = typeResolver1.resolve(ResponseData.class, User.class);
        System.out.println(t1);
        System.out.println(t2);
        System.out.println(t1.equals(t2));
    }
}
