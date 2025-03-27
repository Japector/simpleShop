import { fadeLoad } from './utility.js';

export function initProductAdd(){
    $(document).off('submit', '#addProductForm').on('submit', '#addProductForm', function(e) {
        e.preventDefault();
        const formData = new FormData(this);

        $.ajax({
            type: 'POST',
            url: '/api/products/add',
            data: formData,
            processData: false,
            contentType: false,
            success: function() {
                alert("Product added successfully!");
                fadeLoad('#main-content', 'products.html');
            },
            error: function() {
                alert("Failed to add product.");
            }
        });
    });
}