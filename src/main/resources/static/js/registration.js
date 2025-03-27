
export function initRegistrationForm(){
    $('#regform').on('submit', function(e) {
        e.preventDefault();

        var formData = {
            firstName: $('#firstname').val(),
            lastName: $('#lastname').val(),
            email: $('#email').val(),
            password: $('#password').val()
        };

        console.log(formData);
        $.ajax({
            type: 'POST',
            url: '/registration',
            data: JSON.stringify(formData),
            contentType: 'application/json',
            success: function(response) {
                $('#result').html(response);
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
                    } else if(response.status === 409) {
                        $('#result').html(response.responseText);
                    } else {

                        $('#result').html('An unexpected error occurred.');
                    }
            }
        });
    });
}