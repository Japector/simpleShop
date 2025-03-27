export function initUsers(){
    loadUsers();
    $(document).off('click', '.delete').on('click', '.delete', function(e) {
        e.preventDefault();
        $.ajax({
            type: 'DELETE',
            url: '/delete',
            success: function(response) {
                console.log(response);
                $('#result').html(response);
                window.location.href = 'index.html';
            },
            error: function(xhr, status, error) {
                $('#result').html('An error occurred.');
                console.error('Error deleting user:', error);
            }
        });
    });
}


function loadUsers(){
    $.ajax({
        type: 'GET',
        url: '/users',
        data: 'json',

        success: function(users) {
            console.log(users);
            var $table = $('#users');
            $.each(users, function(i, user) {
                var $row = $('<tr>').append(
                    $('<td>').text(user.firstName),
                    $('<td>').text(user.lastName),
                    $('<td>').text(user.email),
                    $('<td>').html('<a href="update.html" data-spa>Modify</a>/<a href="#" class="delete">Delete</a>')

                );
                $table.append($row);
            });
            $('#result').html('Success!');
        },
        error: function() {
            $('#result').html('An error occurred.');
        }
    });
};    
