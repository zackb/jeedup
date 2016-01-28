function search(a,b) {
    document.location = '/admin/search?q=' + $('.z-search-q').val();
    return false;
}

$(document).ready(function() {
    $('.z-form-search ').submit(search);
    $('.z-btn-search ').click(search);
});
