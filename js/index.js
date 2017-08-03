var deviceWidth = $(window).width();
var isMobile = deviceWidth < 900;

if (isMobile) {
  $('.hook-container').insertBefore($('.phone-container'));
  $('.download').text('PlayStore');
}

// Video stuff
var tag = document.createElement('script');
tag.src = "https://www.youtube.com/iframe_api";
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

var controlsState;
if (isMobile) {
  controlsState = 1;
} else {
  controlsState = 0;
}
var player;
function onYouTubeIframeAPIReady() {
  player = new YT.Player('player', {
    height: '100%',
    width: '100%',
    playerVars: { 'controls': controlsState, 'showinfo': 0 },
    videoId: 'baZWeNArmLg',
    events: {
      'onReady': onPlayerReady
    }
  });
}

function onPlayerReady(e) {
  e.target.setPlaybackQuality("hd720");
  e.target.mute();
  e.target.playVideo();
  e.target.pauseVideo();
  e.target.unMute();
  e.target.seekTo(0);
}

function playVideo() {
  player.playVideo();
}

function stopVideo() {
  player.stopVideo();
}

// Play button click trigger
$('.play-button').click(function() {

  $('#player').show();
  playVideo();
});

// $('hr').click(function() {
//   stopVideo();
//   $('#player').hide();
// });

// Play button hover trigger
var $playButtonOuter = $('#promo .outer');
$('.play-button').hover(
  function() {
    $($playButtonOuter).addClass('whoop');
  }, function() {
    $($playButtonOuter).removeClass('whoop');
  }
);