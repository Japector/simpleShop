import { fadeLoad } from './utility.js';

export function initProducts(){
    $(document).off('click', '.category-card').on('click', '.category-card', function () {
        const category = $(this).data('category');

        $.ajax({
            url: `/listProducts/${category}`,
            method: 'GET',
            success: function (items) {
                $('#category-title').text(category);

                const productCards = items.map(p => `
                    <div class="col-md-4 mb-4 products">
                    <div class="card h-100 shadow-sm">
                        <img src="${p.pathToPicture}" class="card-img-top" alt="${p.name}" style="height: 200px; object-fit: cover;">
                        <div class="card-body text-center">
                        <h5 class="card-title">${p.name}</h5>
                        <p class="card-text">${p.defaultPrice} € / ${p.unit}</p>
                        <input type="number" class="form-control mb-2 quantity-input" placeholder="Quantity" data-id="${p.id}">
                        <button class="btn btn-primary add-button"
                                data-id="${p.id}" 
                                data-name="${p.name}"
                                data-unit="${p.unit}"
                                data-price="${p.defaultPrice}">Add</button>
                        </div>
                    </div>
                    </div>
                `).join('');

                $('#main-content').html(`
                    <div class="row">${productCards}</div>
                    <div class="text-center mt-4">
                    <button class="btn btn-secondary" id="back-to-categories">← Back to Categories</button>
                    </div>
                `);
            },
            error: function () {
                $('#main-content').html('<div class="alert alert-danger">Could not load products.</div>');
            }
        });
    });


    $(document).off('click', '.add-button').on('click', '.add-button', function () {
        const productId = $(this).data('id');
        const name = $(this).data('name');
        const quantity = $(`.quantity-input[data-id="${productId}"]`).val();
        const price = $(this).data('price');
        const unit = $(this).data('unit');

        if (!quantity || quantity <= 0) {
            alert("Please enter a valid quantity.");
            return;
        }

        const itemDto = {
            productId: productId,
            name: name,
            quantity: quantity,
            price: price,
            unit: unit
        };

        console.log("Item to send:", itemDto);

        $.ajax({
            url: '/addItem',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(itemDto),
            success: function () {
                alert("Item added to your shopping list.");
            },
            error: function () {
                alert("Failed to add item. Make sure you're logged in.");
            }
        });
    });


    $(document).on('click', '#back-to-categories', function () {
        fadeLoad('#main-content', 'products.html');
    });
}