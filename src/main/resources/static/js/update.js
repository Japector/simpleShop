import { updateNavbarVisibility } from './utility.js';

export function initUpdate(){
    getUserData();

    $('#profilerefresh').on('submit', function(e) {
        e.preventDefault();

        var formData = {
            firstName: $('#firstname').val(),
            lastName: $('#lastname').val(),
            email: $('#email').val(),
            password: $('#password').val()
        };

        console.log(formData);
        $.ajax({
            type: 'PUT',
            url: '/update',
            data: JSON.stringify(formData),
            contentType: 'application/json',
            success: function(response) {
                getUserData(1);
                $('#result').html(response);
                updateNavbarVisibility()
            },
            error: function(response) {

                    if (response.status === 400) {
                        var errors = response.responseJSON;
                        var errorMessages = '<ul>';


                        for (var fieldName in errors) {
                            errorMessages += '<li>' + fieldName + ': ' + errors[fieldName] + '</li>';
                        }
                        errorMessages += '</ul>';
                        $('#result').html(errorMessages);
                    } else {

                        $('#result').html('An unexpected error occurred.');
                    }
            }
        });
    });

}

function getUserData(){
    $.ajax({
    type: 'GET',
    url: '/getUser',
    data: 'json',
    success: function(user) {
        console.log(user);
        $('#lastname').val(user.lastName);
        $('#firstname').val(user.firstName);
        $('#email').val(user.email);
        $('#result').html('Success getting user data!');
    },
    error: function() {
        $('#result').html('An error occurred.');
    }
    });
}