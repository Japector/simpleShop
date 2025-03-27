export function initShoppingList(){


    fetchShoppingList();

    $('#addItem').click(function() {
        var tr = $(this).closest('tr');
        var unit = tr.find('td:eq(3)').text()
        var newRow = `<tr class="text-center" data-unit="Custom" data-id="-1"> 
            <td><input type="text" class="form-control" name="item"></td>
            <td><input type="number" class="form-control" name="price" step="0.01"></td>
            <td><input type="number" class="form-control" name="quantity"></td>
            <td>${unit}</td>
            <td><button class="btn btn-primary saveItem">Save</button></td>
        </tr>`;

        $('#shoppingListTable tbody').append(newRow);
    });


    $(document).off('click', '.saveItem').on('click', '.saveItem', function() {
        var tr = $(this).closest('tr');
        var productId = tr.data('id');
        var name = tr.find('td:eq(0)').text();
        var price = tr.find('td:eq(1)').text();
        var quantity = parseInt(tr.find('input[name="quantity"]').val());

        const itemDto = {
            productId: productId,
            name: name,
            quantity: quantity,
            price: price
        };


        if (name === "" || price === "" || quantity === "") {
            alert("Please fill all fields before saving.");
            return;
        }

        console.log(name, price, quantity);

        $.ajax({
            type: 'PUT',
            url: '/updateItem',
            contentType: 'application/json',
            data: JSON.stringify(itemDto),
            success: function () {
                alert("Item updated!");
            },
            error: function (xhr, status, error) {
                console.error("Failed to update item:", error);
            }
        });


        tr.find('td:eq(0)').text(name);
        tr.find('td:eq(1)').text(price);
        tr.find('td:eq(2)').text(quantity);
        tr.find('td:eq(4)').html(`
            <button class="btn btn-warning updateItem text-center"><i class="fas fa-sync"></i></button>
            <button class="btn btn-danger deleteItem text-center"><i class="fas fa-times"></i></button>
        `).addClass('text-center');
        updateFinalSum();

    });


    $(document).off('click', '.deleteItem').on('click', '.deleteItem', function() {
        
        var tr = $(this).closest('tr');
        const productId = tr.data('id')

        $.ajax({
            type: 'DELETE',
            url: `/removeItem/${productId}`,
            success: function(response) {
                console.log('Product deleted successfully!');
            },
            error: function(xhr, status, error) {
                console.error("Failed to delete item:", error);
                alert("Failed to delete item.");
            }
        });

        tr.remove();
        updateFinalSum();
        
    });



    $(document).off('click', '.updateItem').on('click', '.updateItem', function() {
        var tr = $(this).closest('tr');
        var name = tr.find('td:eq(0)').text();
        var price = tr.find('td:eq(1)').text();
        var quantity = tr.find('td:eq(2)').text();
        var unit = tr.find('td:eq(3)').text();

        tr.find('td:eq(0)').text(name);
        tr.find('td:eq(1)').text(price);
        tr.find('td:eq(2)').html(`<input type="number" class="form-control" name="quantity" value="${quantity}">`);
        tr.find('td:eq(3)').text(unit);
        tr.find('td:eq(4)').html(`<button class="btn btn-primary saveItem">Save</button>`);
    });



    $('#refreshList').click(function() {
        var items = [];


        $('#shoppingListTable tbody tr').each(function() {
            var item = {
                name: $(this).find('td:eq(0)').text(),
                price: parseFloat($(this).find('td:eq(1)').text()),
                quantity: parseInt($(this).find('td:eq(2)').text(), 10),
                unit: $(this).data('unit'),
                productId: $(this).data('id')
            };
            items.push(item);
        });

        console.log(items);

        $.ajax({
            type: 'POST',
            url: '/saveShoppingList',
            contentType: 'application/json',
            data: JSON.stringify(items),
            success: function(response) {
                console.log('List saved successfully!');
            },
            error: function(xhr, status, error) {
                console.error('Error saving list:', error);

            }
        });
    });

}


function updateFinalSum() {
    var totalSum = 0;


    $('#shoppingListTable tbody tr').each(function() {
        var price = parseFloat($(this).find('td:eq(1)').text());
        var quantity = parseInt($(this).find('td:eq(2)').text(), 10);

        if (!isNaN(price) && !isNaN(quantity)) {
            totalSum += price * quantity;
        }
    });


    $('#totalSum').text(totalSum.toFixed(2) + " €");
}

function fetchShoppingList() {
    $.ajax({
        type: 'GET',
        url: '/fetchShoppingList',
        data: 'json',
        success: function(items) {

            items.forEach(function(item) {
                var row = `<tr class="text-center" data-id="${item.productId}" data-name="${item.name}" data-price="${item.price}" data-quantity="${item.quantity}" data-unit="${item.unit}">
                    <td>${item.name}</td>
                    <td>${item.price.toFixed(2)}</td>
                    <td>${item.quantity}</td>
                    <td>${item.unit}</td>
                    <td class="text-center">
                        <button class="btn btn-warning updateItem"><i class="fas fa-sync"></i></button>
                        <button class="btn btn-danger deleteItem"><i class="fas fa-times"></i></button>
                    </td>
                </tr>`;
                $('#shoppingListTable tbody').append(row);
            });
            updateFinalSum();
        },
        error: function(xhr, status, error) {
            console.error('Error fetching shopping list:', error);
        }
    });

}

