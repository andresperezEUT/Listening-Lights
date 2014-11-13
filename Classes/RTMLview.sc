RTMLview {

	var window;
	var bounds;

	var optionsView;
	var elementsView;
	var dspsView, trackersView;
	var elementsViewSize = 100;
	var elementsViewMargin = 50;
	var elementsViewGap = 0;
	var drawView;
	var canvasView;

	*new{
		^super.new.init;
	}

	init {

		GUI.qt;

		bounds = Window.availableBounds;
		window = Window.new("RTML",bounds,resizable:false).front;

		//----------------------------------------------------------------
		// window layout

		// options bar
		optionsView = View(window,Rect(0,0,bounds.width,bounds.height/10));
		optionsView.background = Color.grey(0.6);

		drawView = View(window,Rect(0,bounds.height/10,bounds.width,bounds.height*9/10));
		drawView.background = Color.grey(0.4);

		// items bar
		elementsView = View(drawView,Rect(0,0,bounds.width,drawView.bounds.height/8));
		elementsView.background = Color.blue;

		dspsView = View(elementsView,Rect(0,0,bounds.width,elementsView.bounds.height/2));
		dspsView.background = Color.red;


		trackersView = View(elementsView,Rect(0,elementsView.bounds.height/2,bounds.width,elementsView.bounds.height/2));
		trackersView.background = Color.cyan;

		// canvas
		canvasView = UserView(drawView,Rect(0,drawView.bounds.height/8,bounds.width,drawView.bounds.height*7/8));
		canvasView.background = Color.grey(0.9);



		//----------------------------------------------------------------
		// elements
		dspsView.addFlowLayout(margin:elementsViewMargin@0,gap:elementsViewGap@0);
		RTMLelement.dsps.do { |e|
			var string = e.asString;
			StaticText(dspsView,Rect(0,0,elementsViewSize,dspsView.bounds.height)).string_(string);
		};

		trackersView.addFlowLayout(margin:elementsViewMargin@0,gap:elementsViewGap@0);
		RTMLelement.trackers.do { |e|
			var string = e.asString;
			StaticText(trackersView,Rect(0,0,elementsViewSize,trackersView.bounds.height)).string_(string);
		};



		//----------------------------------------------------------------
		// actions
		dspsView.mouseDownAction = { |view, x, y, modifiers, buttonNumber, clickCount|
			var dspIndex = (x - (elementsViewMargin) / elementsViewSize).floor;
			[dspIndex].postln;
		};

		trackersView.mouseDownAction = { |view, x, y, modifiers, buttonNumber, clickCount|
			var trackerIndex = (x - (elementsViewMargin) / elementsViewSize).floor;
			[trackerIndex].postln;
		};

		drawView.mouseUpAction = { |view, x, y, modifiers|
			[x,y].postln;
		};

		canvasView.mouseUpAction = { |view, x, y, modifiers|
			[x,y].postln;
		}

	}

}
