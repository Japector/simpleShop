package org.japector.shopping.controller;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.japector.shopping.entity.ProductEntity;
import org.japector.shopping.entity.UserEntity;
import org.japector.shopping.model.ItemDto;
import org.japector.shopping.model.LoginDto;
import org.japector.shopping.model.UserDto;
import org.japector.shopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class Controller {

    @Autowired
    private UserService userService;


    @PostMapping("/registration")
    public ResponseEntity<?> registration(@Valid @RequestBody UserDto userDto) {
        UserEntity userEntity = userService.registerUser(userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                userDto.getPassword());
        if (userEntity != null) {
            return ResponseEntity.ok().body("Successful registration!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cannot be registered");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody UserDto userDto, HttpSession session) {
        UserDto userDtoUpdated = userService.updateUser(userDto);
        if (userDtoUpdated != null) {
            session.setAttribute("user", userDtoUpdated);
            return ResponseEntity.ok().body("Successful update!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client cannot be found!");
        }
    }

    @PostMapping("/saveShoppingList")
    public ResponseEntity<?> saveShoppingList(@Valid @RequestBody List<ItemDto> itemDtoList, HttpSession session) {
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto.getEmail() != null) {
            userService.saveItemsForUser(itemDtoList, userDto);
            return ResponseEntity.ok().body("Successful update!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cannot update shopping list!");
        }
    }

    @GetMapping("/fetchShoppingList")
    public ResponseEntity<?> fetchShoppingList(HttpSession session) {
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto.getEmail() != null) {
            List<ItemDto> itemDtoList = userService.findAllItemsForUser(userDto);
            return ResponseEntity.ok().body(itemDtoList);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cannot get shopping list!");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?>  deleteUser(HttpSession session) {
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto.getEmail() != null) {
            UserEntity userEntity = userService.findUserByEmail(userDto.getEmail());
            System.out.println(userEntity.toString());
            session.invalidate();
            userService.deleteUserById(userEntity.getId());
            return ResponseEntity.ok().body("User deleted");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found in session!");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>>  getUsers() {
        List<UserDto> userDtoList = userService.findAllUsers();
        return ResponseEntity.ok(userDtoList);
    }


    @GetMapping("/getUser")
    public ResponseEntity<UserDto>  getAUser(HttpSession session) {
        UserDto userDto = (UserDto) session.getAttribute("user");
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto, HttpSession session) {
        UserDto userDto = userService.authenticateUser(loginDto);
        if (userDto != null) {
            session.setAttribute("user", userDto);
            return ResponseEntity.ok().body("User authenticated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    @GetMapping("/api/session")
    public ResponseEntity<?> getLoggedInUser(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute("user");

        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }
    }


    @GetMapping("/listProducts/{category}")
    public ResponseEntity<?> listProducts(@PathVariable String category, HttpSession session) {
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        List<ProductEntity> products = userService.getProductsInCategory(category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/api/products/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable Long productId, HttpSession session) {
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        ProductEntity product = userService.getProduct(productId);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/api/updateProduct/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId,
                                           @RequestParam String name,
                                           @RequestParam String unit,
                                           @RequestParam double price,
                                           @RequestParam String category,
                                           @RequestParam("image") MultipartFile imageFile) {

        ProductEntity product = ProductEntity.builder()
                .name(name)
                .unit(unit)
                .defaultPrice(price)
                .category(category)
                .build();


        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imagePath = "uploads/categories/" + category;
                String fileName = name.replaceAll("\\s+", "_") + ".png";
                Path path = Paths.get(imagePath, fileName);

                Files.write(path, imageFile.getBytes());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed.");
            }
        } else {
            ProductEntity existing = userService.getProduct(productId);
            product.setPathToPicture(existing.getPathToPicture());
        }

        userService.updateProduct(product, productId);
        return ResponseEntity.ok("Product updated");
    }

    @PostMapping("/api/products/add")
    public ResponseEntity<?> addProduct(@RequestParam String name,
                                        @RequestParam String unit,
                                        @RequestParam double price,
                                        @RequestParam String category,
                                        @RequestParam("image") MultipartFile imageFile) {
        try {
            String imagePath = "uploads/categories/" + category;
            String fileName = name.replaceAll("\\s+", "_") + ".png";
            Path path = Paths.get(imagePath, fileName);
            Files.write(path, imageFile.getBytes());

            ProductEntity product = ProductEntity.builder()
                    .name(name)
                    .unit(unit)
                    .defaultPrice(price)
                    .category(category)
                    .pathToPicture("/uploads/categories/" + category + "/" + fileName)
                    .build();

            userService.saveProduct(product);

            return ResponseEntity.ok("Product added.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed.");
        }
    }

    @DeleteMapping("/api/deleteProduct/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId, HttpSession session) {
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        userService.deleteProduct(productId);
        return ResponseEntity.ok("Deleted");
    }

    @PostMapping("/addItem")
    public ResponseEntity<?> addItem(@Valid @RequestBody ItemDto itemDto, HttpSession session) {
        System.out.println(itemDto);
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        userService.addItemToUser(itemDto, userDto);
        return ResponseEntity.ok("Item added");
    }

    @PutMapping("/updateItem")
    public ResponseEntity<?> updateItem(@RequestBody ItemDto itemDto, HttpSession session) {
        UserDto user = (UserDto) session.getAttribute("user");
        userService.updateItemForUser(itemDto, user);
        return ResponseEntity.ok("Item updated");
    }


    @DeleteMapping("/removeItem/{productId}")
    public ResponseEntity<?> removeItem(@PathVariable Long productId, HttpSession session) {
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        userService.removeItemFromUser(productId, userDto);
        return ResponseEntity.ok("Item removed");
    }





    @GetMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        session.invalidate();

        Map<String, String> body = new HashMap<>();
        body.put("message", "Logged out successfully");
        body.put("redirect", "home.html");

        return ResponseEntity.ok(body);
    }

}
