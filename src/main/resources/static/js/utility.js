
export  function fadeLoad(selector, pageUrl, fadeDuration = 300, onComplete = null) {
    const $target = $(selector);

    $target.addClass('fade-out');

    setTimeout(() => {
        $target.load(pageUrl, function (response, status, xhr) {
            if (status === "error") {
                $target.html('Sorry, content could not be loaded.');
            }

            setTimeout(() => {
                $target.removeClass('fade-out');
                if (typeof onComplete === 'function') {
                    onComplete();
                }
                
                if (typeof runPageScript === 'function') {
                    runPageScript(pageUrl);
                }

            }, fadeDuration);
        });
    }, fadeDuration);
}

export function setupNavigation(selector = '#main-content') {
    $(document).on('click', 'a[data-spa]', function (e) {
        const pageUrl = $(this).attr('href');
        if (!pageUrl || pageUrl === '#') return;
        e.preventDefault();
        if (pageUrl === "/logout") {
            $.ajax({
                url: '/logout',
                method: 'GET',
                dataType: 'json',
                success: function(response) {
                    updateNavbarVisibility();
                    fadeLoad(selector, response.redirect || 'home.html');
                },
                error: function() {
                    alert('Logout failed.');
                }
            });
        } else {
            fadeLoad('#main-content', pageUrl);
        }
    });
}


export function updateNavbarVisibility() {
    $.ajax({
        url: '/api/session',
        method: 'GET',
        dataType: 'json',
        success: function(user) {
            $('.auth-only').addClass('visible');
            $('.guest-only').removeClass('visible');
            $('#navbar-user-label').text(`Welcome, ${user.firstName}!`);
        },
        error: function(xhr, status, error) {
            $('.auth-only').removeClass('visible');
            $('.guest-only').addClass('visible');
            $('#navbar-user-label').text('Welcome, [guest]!');
        }
    });
}