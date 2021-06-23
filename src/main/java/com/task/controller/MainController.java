package com.task.controller;

import com.task.model.Role;
import com.task.model.User;
import com.task.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Controller
public class MainController {
    private static Set<Role> roles;
    static {
        roles = new HashSet<>();
        roles.add(Role.USER);
        roles.add(Role.ADMIN);
    }
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String getAllUsers(Model model){
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "main";
    }

    @GetMapping("/{id}")
    public String getUser(@PathVariable long id, Model model){
        User user = userRepository.findById(id).orElse(new User());
        model.addAttribute("user", user);
        model.addAttribute("roleAdmin", Role.ADMIN);
        model.addAttribute("roleUser", Role.USER);
        model.addAttribute("userRoles", roles);
        model.addAttribute("id", id);
        return "userPage";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable long id,
                             Model model,
                             @ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "userPage";
        }
        userRepository.save(user);
        model.addAttribute("user", user);
        model.addAttribute("userRoles", user.getRoles());
        model.addAttribute("id", id);
        return "userPage";
    }

    @PostMapping("/{id}/removeRole")
    public String removeRole(@PathVariable long id, @RequestParam("role")String role){
        User user = userRepository.findById(id).orElse(new User());
        if (user.getId() == 0L){
            return "redirect:/";
        }
        if (role.equals(Role.ADMIN.toString())) {
            user.getRoles().remove(Role.ADMIN);
        }
        if (role.equals(Role.USER.toString())){
            user.getRoles().remove(Role.USER);
        }
        userRepository.save(user);
        return "redirect:/"+id;
    }

    @PostMapping("/{id}/addRole")
    public String addRole(@PathVariable long id, @RequestParam("role") String role){
        User user = userRepository.findById(id).orElse(new User());
        if (user.getId() == 0L){
            return "redirect:/";
        }
        if (role.equals(Role.ADMIN.toString())) {
            user.getRoles().add(Role.ADMIN);
        }
        if (role.equals(Role.USER.toString())){
            user.getRoles().add(Role.USER);
        }
        userRepository.save(user);
        return "redirect:/"+id;
    }

    @GetMapping("/new")
    public String newUser(@ModelAttribute User user){
        return "newUser";
    }

    @PostMapping("/new")
    public String addNewUser(@ModelAttribute @Valid User user, BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return "newUser";
        userRepository.save(user);
        return "redirect:/";
    }
}
