package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.TaskDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.Project;
import com.cybertek.entity.User;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.mapper.MapperUtil;
import com.cybertek.mapper.UserMapper;
import com.cybertek.repository.UserRepository;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
//    UserMapper userMapper;
    private ProjectService projectService;
    private TaskService taskService;
    private MapperUtil mapperUtil;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,  @Lazy ProjectService projectService, TaskService taskService,
                           MapperUtil mapperUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.taskService = taskService;
        this.mapperUtil = mapperUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDTO> listAllUsers() {
        List<User> list=userRepository.findAll(Sort.by("firstName"));
        //convert entity to DTO
        return list.stream()
//                .map(obj-> {return userMapper.convertToDTO(obj);})
                .map(obj-> {return mapperUtil.convert(obj, new UserDTO());})

                .collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String userName) {
        User user=userRepository.findByUserName(userName);
//        return userMapper.convertToDTO(user);
        return mapperUtil.convert(user, new UserDTO());

    }

    @Override
    public void save(UserDTO dto) {
//        User user=userMapper.convertToEntity(dto);
        User foundUser = userRepository.findByUserName(dto.getUserName());
        dto.setEnabled(true);


        User user=mapperUtil.convert(dto, new User());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

    }

    @Override
    public UserDTO update(UserDTO dto) {
        //Find current user
        User user = userRepository.findByUserName(dto.getUserName());
        //Map update user dto to entity object
//        User convertedUser= userMapper.convertToEntity(dto);
        User convertedUser= mapperUtil.convert(dto, new User());

        //set id to the converted object
        convertedUser.setId(user.getId());
        convertedUser.setEnabled(true);
        convertedUser.setPassword(passwordEncoder.encode(convertedUser.getPassword()));
        //save updated user
        userRepository.save(convertedUser);
        return findByUserName(dto.getUserName());
    }

    @Override
    public void delete(String username) throws TicketingProjectException {
        User user=userRepository.findByUserName(username);
        if(user == null){
            throw new TicketingProjectException("User does not exists");
        }
        if (!checkIfUserCanBeDeleted(user)){
            throw new TicketingProjectException("User can not be deleted.  It is linked by a project or task");
        }
        user.setUserName("d-"+user.getUserName()+"-"+user.getId());

        user.setIsDeleted(true);
        userRepository.save(user);
    }

    @Override
    public void deleteByUserName(String username) {
        //hard delete
        userRepository.deleteByUserName(username);
    }

    @Override
    public List<UserDTO> listAllByRole(String role) {
        List<User> users=userRepository.findAllByRoleDescriptionIgnoreCase(role);
        return users.stream()
                .map(user->{
//                    return userMapper.convertToDTO(user);
                    return mapperUtil.convert(user, new UserDTO());

                })
                .collect(Collectors.toList());
    }

    @Override
    public Boolean checkIfUserCanBeDeleted(User user) {

        switch (user.getRole().getDescription()){
            case "Manager":
                List<ProjectDTO> projectList= projectService.readAllByAssignedManager(user);
                return projectList.size()==0;
            case "Employee":
                List<TaskDTO> taskList= taskService.readAllByEmployee(user);
                return taskList.size()==0;
            default:// admin
                return true;
        }

//        return null;
    }
}
