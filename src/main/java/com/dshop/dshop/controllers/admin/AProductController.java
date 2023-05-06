package com.dshop.dshop.controllers.admin;

import com.dshop.dshop.models.Category;
import com.dshop.dshop.models.dtos.CategoryDTO;
import com.dshop.dshop.models.request.ColorRequest;
import com.dshop.dshop.models.request.ProductRequest;
import com.dshop.dshop.models.request.SizeRequest;
import com.dshop.dshop.models.response.ColorResponse;
import com.dshop.dshop.models.response.ProductResponse;
import com.dshop.dshop.models.response.SizeResponse;
import com.dshop.dshop.models.response.UserResponse;
import com.dshop.dshop.repositories.CategoryRepository;
import com.dshop.dshop.services.CategoryService;
import com.dshop.dshop.utils.GetUserFromToken;
import com.dshop.dshop.services.ProductService;
import com.dshop.dshop.utils.JwtUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AProductController {

    private final ProductService productService;

    private final CategoryRepository categoryRepository;

    private final CategoryService categoryService;

    private final GetUserFromToken getUserFromToken;

    private final JwtUtils jwtUtils;
    @Autowired
    public AProductController(ProductService productService, CategoryRepository categoryRepository, CategoryService categoryService, GetUserFromToken getUserFromToken, JwtUtils jwtUtils) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.categoryService = categoryService;
        this.getUserFromToken = getUserFromToken;
        this.jwtUtils = jwtUtils;
    }
    //Lấy thông tin người dùng
    @ModelAttribute("userLogined")
    public UserResponse getUserLogined(ModelMap model, HttpServletRequest request) {
        return getUserFromToken.getUserFromToken(request);
    }

    @GetMapping("/product")
    public String getAllProduct(Model model, HttpServletRequest request,
                                @RequestParam(defaultValue = "0") int pageNumber,
                                @RequestParam(defaultValue = "10") int pageSize,
                                @RequestParam(defaultValue = "id") String sortBy){
        model.addAttribute("countProduct",productService.countProduct());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", pageSize);
        List<ProductResponse> productResponses = productService.getAllProducts(pageNumber,pageSize, sortBy).stream().map(p -> {
            ProductResponse productResponse = new ProductResponse();
            productResponse.setName(p.getName());
            productResponse.setSku(p.getSku());
            productResponse.setPrice(p.getPrice());
            productResponse.setDiscountPrice(p.getDiscountPrice());
            productResponse.setColorResponses(p.getColorResponses());
            productResponse.setImageResponses(p.getImageResponses());
            productResponse.setDescription(p.getDescription());
            productResponse.setVisited(p.getVisited());
            productResponse.setId(p.getId());
            return productResponse;
        }).collect(Collectors.toList());
        model.addAttribute("products",productResponses);
        return "admin/products";
    }

    @ModelAttribute("categoryList")
    public List<Category> getCategories(){
        return categoryRepository.findAll();
    }
    @GetMapping("/product/create")
    public String createProduct(ModelMap model){
        ProductRequest productRequest = new ProductRequest();
        model.addAttribute("productRequest", productRequest);
        return "admin/newProduct";
    }

    @PostMapping("/product/create")
    public String createProduct(ModelMap model, @ModelAttribute("productRequest") ProductRequest productRequest
                                , @RequestParam("images") MultipartFile[] images
                                , @RequestParam("jsonColors") String jsonColors
                                , HttpServletRequest request
                                )throws IOException {

        JSONObject jsonObj = new JSONObject(jsonColors);

        JSONArray colorsArr = jsonObj.getJSONArray("colors");
        List<ColorRequest> colorRequests = new ArrayList<>();

        for (int i = 0; i < colorsArr.length(); i++) {
            ColorRequest colorRequest = new ColorRequest();
            JSONObject colorObj = colorsArr.getJSONObject(i);
            String value = colorObj.getString("value");
            colorRequest.setValue(value);
            JSONArray sizesArr = colorObj.getJSONArray("sizes");
            List<SizeRequest> sizeRequests = new ArrayList<>();
            for (int j = 0; j < sizesArr.length(); j++) {
                JSONObject sizeObj = sizesArr.getJSONObject(j);
                SizeRequest sizeRequest =new SizeRequest();

                String sizeValue = sizeObj.getString("value");
                int total = sizeObj.getInt("total");
                sizeRequest.setValue(sizeValue);
                sizeRequest.setTotal(total);
                sizeRequests.add(sizeRequest);
            }
            colorRequest.setSizes(sizeRequests);
            colorRequests.add(colorRequest);
        }
        productRequest.setColors(colorRequests);
        Long userId = jwtUtils.getUserIdFromToken(jwtUtils.getTokenLoginFromCookie(request));
        productRequest.setUserId(userId);
        productService.createProduct(productRequest,images);

        return "redirect:/admin/product";
    }

    @GetMapping("/product/detail/{id}")
    public String productDetail(ModelMap model, HttpServletRequest request,
                                HttpServletResponse response, RedirectAttributes redirectAttributes,
                                @PathVariable("id") Long productId){
        try {
            //Lấy thông tin sản phẩm xem chi tiết
            ProductResponse productResponse = productService.getProductById(productId);
            model.addAttribute("productDetail", productResponse);

            model.addAttribute("category", categoryService.getCategoryById(productResponse.getCategoryId()));
            // Lấy ra mảng Color và mảng Size
            List<ColorResponse> colorResponses = productResponse.getColorResponses();

            // Lấy dánh sách màu sắc trong sản phẩm
            List<String> listColors = colorResponses.stream()
                    .map(ColorResponse::getValue)
                    .distinct()
                    .toList();

            // Lấy danh sách size
            List<String> listSizes = colorResponses.stream()
                    .flatMap(color -> color.getSizeResponses().stream())
                    .map(SizeResponse::getValue)
                    .distinct()
                    .sorted(Comparator.comparingInt(Integer::parseInt))
                    .toList();

            // Lấy thông tin số lượng đã bán của sản phẩm
            int sold = colorResponses.stream()
                    .flatMap(color -> color.getSizeResponses().stream())
                    .mapToInt(SizeResponse::getSold)
                    .sum();

            //Truyền về số lượng của từ sản phẩm theo cặp MÀU-SIZE
            Map<String, String> totalByColorAndSize = colorResponses.stream()
                    .flatMap(color -> color.getSizeResponses().stream()
                            .map(size -> {
                                String key = color.getValue() + " / " + size.getValue();
                                return Map.entry(key, size.getTotal() +"-"+ size.getId());
                            }))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));

            // Gửi dữ liệu đi bằng model map
            model.addAttribute("listColors",listColors);
            model.addAttribute("listSizes",listSizes);
            model.addAttribute("sold", sold);
            model.addAttribute("totalByColorAndSize", totalByColorAndSize);
            return "admin/productDetail";
        }catch (Exception e){
            return "redirect:/admin/product?err=false";
        }

    }

}
