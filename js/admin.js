function search(a,b) {
    document.location = '/admin/search?q=' + $('.z-search-q').val();
    return false;
}

function typeaheadSearch() {
    var map = {};
    var $input = $('.z-search-q');
    $input.typeahead({
        minLength: 2,
        autoSelect: true,
        items: 16,
        source: function (q, process) {
            return $.get('/admin/suggest', { q: q }, function (data) {
                syms = [];
                $.each(data.data, function (i, s) {
                    key = s.id + ' - ' + s.name
                    syms.push(key);
                    map[key] = s.id;
                });
                return process(syms);
            });
        },
        updater: function(q) {
            if (map[q]) q = map[q]
            document.location = '/admin/search?q=' + q
        }
    }); 
}

$(function() {
    $('.z-form-search').submit(search);
    $('.z-btn-search').click(search);
    typeaheadSearch();
});
