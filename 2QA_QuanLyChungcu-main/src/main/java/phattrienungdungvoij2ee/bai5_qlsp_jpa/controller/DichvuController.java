package phattrienungdungvoij2ee.bai5_qlsp_jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.Category_Dichvu;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.Dichvu;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.service.CategoryDichvuService;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.service.DichvuService;

@Controller
@RequestMapping("/services")
public class DichvuController {

    @Autowired//
    private DichvuService dichvuService;

    @Autowired
    private CategoryDichvuService categoryDichvuService;

    // ===== LIST =====
    @GetMapping
    public String list(Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("services", dichvuService.getAllServices());
        } catch (Exception e) {
            model.addAttribute("services", java.util.Collections.emptyList());
            model.addAttribute("errorMsg", "Khong tai duoc danh sach dich vu: " + e.getMessage());
        }
        return "service/list";
    }

    // ===== ADD =====
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("service", new Dichvu());
        if (!loadDropdown(model)) {
            model.addAttribute("errorMsg", "Khong tai duoc danh sach nhom dich vu.");
        }
        return "service/add";
    }

    // ===== SAVE =====
    @PostMapping("/save")
    public String save(@ModelAttribute("service") Dichvu dichvu,
                       @RequestParam("categoryId") Long categoryId,
                       RedirectAttributes redirectAttributes) {
        try {
            Category_Dichvu category = categoryDichvuService.getCategoryById(categoryId);
            if (category == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "Nhom dich vu khong ton tai.");
                return "redirect:/services";
            }
            dichvu.setCategory(category);
            dichvuService.saveService(dichvu);
            redirectAttributes.addFlashAttribute("successMsg", "Lưu dịch vụ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lưu thất bại: " + e.getMessage());
        }
        return "redirect:/services";
    }

    // ===== EDIT =====
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        Dichvu service = dichvuService.getServiceById(id);
        if (service == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Không tìm thấy dịch vụ!");
            return "redirect:/services";
        }
        model.addAttribute("service", service);
        if (!loadDropdown(model)) {
            redirectAttributes.addFlashAttribute("errorMsg", "Khong tai duoc danh sach nhom dich vu.");
            return "redirect:/services";
        }
        return "service/edit";
    }

    // ===== DELETE =====
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            dichvuService.deleteService(id);
            redirectAttributes.addFlashAttribute("successMsg", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Xóa thất bại: " + e.getMessage());
        }
        return "redirect:/services";
    }

    // ===== LOAD DROPDOWN =====
    private boolean loadDropdown(Model model) {
        try {
            model.addAttribute("categories", categoryDichvuService.getAllCategories());
            return true;
        } catch (Exception e) {
            model.addAttribute("categories", java.util.Collections.emptyList());
            return false;
        }
    }
}
