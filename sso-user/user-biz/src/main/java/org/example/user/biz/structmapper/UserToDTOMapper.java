package org.example.user.biz.structmapper;

import org.example.user.data.entity.User;
import org.example.user.data.entity.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserToDTOMapper {
    UserDTO from(User user);
}
