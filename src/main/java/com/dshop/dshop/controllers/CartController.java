package com.dshop.dshop.controllers;

import com.dshop.dshop.mapper.UserMapper;
import com.dshop.dshop.models.dtos.CategoryDTO;
import com.dshop.dshop.models.dtos.UserDTO;
import com.dshop.dshop.models.request.CheckOutRequest;
import com.dshop.dshop.models.request.OrderDetailRequest;
import com.dshop.dshop.models.request.OrderItemRequest;
import com.dshop.dshop.models.response.OrderDetailResponse;
import com.dshop.dshop.models.response.OrderItemResponse;
import com.dshop.dshop.models.response.UserResponse;
import com.dshop.dshop.services.CategoryService;
import com.dshop.dshop.services.OrderDetailService;
import com.dshop.dshop.services.UserService;
import com.dshop.dshop.utils.GetUserFromToken;
import com.dshop.dshop.utils.JwtUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/home/cart")
public class CartController {
    private final OrderDetailService orderDetailService;

    private final GetUserFromToken getUserFromToken;
    private final UserService userService;
    private final UserMapper userMapper;

    private final CategoryService categoryService;
    private final JwtUtils jwtUtils;
    public CartController(OrderDetailService orderDetailService, GetUserFromToken getUserFromToken, UserService userService, UserMapper userMapper, CategoryService categoryService, JwtUtils jwtUtils) {
        this.orderDetailService = orderDetailService;
        this.getUserFromToken = getUserFromToken;
        this.userService = userService;
        this.userMapper = userMapper;
        this.categoryService = categoryService;
        this.jwtUtils = jwtUtils;
    }
    @ModelAttribute("userLogined")
    public UserResponse getUserLogined(ModelMap model, HttpServletRequest request) {
        return getUserFromToken.getUserFromToken(request);
    }
    @ModelAttribute("categories")
    public List<CategoryDTO> getCategories(){
        return categoryService.getAllCategories();
    }
    @ModelAttribute("cartId")
    public Long getOrderDetailId(HttpServletRequest request){
        UserResponse userResponse = getUserFromToken.getUserFromToken(request);
        OrderDetailResponse orderDetailResponse = orderDetailService.getCartByUserAndType(userResponse.getId(), 0);
        if (orderDetailResponse == null){
            return null;
        }else {
            return orderDetailService.getCartByUserAndType(userResponse.getId(), 0).getId();
        }
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
    @ModelAttribute("carts")
    public List<OrderItemResponse> getAllItemFromCart(HttpServletRequest request){
        // Lấy id người dùng
        String token = jwtUtils.getTokenLoginFromCookie(request);
        //Lấy id người dùng
        Long userId = jwtUtils.getUserIdFromToken(token);
        return orderDetailService.getItemFromCart(userId);
    }
    @GetMapping
    public String viewCart(HttpServletRequest request, ModelMap model){
        return "web/cart";
    }

    @PostMapping("/addtocart")
    public String addToCart(ModelMap model, HttpServletRequest request,
                            @ModelAttribute("OrderItemRequest") OrderItemRequest orderItemRequest){
        //Lấy thông tin user
        String token = jwtUtils.getTokenLoginFromCookie(request);
        Long userId = jwtUtils.getUserIdFromToken(token);
        orderDetailService.addToOrder(userId,orderItemRequest);
        return "redirect:/home/cart";
    }
    //Xử lý tăng, giảm số lượng sản phẩm
    @PutMapping("/{id}")
    public String operateItem(ModelMap model,@PathVariable("id") Long cartItemId ,
                              @RequestParam("o") String operation){
        orderDetailService.plusOrMinusQuantity(cartItemId,operation);
        return "redirect:/home/cart";
    }
    @PersistenceContext
    EntityManager entityManager;
    @DeleteMapping("/delete/{id}")
    @Transactional
    public String removeFromCart(@PathVariable("id") Long itemId, ModelMap model) {
        Query query = entityManager.createQuery("DELETE FROM OrderItem oi WHERE oi.id = :id");
        query.setParameter("id", itemId);
        query.executeUpdate();
        return "redirect:/home/cart";
    }

    @GetMapping("/checkout")
    public String getCheckOut(ModelMap model, HttpServletRequest request,
                           @RequestParam("id") long orderDetailId,
                           @RequestParam("totalQuantity") String totalQuantity,
                           @RequestParam("totalAmount") String totalAmount) throws ParseException {
        //Lấy thông tin người dùng đang đăng nhập
        String token = jwtUtils.getTokenLoginFromCookie(request);
        Long userId = jwtUtils.getUserIdFromToken(token);
        UserDTO user = userService.getUserById(userId);
        model.addAttribute("userCheckOut",user);

        OrderDetailRequest orderDetailRequest = new OrderDetailRequest();
        orderDetailRequest.setId(orderDetailId);
        orderDetailRequest.setTotalQuantity(Integer.parseInt(totalQuantity));
        orderDetailRequest.setTotalAmount(Integer.parseInt(totalAmount.replace(".", "").replace("đ", "")));
        model.addAttribute("orderDetailRequest", orderDetailRequest);

        return "web/checkout";
    }

    @PutMapping("/checkout")
    public String checkOut(ModelMap model, HttpServletRequest request,
                           @ModelAttribute("checkOutRequest")CheckOutRequest checkOutRequest){
        try{
            String token = jwtUtils.getTokenLoginFromCookie(request);
            Long userId = jwtUtils.getUserIdFromToken(token);

            OrderDetailRequest orderDetailRequest = new OrderDetailRequest();
            orderDetailRequest.setId(checkOutRequest.getId());
            orderDetailRequest.setTotalQuantity(checkOutRequest.getTotalQuantity());
            orderDetailRequest.setTotalAmount(checkOutRequest.getTotalAmount());
            orderDetailRequest.setPhone(checkOutRequest.getPhone());
            if(checkOutRequest.getNotes() != null && !checkOutRequest.getNotes().equals("")){
                orderDetailRequest.setNotes(checkOutRequest.getNotes());
            }else {
                orderDetailRequest.setNotes("Không");
            }

            if(!Objects.equals(checkOutRequest.getWard(), "") && !Objects.equals(checkOutRequest.getDistrict(), "") && !Objects.equals(checkOutRequest.getCity(), "")){
                orderDetailRequest.setAddress(checkOutRequest.getAddress() +"; "+ checkOutRequest.getWard() + ", " + checkOutRequest.getDistrict()+", "+ checkOutRequest.getCity());
            }else {
                orderDetailRequest.setAddress(checkOutRequest.getAddress());
            }
            if(Objects.equals(checkOutRequest.getPaymentMethod(), "cod")){
                orderDetailRequest.setPaymentId((long)1);
            }else if (Objects.equals(checkOutRequest.getPaymentMethod(), "bankTransfer")){
                orderDetailRequest.setPaymentId((long)2);
            }
            OrderDetailResponse orderDetailResponse = orderDetailService.checkOut(userId,orderDetailRequest);
            return "redirect:/home/user";
        }catch (Exception e){
            return null;
        }

    }

}

