import { fadeLoad } from './utility.js';

export function initProductEditIn(){
    
    loadProductForEdit();

    $(document).off('submit', '#editProductForm').on('submit', '#editProductForm', function(e) {
        e.preventDefault();

        const formData = new FormData(this);
        const productId = $('#main-content').data('productId');

        $.ajax({
            url: `/api/updateProduct/${productId}`,
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function() {
                alert('Product updated!');
                fadeLoad('#main-content', 'editProduct.html');
            },
            error: function() {
                alert("Something went wrong!");
            }
        });
    });
}

function loadProductForEdit(){
    const productId = $('#main-content').data('productId');
    if (!productId) {
        return;
    }

    if (productId) {
        $.ajax({
            url: `/api/products/${productId}`,
            method: 'GET',
            success: function(product) {
                $('#productId').val(product.id);
                $('#productName').val(product.name);
                $('#unit').val(product.unit);
                $('#price').val(product.defaultPrice);
                $('#category').val(product.category);
                $('#image').prop('required', false);
                $('#editProductForm button[type="submit"]').text("Update Product");
            }
        });
    }
    
    console.log("Name:", $('#productName').val());
}

