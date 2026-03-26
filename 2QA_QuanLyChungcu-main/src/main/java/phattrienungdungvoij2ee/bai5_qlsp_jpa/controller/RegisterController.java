package phattrienungdungvoij2ee.bai5_qlsp_jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.Account;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.ChungCu;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.Role;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.repository.AccountRepository;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.repository.ChungCuRepository;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.repository.RoleRepository;

import java.util.Optional;

@Controller
public class RegisterController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ChungCuRepository chungCuRepository;

    @PostMapping("/register")
    public String register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("role") String roleName,
            @RequestParam(value = "chungCuId", required = false) Long chungCuId,
            @RequestParam(value = "room", required = false) String room,
            RedirectAttributes redirectAttributes
    ) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("registerError", "Password does not match!");
            redirectAttributes.addFlashAttribute("openRegister", true);
            return "redirect:/login";
        }

        Optional<Account> existing = accountRepository.findByLoginName(username);
        if (existing.isPresent()) {
            redirectAttributes.addFlashAttribute("registerError", "Username already exists!");
            redirectAttributes.addFlashAttribute("openRegister", true);
            return "redirect:/login";
        }

        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        if (!roleOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("registerError", "Role not found!");
            redirectAttributes.addFlashAttribute("openRegister", true);
            return "redirect:/login";
        }

        try {
            Account account = new Account();
            account.setLogin_name(username);
            account.setPassword(passwordEncoder.encode(password));
            account.getRoles().add(roleOpt.get());

            // Neu la USER va co chung cu ID -> lien ket voi chung cu
            if ("ROLE_USER".equals(roleName) && chungCuId != null) {
                Optional<ChungCu> chungCuOpt = chungCuRepository.findById(chungCuId);
                if (chungCuOpt.isPresent()) {
                    account.setChungCu(chungCuOpt.get());
                    if (room != null && !room.trim().isEmpty()) {
                        account.setRoom(room.trim());
                    }
                } else {
                    redirectAttributes.addFlashAttribute("registerError",
                            String.format("Ma chung cu '%s' khong ton tai! Vui long kiem tra lai.", chungCuId));
                    redirectAttributes.addFlashAttribute("openRegister", true);
                    return "redirect:/login";
                }
            }

            accountRepository.save(account);
            redirectAttributes.addFlashAttribute("registerSuccess", "Dang ky thanh cong! Vui long dang nhap.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "registerError",
                    String.format("Luu that bai: %s", e.getMessage())
            );
            redirectAttributes.addFlashAttribute("openRegister", true);
        }

        return "redirect:/login";
    }
}