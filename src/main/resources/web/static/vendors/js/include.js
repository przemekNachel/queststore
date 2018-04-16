$(function(){
    var includes = $('[data-include]');
    jQuery.each(includes, function(){
        var file = 'resources/views/' + $(this).data('include') + '.html';
        $(this).load(file);
    });
});
