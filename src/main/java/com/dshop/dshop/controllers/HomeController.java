package com.dshop.dshop.controllers;

import com.dshop.dshop.models.User;
import com.dshop.dshop.models.dtos.CategoryDTO;
import com.dshop.dshop.models.dtos.RoleDTO;
import com.dshop.dshop.models.response.ProductResponse;
import com.dshop.dshop.models.response.UserResponse;
import com.dshop.dshop.securities.JwtConfig;
import com.dshop.dshop.services.CategoryService;
import com.dshop.dshop.services.OrderDetailService;
import com.dshop.dshop.services.ProductService;
import com.dshop.dshop.services.UserService;
import com.dshop.dshop.utils.GetUserFromToken;
import com.dshop.dshop.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping
public class HomeController {

    private final ProductService productService;

    private final CategoryService categoryService;

    private final JwtConfig jwtConfig;

    private final JwtUtils jwtUtils;

    private final GetUserFromToken getUserFromToken;

    private final OrderDetailService orderDetailService;

    private final UserService userService;

    @Autowired
    public HomeController(ProductService productService, CategoryService categoryService, JwtConfig jwtConfig, JwtUtils jwtUtils, GetUserFromToken getUserFromToken, OrderDetailService orderDetailService, UserService userService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.jwtConfig = jwtConfig;
        this.jwtUtils = jwtUtils;
        this.getUserFromToken = getUserFromToken;
        this.orderDetailService = orderDetailService;
        this.userService = userService;
    }

    @ModelAttribute("userLogined")
    public UserResponse getUserLogined(ModelMap model, HttpServletRequest request) {
        return getUserFromToken.getUserFromToken(request);
    }
    @ModelAttribute("products")
    public Page<ProductResponse> getProducts(){
        return productService.getAllProducts(0,12,"createdDate");
    }
    @ModelAttribute("categories")
    public List<CategoryDTO> getCategories(){
        return categoryService.getAllCategories();
    }

    @ModelAttribute("cartItem")
    public int countCartItem(HttpServletRequest request){
        UserResponse user = getUserFromToken.getUserFromToken(request);
        if (user != null){
            return orderDetailService.countItemFromCart(user.getId());
        }else {
            return 0;
        }

    }
    @GetMapping
    public String home(ModelMap model, HttpServletRequest request){
        //check lại token đăng nhập từ cookie
        String checkToken = jwtUtils.getTokenLoginFromCookie(request);
        //Nếu token đăng nhập còn thời hạn, lấy thông tin ngươ dùng và điều hướng
        if(checkToken != null){
            String username = jwtConfig.getUsernameFromJWT(checkToken);

            //Lấy thông tin người dùng
            // kiểm tra lại role của người dùng đăng nhập và điều hướng
            List<RoleDTO> roleDTOS = userService.getRoleByUsername(username);
            RoleDTO roleDTO = null;

            if(roleDTOS.size() == 1){
                roleDTO = roleDTOS.get(0);
            }
            assert roleDTO != null;
            if(Objects.equals(roleDTO.getName(), "ROLE_ADMIN")){
                return "admin/index";
            }else {
                return "web/index";
            }
        }else {
            return "index";
        }

    }

}
