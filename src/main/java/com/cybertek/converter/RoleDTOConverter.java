package com.cybertek.converter;

import com.cybertek.dto.RoleDTO;
import com.cybertek.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding //no need to call the convert method. Spring will auto convert to Role
public class RoleDTOConverter implements Converter<String, RoleDTO> {


    @Autowired
    RoleService roleService;

    @Override
    public RoleDTO convert(String source) {
        Long id = Long.parseLong(source);
        return roleService.findById(id);
    }

//    @Override
//    public RoleDTO convert(String source) {
////        RoleDTO role=new RoleDTO();
////        role=roleService.findById(Long.parseLong(source));
////        return role;
//    }
}
