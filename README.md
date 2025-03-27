# Simple Shop App

This is a simple webshop application that allows users to register, log in, browse products by categories, manage their shopping list, and access account and catalog management features.

## Features

### User:
- **Register**: First register your user.
- **Login**: Use your credential to have access to your secure session
- **Update**: Update your profile whenever you want
- **Delete your info**: According to our data protection guidlines you can delete your user anytime by a click of a button.
- **Browse Products by Category**: Products are grouped into categories (e.g., Fruits, Vegetables, Baked Goods). Users can browse and choose items from these categories.

- **Add to Shopping List**  
  Users can add products to their shopping list directly from the product catalog.

- **Modify Shopping List**  
  On the **Shopping List** tab, users can:
   - Change the **quantity** of items
   - **Delete** items they no longer wish to purchase
   - 
- **Logout**  
  Users can securely log out of their account at any time.

### Admin Functionality (Accessible via the Admin Menu)

- **Account Management**
   - Update account information (redirects to the account update page)
   - Delete account permanently

- **Product Catalog Management**
   - Add new products to the catalog, including:
      - Product name
      - Category
      - Price
      - Unit of measure (e.g., kg, pcs, l)
      - Product image upload
   - Browse the current product assortment
   - Delete existing products from the catalog
   - Update existing product details such as:
      - Price
      - Unit of measure
      - Product image

### Security:
- **Session Management**: Secure session control to ensure your data is safe and only accessible to you.

## Technologies Used

- **Frontend**: HTML, CSS, JavaScript, jQuery
- **Backend**: Java with Spring Boot
- **Database**: H2
- **Containerization**: Docker
- **CI/CD**: GitHub Actions
- **Authentication**: Session-based authentication for secure access

## Getting Started

### Prerequisites

Before you can run the application, you need to have at least:
- Docker (for containerized image)

### Getting Started:

1. **Clone the repository**
   ```bash
   git clone https://github.com/Japector/simpleShop.git
   cd simpleShop