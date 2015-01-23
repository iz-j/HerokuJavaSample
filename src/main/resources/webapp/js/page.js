$(function(){
  $('#post').click(function() {
    var comment = $('#comment').val();
    if (!comment) {
      alert('コメントを入力してください。');
      return;
    }
    
    $(this).button('loading');
    $.ajax({
      url : '/servlet',
      type: "POST",
      data: {comment : comment}
    }).done(function() {
      location.reload();
    });
  });
});