/*!
Deck JS - deck.notes
Copyright (c) 2011 Remi BARRAQUAND
Dual licensed under the MIT license and GPL license.
https://github.com/imakewebthings/deck.js/blob/master/MIT-license.txt
https://github.com/imakewebthings/deck.js/blob/master/GPL-license.txt
*/

/*
This module adds the methods and key binding to show and hide speaker notes.
To better use this module consider the "deck.clone.js" module which allows to 
clone a deck presentation and displays into a popup window. That way you can
only toggle the notes panel for this cloned window.
*/
(function($, deck, undefined) {
    var $d = $(document);
    var $notesContainer;
    
    /*
	Extends defaults/options.
	
        options.classes.notes
		This class is added to the deck container when showing the slide
                notes.
	
	options.keys.notes
		The numeric keycode used to toggle between showing and hiding 
                the slide notes.
	*/
    $.extend(true, $[deck].defaults, {
        classes: {
            notes: 'deck-notes',
            notesContainer: 'deck-notes-container'
        },
		
        keys: {
            notes: 78 // n
        },
                
        selectors: {
            // no selector
        }
    });

    /*
	jQuery.deck('showNotes')
	
	Shows the slide notes by adding the class specified by the toc class option
	to the deck container.
	*/
    $[deck]('extend', 'showNotes', function() {
        $("."+$[deck]('getOptions').classes.notes).show();
    });
    
    /*
	jQuery.deck('hideNotes')
	
	Hides the slide notes by removing the class specified by the toc class
	option from the deck container.
	*/
    $[deck]('extend', 'hideNotes', function() {
        $("."+$[deck]('getOptions').classes.notes).hide();
    });

    /*
	jQuery.deck('toggleNotes')
	
	Toggles between showing and hiding the notes.
	*/
    $[deck]('extend', 'toggleNotes', function() {
        $("."+$[deck]('getOptions').classes.notes).is(":visible") ? $[deck]('hideNotes') : $[deck]('showNotes');
    });


    /*
        jQuery.deck('Init')
        */
    $d.bind('deck.init', function() {
        var opts = $[deck]('getOptions');
        var container = $[deck]('getContainer');
        
        /* Bind key events */
        $d.unbind('keydown.decknotes').bind('keydown.decknotes', function(e) {
            if (e.which === opts.keys.notes || $.inArray(e.which, opts.keys.notes) > -1) {
                $[deck]('toggleNotes');
                e.preventDefault();
            }
        });
    })
    .bind('deck.change', function(e, from, to) {
        var slideTo = $[deck]('getSlide', to);
        
        /* Update notes */
        if( slideTo.children(".notes").length > 0) {
            $("."+$[deck]('getOptions').classes.notesContainer).html(slideTo.find(".notes").html())
        } else {
            $("."+$[deck]('getOptions').classes.notesContainer).html("")
        }
    });
})(jQuery, 'deck');