import { fadeLoad } from './utility.js';

export function initProductEdit(){

    $('#categorySelect').change(function () {
        const category = $(this).val();
        if (!category) return;

        $.ajax({
            url: `/listProducts/${category}`, 
            method: 'GET',
            success: function (products) {
                const $select = $('#productSelect');
                $select.empty();
                $select.append(`<option value="">-- Choose a Product --</option>`);
                products.forEach(p => {
                    $select.append(`<option value="${p.id}">${p.name}</option>`);
                });
                $('#productSelectContainer').show();
            },
            error: function (xhr) {
                alert("Could not load products. Are you logged in?");
            }
        });
    });


    $('#productSelect').change(function () {
        const productId = $(this).val();
        if (!productId) return;

        $.ajax({
            url: `/api/products/${productId}`,
            method: 'GET',
            success: function (product) {
                const row = `
                    <tr data-id="${product.id}" data-category="${product.category}">
                        <td>${product.name}</td>
                        <td>${product.unit}</td>
                        <td>${product.defaultPrice}</td>
                        <td><img src="${product.pathToPicture}" width="50" height="50" style="object-fit: cover;" alt="${product.name}"></td>
                        <td>
                            <button class="btn btn-warning editProduct"><i class="fas fa-edit"></i> Modify</button>
                            <button class="btn btn-danger deleteProduct"><i class="fas fa-trash"></i> Delete</button>
                        </td>
                    </tr>`;
                $('#productTableBody').append(row);
                $('#productDetails').show();
            },
            error: function () {
                alert("Failed to load product details.");
            }
        });
    });

    $(document).off('click','.deleteProduct').on('click', '.deleteProduct', function () {
        const productId = $(this).closest('tr').data('id');
        if (!confirm("Are you sure you want to delete this product?")) return;

        $.ajax({
            url: `/api/deleteProduct/${productId}`,
            type: 'DELETE',
            success: function () {
                alert("Product deleted successfully.");
                $('#productDetails').hide();
            }
        });
    });

    $(document).off('click','.editProduct').on('click', '.editProduct', function () {
        const productId = $(this).closest('tr').data('id');
        $('#main-content').data('productId', productId);
        fadeLoad('#main-content', 'editProductIn.html');
    });

}
