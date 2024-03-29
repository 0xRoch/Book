$(function(){
  App.init();
});

var page = 0;
var App = {
  playlist: [],

  init: function() {
    App.admin('body'),
    App.lazyLoad(),
    App.pageFlip(),
    App.audio()
  },

  notify: function(msg) {
      $('#admin').append('<div id="notification" class="span4 alert alert-success">'+msg+'</div>');
      setTimeout(function() {
          $('#notification').fadeOut('fast');
          $('#notification').remove();
      }, 5000);
  },

  highlight: function(page, sentence) {
    $('.highlight:visible').each(function() {
        $(this).hide();
    });
    elem = $('#page-'+page).children('#sentence-'+sentence).children('.highlight');
    elem.fadeIn('fast');
  },

  upload: function(elem) {
    $(elem).ajaxForm({
        success:App.notify("Your file has been uploaded")
    }).submit();
  },

  loadAudio: function(page) {
      $('#page-'+page+' .sentence').each(function(a) {
          App.playlist.add({
            mp3: $(this).data('audio')
          });
      });
  },

  lazyLoad: function() {
    $('.lazyLoad').each(function(a) {
        $(this).removeClass('lazyLoad');
        var href = $(this).data('url');
        $(this).load(href, function(XMLHttpRequest) {
            $(this).children('#page-0 .sentence').each(function(a) {
                App.playlist.add({
                  mp3: $(this).data('audio')
                });
            });
            App.lazyLoad();
        });
    });
  },

  admin: function(elem) {
    $(elem+' .adminAction').each(function(a) {
      var href = $(this).attr('href');
      $(this).click( function(e) {
          e.preventDefault();
          $('#content').load(href+' #admin-container', function(XMLHttpRequest) {
            App.admin('#content')
          });
      });
    });
    $(elem+' .adminPage').each(function(a) {
        var href = $(this).attr('href');
        $(this).click( function(e) {
          e.preventDefault();
          $('#page-container').load(href, function(XMLHttpRequest) {
              App.admin('#page-container')
          });
      });
    });
    $(elem+' .adminCallback').each(function(a) {
        var href = $(this).data('callback');
        $(this).ajaxForm({
            success:function() {
                $('#page-container').load(href, function(XMLHttpRequest) {
                    App.admin('#page-container')
                    App.notify("Modification saved")
                });
            }
        });
    });
  },

  pageFlip: function() {
	// Dimensions of the whole book
	var BOOK_WIDTH = 988;
	var BOOK_HEIGHT = 550;
	  	
	// Dimensions of one page in the book
	var PAGE_WIDTH = 450;
	var PAGE_HEIGHT = 510;
	
	// Vertical spacing between the top edge of the book and the papers
  	var PAGE_Y = ( BOOK_HEIGHT - PAGE_HEIGHT ) / 2;
	
  	// The canvas size equals to the book dimensions + this padding
  	var CANVAS_PADDING = 60;
  	
  	var book = document.getElementById("book");

    if (book != undefined) {

        var canvas = document.createElement('canvas');
        canvas.setAttribute("id", "pages-canvas");
        book.appendChild(canvas);

        var G_vmlCanvasManager;
        if (typeof G_vmlCanvasManager != 'undefined') { // ie IE
            G_vmlCanvasManager.initElement(canvas);
        }

        if (canvas.getContext) {
            var context = canvas.getContext( "2d" );
        }

        var mouse = { x: 0, y: 0 };

        var flips = [];

        // List of all the page elements in the DOM
        var pages = book.getElementsByTagName( "section" );

        // Organize the depth of our pages and create the flip definitions
        for( var i = 0, len = pages.length; i < len; i++ ) {
            pages[i].style.zIndex = len - i;

            flips.push( {
                // Current progress of the flip (left -1 to right +1)
                progress: 1,
                // The target value towards which progress is always moving
                target: 1,
                // The page DOM element related to this flip
                page: pages[i],
                // True while the page is being dragged
                dragging: false
            } );
        }

        // Resize the canvas to match the book size
        canvas.width = BOOK_WIDTH + ( CANVAS_PADDING * 2 );
        canvas.height = BOOK_HEIGHT + ( CANVAS_PADDING * 2 );

        // Offset the canvas so that it's padding is evenly spread around the book
        canvas.style.top = -CANVAS_PADDING + "px";
        canvas.style.left = -CANVAS_PADDING + "px";

        // Render the page flip 60 times a second
        setInterval( render, 1000 / 60 );

        // for IE compatability
        if (!document.addEventListener) {
            document.attachEvent("mousemove", mouseMoveHandler);
            document.attachEvent("mousedown", mouseDownHandler);
            document.attachEvent("mouseup", mouseUpHandler);
        }
        else {
            document.addEventListener( "mousemove", mouseMoveHandler, false );
            document.addEventListener( "mousedown", mouseDownHandler, false );
            document.addEventListener( "mouseup", mouseUpHandler, false );
        }

        $('.prevPage').click( function(e) {
          e.preventDefault();
          App.playlist.remove();
          flips[page].target = 1;
          page = Math.max( page - 1, 0 );
          flips[page].dragging = false;
          App.loadAudio(page);
        });

        $('.nextPage').click( function(e) {
          e.preventDefault();
          if (page + 1 < flips.length) {
              App.playlist.remove();
              flips[page].target = -1;
              page = Math.min( page + 1, flips.length );
              flips[page].dragging = false;
              App.loadAudio(page);
          }
        });

        $('.goTo').click( function(e) {
            e.preventDefault();
            App.playlist.remove();
            nextPage = $(this).data('page')-1;
            if (nextPage < page) {
                target = page - nextPage;
                for( var i = -1; i < target; i++ ) {
                    flips[page].target = 1;
                    page = Math.max( page - 1, 0 );
                    flips[page].dragging = false;
                }
            } else {
                target = nextPage - page;
                for( var i = 0; i < target; i++ ) {
                    flips[page].target = -1;
                    page = Math.min( page + 1, flips.length );
                    flips[page].dragging = false;
                }
            }
            App.loadAudio(page);
        });
    }

  	function mouseMoveHandler( event ) {
  		// Offset mouse position so that the top of the book spine is 0,0
  		mouse.x = event.clientX - book.offsetLeft - ( BOOK_WIDTH / 2 );
  		mouse.y = event.clientY - book.offsetTop;
  	}
  	
  	function mouseDownHandler( event ) {
  		// Make sure the mouse pointer is inside of the book
  		if (Math.abs(mouse.x) < PAGE_WIDTH) {
  			if (mouse.x < 0 && page - 1 >= 0) {
  				// We are on the left side, drag the previous page
  				flips[page - 1].dragging = true;
  			}
  			else if (mouse.x > 0 && page + 1 < flips.length) {
  				// We are on the right side, drag the current page
  				flips[page].dragging = true;
  			}
  		}
  		
  		// Prevents the text selection
  		event.preventDefault();
  	}
  	
  	function mouseUpHandler( event ) {
  		for( var i = 0; i < flips.length; i++ ) {
  			// If this flip was being dragged, animate to its destination
  			if( flips[i].dragging ) {
  				// Figure out which page we should navigate to
  				if( mouse.x < 0 ) {
  					flips[i].target = -1;
  					page = Math.min( page + 1, flips.length );
  				}
  				else {
  					flips[i].target = 1;
  					page = Math.max( page - 1, 0 );
  				}
  			}
  			
  			flips[i].dragging = false;
  		}
  	}
  	
  	function render() {

  		// Reset all pixels in the canvas
  		context.clearRect( 0, 0, canvas.width, canvas.height );
  		
  		for( var i = 0, len = flips.length; i < len; i++ ) {
  			var flip = flips[i];
  			
  			if( flip.dragging ) {
  				flip.target = Math.max( Math.min( mouse.x / PAGE_WIDTH, 1 ), -1 );
  			}
  			
  			// Ease progress towards the target value 
  			flip.progress += ( flip.target - flip.progress ) * 0.2;
  			
  			// If the flip is being dragged or is somewhere in the middle of the book, render it
  			if( flip.dragging || Math.abs( flip.progress ) < 0.997 ) {
  				drawFlip( flip );
  			}
  			
  		}
  		
  	}
  	
  	function drawFlip( flip ) {
  		// Strength of the fold is strongest in the middle of the book
  		var strength = 1 - Math.abs( flip.progress );
  		
  		// Width of the folded paper
  		var foldWidth = ( PAGE_WIDTH * 0.5 ) * ( 1 - flip.progress );
  		
  		// X position of the folded paper
  		var foldX = PAGE_WIDTH * flip.progress + foldWidth;
  		
  		// How far the page should outdent vertically due to perspective
  		var verticalOutdent = 20 * strength;
  		
  		// The maximum width of the left and right side shadows
  		var paperShadowWidth = ( PAGE_WIDTH * 0.5 ) * Math.max( Math.min( 1 - flip.progress, 0.5 ), 0 );
  		var rightShadowWidth = ( PAGE_WIDTH * 0.5 ) * Math.max( Math.min( strength, 0.5 ), 0 );
  		var leftShadowWidth = ( PAGE_WIDTH * 0.5 ) * Math.max( Math.min( strength, 0.5 ), 0 );
  		
  		
  		// Change page element width to match the x position of the fold
  		flip.page.style.width = Math.max(foldX, 0) + "px";
  		
  		context.save();
  		context.translate( CANVAS_PADDING + ( BOOK_WIDTH / 2 ), PAGE_Y + CANVAS_PADDING );
  		
  		
  		// Draw a sharp shadow on the left side of the page
  		context.strokeStyle = 'rgba(0,0,0,'+(0.05 * strength)+')';
  		context.lineWidth = 30 * strength;
  		context.beginPath();
  		context.moveTo(foldX - foldWidth, -verticalOutdent * 0.5);
  		context.lineTo(foldX - foldWidth, PAGE_HEIGHT + (verticalOutdent * 0.5));
  		context.stroke();
  		
  		
  		// Right side drop shadow
  		var rightShadowGradient = context.createLinearGradient(foldX, 0, foldX + rightShadowWidth, 0);
  		rightShadowGradient.addColorStop(0, 'rgba(0,0,0,'+(strength*0.2)+')');
  		rightShadowGradient.addColorStop(0.8, 'rgba(0,0,0,0.0)');
  		
  		context.fillStyle = rightShadowGradient;
  		context.beginPath();
  		context.moveTo(foldX, 0);
  		context.lineTo(foldX + rightShadowWidth, 0);
  		context.lineTo(foldX + rightShadowWidth, PAGE_HEIGHT);
  		context.lineTo(foldX, PAGE_HEIGHT);
  		context.fill();
  		
  		
  		// Left side drop shadow
  		var leftShadowGradient = context.createLinearGradient(foldX - foldWidth - leftShadowWidth, 0, foldX - foldWidth, 0);
  		leftShadowGradient.addColorStop(0, 'rgba(0,0,0,0.0)');
  		leftShadowGradient.addColorStop(1, 'rgba(0,0,0,'+(strength*0.15)+')');
  		
  		context.fillStyle = leftShadowGradient;
  		context.beginPath();
  		context.moveTo(foldX - foldWidth - leftShadowWidth, 0);
  		context.lineTo(foldX - foldWidth, 0);
  		context.lineTo(foldX - foldWidth, PAGE_HEIGHT);
  		context.lineTo(foldX - foldWidth - leftShadowWidth, PAGE_HEIGHT);
  		context.fill();
  		
  		
  		// Gradient applied to the folded paper (highlights & shadows)
  		var foldGradient = context.createLinearGradient(foldX - paperShadowWidth, 0, foldX, 0);
  		foldGradient.addColorStop(0.35, '#fafafa');
  		foldGradient.addColorStop(0.73, '#eeeeee');
  		foldGradient.addColorStop(0.9, '#fafafa');
  		foldGradient.addColorStop(1.0, '#e2e2e2');
  		
  		context.fillStyle = foldGradient;
  		context.strokeStyle = 'rgba(0,0,0,0.06)';
  		context.lineWidth = 0.5;
  		
  		// Draw the folded piece of paper
  		context.beginPath();
  		context.moveTo(foldX, 0);
  		context.lineTo(foldX, PAGE_HEIGHT);
  		context.quadraticCurveTo(foldX, PAGE_HEIGHT + (verticalOutdent * 2), foldX - foldWidth, PAGE_HEIGHT + verticalOutdent);
  		context.lineTo(foldX - foldWidth, -verticalOutdent);
  		context.quadraticCurveTo(foldX, -verticalOutdent * 2, foldX, 0);
  		
  		context.fill();
  		context.stroke();
  		
  		
  		context.restore();
  	}
  },

  audio: function() {
      App.playlist = new jPlayerPlaylist({
          jPlayer: "#audio",
          cssSelectorAncestor: "#audio-container"
      }, [], {
          swfPath: "/public/javascripts",
          supplied: "mp3",
          wmode: "window",
          play: function () {
            App.highlight(page, App.playlist.current);
          }
      });
  }
}