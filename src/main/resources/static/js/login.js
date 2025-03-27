import { fadeLoad, updateNavbarVisibility } from './utility.js';


export function initLogin(){
    $('#authform').on('submit', function(e) {
        e.preventDefault();
            var formData = {
            email: $('#email').val(),
            password: $('#password').val()
        };
        console.log(formData);
        $.ajax({
            type: 'POST',
            url: '/login',
            data: JSON.stringify(formData),
            contentType: 'application/json',
            success: function(response) {
                $('#result').html(response);
                fadeLoad('#main-content', 'home.html');
                console.log(response);
                setTimeout(() => {
                    updateNavbarVisibility();
                    fadeLoad('#main-content', 'home.html');
                }, 200);
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
                    } else if(response.status === 401){
                        $('#result').html(response.responseText);
                    } else if(response.status === 404){
                        $('#result').html(response.responseText);
                    } else {
                        $('#result').html('An unexpected error occurred.');
                    }
            }
        });
    });
}