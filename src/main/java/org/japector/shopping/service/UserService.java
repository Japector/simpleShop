package org.japector.shopping.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import org.h2.engine.User;
import org.japector.shopping.entity.ItemEntity;
import org.japector.shopping.entity.ProductEntity;
import org.japector.shopping.entity.UserEntity;
import org.japector.shopping.exception.EmailAlreadyInUseException;
import org.japector.shopping.exception.ProductNotFoundException;
import org.japector.shopping.exception.UnknownUserException;
import org.japector.shopping.model.ItemDto;
import org.japector.shopping.model.LoginDto;
import org.japector.shopping.model.UserDto;
import org.japector.shopping.repository.ItemRepository;
import org.japector.shopping.repository.ProductRepository;
import org.japector.shopping.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;




@Service
public class UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, ItemRepository itemRepository,
                       ProductRepository productRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public UserEntity  registerUser(String firstName, String lastName, String email, String password) {
        if (userRepository.findByEmail(email) != null) {
            throw new EmailAlreadyInUseException("Email already in use");
        }

        String hashedPassword = passwordEncoder.encode(password);

        UserEntity newUser = new UserEntity();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword);

        return userRepository.save(newUser);

    }

    public UserEntity findUserByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    public void saveProduct(ProductEntity product) {
        productRepository.save(product);
    }

    public List<UserDto> findAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto findUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UnknownUserException("User not found with id: " + id));
        return convertToDto(userEntity);
    }

    public List<ItemDto> findAllItemsForUser(UserDto userDto) {

        UserEntity userEntity = userRepository.findByEmail(userDto.getEmail());
        List<ItemEntity> items = itemRepository.findByUser(userEntity);

        return items.stream()
                .map(this::convertToItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductEntity getProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    @Transactional
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }


    @Transactional
    public void updateProduct(ProductEntity product, Long productId) {
        ProductEntity productExisting = productRepository.findById(productId).orElseThrow(() ->
                new ProductNotFoundException("Product not found"));

        if (productExisting != null) {
            productExisting.copyFrom(product);
            productRepository.save(productExisting);
        } else {
            throw new ProductNotFoundException("Product not found");
        }
    }


    @Transactional
    public List<ProductEntity> getProductsInCategory(String category) {
        return productRepository.findByCategory(category);
    }


    @Transactional
    public void addItemToUser(ItemDto itemDto, UserDto userDto) {
        UserEntity user = userRepository.findByEmail(userDto.getEmail());
        ProductEntity product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        Optional<ItemEntity> existingItem = itemRepository.findByUserAndProduct(user, product);

        ItemEntity item;
        if (existingItem.isPresent()) {
            item = existingItem.get();
            item.setQuantity(item.getQuantity() + itemDto.getQuantity());
        } else {
            item = mapToItemEntity(itemDto, user, product);
        }
        itemRepository.save(item);
    }

    @Transactional
    public void removeItemFromUser(Long productId, UserDto userDto) {

        UserEntity userEntity = userRepository.findByEmail(userDto.getEmail());
        Optional<ProductEntity> product = productRepository.findById(productId);

        if (product.isEmpty()) {
            throw new ProductNotFoundException("There is no such product:");
        } else {
            itemRepository.deleteByUserAndProduct(userEntity, product.get());
        }
    }

    @Transactional
    public void updateItemForUser(ItemDto itemDto, UserDto userDto) {
        UserEntity user = userRepository.findByEmail(userDto.getEmail());
        Optional<ItemEntity> existingItem = itemRepository.findByUserIdAndProductId(user.getId(), itemDto.getProductId());

        if (existingItem.isPresent()) {
            ItemEntity item = existingItem.get();
            item.setQuantity(itemDto.getQuantity());
            item.setPrice(itemDto.getPrice());
            itemRepository.save(item);
        } else {
            throw new ProductNotFoundException("Product not found");
        }
    }


    @Transactional
    public void saveItemsForUser(List<ItemDto> itemDtoList, UserDto userDto) {

        UserEntity userEntity = userRepository.findByEmail(userDto.getEmail());
        itemRepository.deleteAllByUser(userEntity);

        for (ItemDto item : itemDtoList) {
            addItemToUser(item, userDto);
        }
    }

    public UserDto updateUser(UserDto userDto) {
        UserEntity userEntity = userRepository.findByEmail(userDto.getEmail());
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        userEntity.setEmail(userDto.getEmail());

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        userEntity = userRepository.save(userEntity);

        return convertToDto(userEntity);
    }

    public void deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new UnknownUserException("User not found with id: " + id);
        }
    }


    public UserDto authenticateUser(LoginDto loginDto) {
        UserEntity user = findUserByEmail(loginDto.getEmail());
        if (user == null) {
            throw new UnknownUserException("User is not registered!");
        }
        if (passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return convertToDto(user);
        }
        return null;
    }

    public UserDto convertToDto(UserEntity userEntity) {
        return UserDto.builder()
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(userEntity.getEmail())
                .build();
    }

    public ItemDto convertToItemDto(ItemEntity itemEntity) {
        return ItemDto.builder()
                .productId(itemEntity.getProduct().getId())
                .name(itemEntity.getProduct().getName())
                .price(itemEntity.getPrice())
                .quantity(itemEntity.getQuantity())
                .unit(itemEntity.getProduct().getUnit())
                .build();

    }

    public ItemEntity mapToItemEntity(ItemDto itemDto, UserEntity user, ProductEntity product) {
        return ItemEntity.builder()
                .user(user)
                .product(product)
                .quantity(itemDto.getQuantity())
                .price(itemDto.getPrice() != 0 ? itemDto.getPrice() : product.getDefaultPrice())
                .build();
    }


    public ItemDto convertProductToItemDto(ProductEntity productEntity) {
        return ItemDto.builder()
                .productId(productEntity.getId())
                .name(productEntity.getName())
                .price(productEntity.getDefaultPrice())
                .quantity(1.0)
                .build();

    }


}
