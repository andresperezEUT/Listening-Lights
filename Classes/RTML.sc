RTML {

	classvar <server;
	classvar <oscReceiver;
	classvar <>verbose = true;

	classvar <elements;
	classvar <numElements = 0;
	classvar <nodeIDs;

	classvar <numOnsetDetector = 0;
	classvar <numPitchFollower = 0;
	classvar <numBeatTracker = 0;
	classvar <numPeakTracker = 0;
	classvar <numEq = 0;
	classvar numKeyTracker = 0;
	classvar numChromaTracker = 0;
	classvar numSpectralTracker = 0;
	classvar numMfccTracker = 0;
	classvar numFftTracker = 0;

	classvar <numMfcc = 20; // not possible to set it as a SynthDef arg...

	classvar <destAddr; // osc destination NetAddr

	//classvar defaultDestIP = "127.0.0.1"; // this
	//classvar defaultDestIP = "172.31.13.255"; // fabraicoats broadcast
	classvar defaultDestIP = "255.255.255.255";

	/*classvar defaultDestPort = 7770; // 7770 qlcplus*/
	//classvar defaultDestPort = 12000; // 12000 processing
	classvar defaultDestPort = 57120;

	classvar eqSetting;

	classvar <view;

	classvar <dsps;
	classvar <trackers;
	classvar <numDsps;
	classvar <numTrackers;

	classvar <numInputBuses;

	classvar <elementsByBus;

	*init { |myServer,myDestAddr|

		server = myServer;
		server.reboot;
		// TODO: desconectar system ins...

		// num input audio buses
		numInputBuses = server.options.numInputBusChannels;

		elements = Dictionary.new; // (elementName -> element)
		nodeIDs = Dictionary.new; // (elementName -> element.synth.nodeID)

		elementsByBus = Array.fill(numInputBuses,{List.new}); // array with lists containing elementName list

		NetAddr.broadcastFlag_(true);
		destAddr = myDestAddr ? NetAddr(defaultDestIP,defaultDestPort);

		// default equalizer settings
		eqSetting = EQSetting( EQdef( 'peak', BPeakEQ, 'gain', Gain),[[1000,0.5,12],[-6]]);

		// get element lists
		dsps = RTMLdsp.allSubclasses.select(_.abstract.not);
		trackers = RTMLtracker.allSubclasses.select(_.abstract.not);

		// keep the number of instances of each element type
		numDsps = Array.fill(dsps.size,{0});
		numTrackers = Array.fill(trackers.size,{0});



		// create view
		RTMLparameter.init;
		view = RTMLview.new;

		// osc receivers from server


		////////////////////////////// BORRAR /////////////////


		/*		~w=Window.new("rtml",Window.availableBounds).front;
		~width = Window.availableBounds.width/3;
		~height = Window.availableBounds.height/3;
		~w.view.decorator=FlowLayout(~w.bounds,0@0,0@0);


		//onset
		~onsetCom = CompositeView(~w,Rect(0,0,~width,~height)).background_(Color.rand);
		~onsetCom.addFlowLayout(0@0,0@0);
		//~onsetCom.decorator = FlowLayout(~onsetCom,0@0,0@0);
		StaticText(~onsetCom,~width@50).string_("ONSET DETECTOR");
		~onsetButton=Button.new(~onsetCom,~width@50).states_([["",Color.black,Color.black],["",Color.red,Color.red]]);

		//beat
		~beatCom = CompositeView(~w,Rect(0,0,~width,~height)).background_(Color.rand);
		~beatCom.addFlowLayout(0@0,0@0);
		//~onsetCom.decorator = FlowLayout(~onsetCom,0@0,0@0);
		StaticText(~beatCom,~width@50).string_("BEAT TRACKER");
		~beatButton1=Button.new(~beatCom,~width@50).states_([["",Color.black,Color.black],["",Color.red,Color.red]]);
		~beatButton2=Button.new(~beatCom,~width@50).states_([["",Color.black,Color.black],["",Color.red,Color.red]]);
		~beatNum = NumberBox(~beatCom,~width@50);

		//pitch
		~pitchCom = CompositeView(~w,Rect(0,0,~width,~height)).background_(Color.rand);
		~pitchCom.addFlowLayout(0@0,0@0);
		StaticText(~pitchCom,~width@50).string_("MONO PITCH");
		~pitchSlider=Slider.new(~pitchCom,~width@50);
		~pitchNum = NumberBox(~pitchCom,~width@50);

		//peak
		~peakCom = CompositeView(~w,Rect(0,0,~width,~height)).background_(Color.rand);
		~peakCom.addFlowLayout(0@0,0@0);
		StaticText(~peakCom,~width@50).string_("PEAK VOLUME");
		~peakSlider=Slider.new(~peakCom,~width@50);
		~peakNum = NumberBox(~peakCom,~width@50);

		//fft
		~fftCom = CompositeView(~w,Rect(0,0,~width,~height)).background_(Color.rand);
		~fftCom.addFlowLayout(0@0,0@0);
		StaticText(~fftCom,~width@50).string_("FFT");
		~fftSlider=MultiSliderView.new(~fftCom,~width@(~height-50));
		~fftSlider.size_(128);
		~fftSlider.elasticMode_(1);

		//mfcc
		~mfccCom = CompositeView(~w,Rect(0,0,~width,~height)).background_(Color.rand);
		~mfccCom.addFlowLayout(0@0,0@0);
		StaticText(~mfccCom,~width@50).string_("MFCC");
		~mfccSlider=MultiSliderView.new(~mfccCom,~width@(~height-50));
		~mfccSlider.size_(20);
		~mfccSlider.elasticMode_(1);

		//spectral
		~spectraCom = CompositeView(~w,Rect(0,0,~width,~height)).background_(Color.rand);
		~spectraCom.addFlowLayout(0@0,0@0);
		StaticText(~spectraCom,~width@50).string_("SPECTRAL");
		~spectraSlider1=Slider.new(~spectraCom,(~width/2.1)@50);
		~spectraNum1 = NumberBox(~spectraCom,(~width/2.1)@50);
		~spectraSlider2=Slider.new(~spectraCom,(~width/2.1)@50);
		~spectraNum2 = NumberBox(~spectraCom,(~width/2.1)@50);
		~spectraSlider3=Slider.new(~spectraCom,(~width/2.1)@50);
		~spectraNum3 = NumberBox(~spectraCom,(~width/2.1)@50);

		//chroma
		~chromaCom = CompositeView(~w,Rect(0,0,~width,~height)).background_(Color.rand);
		~chromaCom.addFlowLayout(0@0,0@0);
		StaticText(~chromaCom,~width@50).string_("CHROMAGRAM");
		~chromaSlider=MultiSliderView.new(~chromaCom,~width@(~height-60));
		~chromaSlider.size_(12);
		~chromaSlider.elasticMode_(1);

		// key
		~keyCom = CompositeView(~w,Rect(0,0,~width,~height)).background_(Color.rand);
		~keyCom.addFlowLayout(0@0,0@0);
		StaticText(~keyCom,~width@50).string_("KEY ESTIMATION");
		~keySlider=Slider.new(~keyCom,~width@50);
		~keyNum = StaticText(~keyCom,~width@50);*/


		//~i=0;

		// TODO: oscReceiver from /b_setn for the FFTsynths
		// then we avoid duplicate messages

		oscReceiver = OSCFunc({ arg msg, time;
			var nodeID = msg[1];
			var element = this.getElementFromNodeID(nodeID);

			if (element.isNil.not) { //avoid error when reset BeatTracker

				element.oscReceiverFunction(msg);

/*				switch (element.class)

				{PeakTracker} {
					//msg.postln;
					~i=~i+1;
					~l=msg[3];
					if ((~i%5) == 0) {
						msg.postln;
						NetAddr.localAddr.sendMsg("/rtml/peakTracker0",msg[3]);
						~v=msg[3];
					}*/
/*					Task({
						inf.do({ |i|
							if ((i%10) == 0) {
								msg.postln;

							}
						})*/
			// }).start;
				//};
/*					element.buffer.getToFloatArray(wait:0.01,action:{ |array|
						// array.maxIndex.postln;

						// TODO: HACER ESTO BIEN!!!

						// ~c = array.maxIndex.linlin(0,11,2,4).round.postln;
						~c = (array.maxIndex % 3) + 2;
						~c.postln;
						// array is already normalized by synthdef definition

						// TODO: send osc


						/////
						/*						Task({
						~chromaSlider.value_(array);
						}).play(AppClock);*/
					});
				};*/
				/*switch (element.class)

				{OnsetDetector} {
				if (element.testSound) {Synth(\click)};

				if (element.send) {element.sendMsg};

				/////
				/*					Task({
				~onsetButton.value_(1);
				0.2.wait;
				~onsetButton.value_(0);
				}).play(AppClock);*/
				}


				{BeatTracker} {
				var beatType = msg[2]; // 0:beats, 1:subbeats
				var tempo = msg[3];
				if (element.testSound) {
				switch(beatType)
				{0} {Synth(\click,[\freq,1500])}
				{1} {Synth(\click,[\freq,1000])};
				};

				if (element.send) {element.sendMsg};


				//
				/*					Task({
				if (beatType==0) {
				~beatButton1.value_(1);
				0.2.wait;
				~beatButton1.value_(0);
				} {
				~beatButton2.value_(1);
				0.2.wait;
				~beatButton2.value_(0);
				};
				~beatNum.value_(tempo*60);
				}).play(AppClock);*/
				}


				{PitchFollower} {
				var freq = msg[3];

				if (element.send) {element.sendMsg(freq)};

				/*					switch (element.mode)
				{\height} {
				//linear in height
				var newFreq = freq.clip(element.parameters[\minFreq],element.parameters[\maxFreq]);
				newFreq = newFreq - 40;
				newFreq = (newFreq / 3960 * 255).round;
				if (element.send) {element.sendMsg(newFreq)};
				}
				{\pitch1} {
				// 0->lower, 11->higher (linear in pitch)
				var pitch = ((freq.cpsmidi%12).round.wrap(0,11));
				pitch = (pitch/11*255);
				if (element.send) {element.sendMsg(pitch)};
				}
				{\pitch2} {
				// 0->lower, 6->higher, 12->lower (circular in pitch)
				var pitch = ((freq.cpsmidi%12).round.wrap(0,11));
				pitch = (pitch/11*255*2).fold(0,255);
				if (element.send) {element.sendMsg(pitch)};
				};*/

				if (element.testSound) {
				element.toneSynth.set(\freq,freq)
				};


				/////
				/*	Task({
				~pitchSlider.value_(freq.linlin(20,1000,0,1));
				~pitchNum.value_(freq);
				}).play(AppClock);*/
				}


				{PeakTracker} {
				var peak = msg[3];
				peak = (peak*255).round;
				if (element.send) {element.sendMsg(peak)};

				/////
				/*					Task({
				~peakSlider.value_(peak/255);
				~peakNum.value_(peak/255);
				}).play(AppClock);*/
				}


				{FFTTracker} {
				element.buffer.getToFloatArray(wait:0.01,action:{ |array|
				//array = array/1024;
				//array = array.normalizeSum;
				// array.postln;
				var sum = array.sum;
				array = array/sum;

				//take 1 out of 16
				array = array.select({|v,i| i%16 == 0});

				array = array.collect({|v|(20*log10(v)).linlin(-100,0,0,1)});

				// TODO: send osc

				/*						Task({
				~fftSlider.value_(array);
				}).play(AppClock);*/
				});
				}


				{MFCCTracker} {
				element.buffer.getToFloatArray(wait:0.01,action:{ |array|
				//array = array/20; //norm
				//array.postln;
				// TODO: send osc

				/////
				/*						Task({
				//inf.do{
				//0.1.wait;
				//array.postln;
				~mfccSlider.value_(array);
				//}
				}).play(AppClock);*/
				});
				}


				{SpectralTracker} {
				var type = msg[2];
				var value = msg[3];
				// "spectral".postln;
				//
				/*					Task({
				switch (type)
				{0} { // centroid
				~spectraSlider1.value_(value.linlin(0,20000,0,1));
				~spectraNum1.value_(value);
				}
				{1} { // flatness
				~spectraSlider2.value_(value);
				~spectraNum2.value_(value);
				}
				{2} { // percentile
				~spectraSlider3.value_(value.linlin(0,20000,0,1));
				~spectraNum3.value_(value);
				};
				}).play(AppClock);*/
				// TODO: send osc
				}


				{ChromaTracker} {
				element.buffer.getToFloatArray(wait:0.01,action:{ |array|
				// array.maxIndex.postln;

				// TODO: HACER ESTO BIEN!!!

				// ~c = array.maxIndex.linlin(0,11,2,4).round.postln;
				~c = (array.maxIndex % 3) + 2;
				// array is already normalized by synthdef definition

				// TODO: send osc


				/////
				/*						Task({
				~chromaSlider.value_(array);
				}).play(AppClock);*/
				});
				}


				{KeyTracker} {
				var key = msg[3];

				var dict = Dictionary.newFrom([0,'C',1,'C#',2,'D',3,'D#',4,'E',5,'F',6,'F#',7,'G',8,'G#',9,'A',10,'A#',11,'B',12,'c',13,'c#',14,'d',15,'d#',16,'e',17,'f',18,'f#',19,'g',20,'g#',21,'a',22,'a#',23,'b']);


				if (element.send) {element.sendMsg(key)};


				// "key".postln;
				/*					Task({
				~keySlider.value_(key.linlin(0,23,0,1));
				~keyNum.string_(dict[key]);
				}).play(AppClock);*/

				}

				;*/



				if (verbose) {
					msg.postln;
				};
			}
		},'/tr', server.addr); //receive only from localhost server


		// load synthdefs
		this.loadSynthDefs;

	}

	*getElementFromNodeID { |nodeID|
		var synthName = nodeIDs.findKeyForValue(nodeID);
		var element = elements.at(synthName);
		^element;
	}

	*removeElement { |element|
		// element is the instance
		var name = element.name;
		var elementIndex;
		var numElements;

		var channel = element.get(\channel);

		// free the synth
		element.synth.free;

		// get element class index inside trackers/dsps array
		// decrease count of element instances
		if (element.class.isKindOfClass(RTMLtracker)) {
			elementIndex = trackers.indexOf(element.class);
			numElements= numTrackers.at(elementIndex);
			//numTrackers.put(elementIndex,numElements - 1);
		} {
			elementIndex = dsps.indexOf(element.class);
			numElements= numDsps.at(elementIndex);
			//numDsps.put(elementIndex,numElements - 1);
		};

		// remove the link to then element
		elements.removeAt(name.asSymbol);
		nodeIDs.removeAt(name.asSymbol);
		numElements = numElements-1;

		// add to by-channel classification
		elementsByBus.at(channel).remove(name.asSymbol);
	}


	*addElement { |element, channel|
		var name = element.class.synthName;

		var elementIndex;
		var numElements;

		// get element class index inside trackers/dsps array
		// increase count of element instances
		if (element.class.isKindOfClass(RTMLtracker)) {
			elementIndex = trackers.indexOf(element.class);
			numElements= numTrackers.at(elementIndex);
			numTrackers.put(elementIndex,numElements + 1);
		} {
			elementIndex = dsps.indexOf(element.class);
			numElements= numDsps.at(elementIndex);
			numDsps.put(elementIndex,numElements + 1);
		};

		// add to internal element storage
		name = element.class.synthName ++ numElements;


		// TODO: ADD PEAK EQ


		elements.add(name.asSymbol -> element);
		nodeIDs.add(name.asSymbol -> element.nodeID);
		numElements = numElements+1;

		// add to by-channel classification
		elementsByBus.at(channel).add(name.asSymbol);


		// PUT OTHER CLASSES

		^name.asSymbol;
	}

	*cloneElement { |element,channel|

		// create new element from same type
		var name = element.class.new(channel);

		// get instance
		var instance = RTML.elements.at(name);

		// copy current parameters
		element.parameters.keysValuesDo { |key, value|
			instance.set(key,value);
		};

		^name;
	}

	///// RESET /////////

	*reset {
		// free all synths
		elements.do(_.free);

		// remove all existing elements
		elements = Dictionary.new; // (elementName -> element)
		nodeIDs = Dictionary.new; // (elementName -> element.synth.nodeID)
		elementsByBus = Array.fill(numInputBuses,{List.new}); // array with lists containing elementName list
		numElements = 0;
		numDsps = Array.fill(dsps.size,{0});
		numTrackers = Array.fill(trackers.size,{0});

		// reset view
		this.view.reset;

	}

	//////// CLOSE ////

	*close {

		// free all synths
		elements.do(_.free);

		// close window
		// this.view.window.close;

		// quit server
		server.quit;

	}


	////////////////// SAVE ////////////////////

	*save {
		Dialog.savePanel({ |path|
			this.saveAction(path);
		})
	}


	*saveAction { |path|
		var d, rtml;
		var elements;
		var file;

		// create document
		d = DOMDocument.new;

		// create root element "rtml"
		rtml = d.createElement("rtml");
		d.appendChild(rtml);

		// define elements
		elements = d.createElement("elements");
		rtml.appendChild(elements);

		RTML.elements.do { |element|
			var e = d.createElement("element");
			e.setAttribute( "type", element.class.asString );
			// parameters
			element.parameters.keysValuesDo { |key, value|
				e.setAttribute( key, value.asString);
				elements.appendChild(e);
			};
		};

		file = File(path, "w");
		d.write(file);
		file.close;
	}


	*load {
		Dialog.openPanel({ |path|
			this.loadAction(path);
		})
	}

	*loadAction { |path|
		var filePath, file;
		var document;
		var xmlContent;
		var elements, e;

		this.reset;

		filePath = path;
		file = File(filePath,"r");
		document = DOMDocument.new;

		xmlContent = String.readNew(file);
		document.parseXML(xmlContent); // parses from string
		file.close;

		elements = document.getDocumentElement.getElement("elements");
		e = elements.getFirstChild;
		while ( { e != nil }, {
			// get class and channel
			var type = e.getAttribute("type").asSymbol.asClass;
			var channel = e.getAttribute("channel").asInteger;
			// instanciate element
			var name = RTML.view.addElement(type,channel);
			var instance = RTML.elements.at(name);

			// set parameter values: iterate all parameters
			instance.parameters.keysValuesDo { |key,value|
				// get values for each parameter
				var v = e.getAttribute(key.asString);
				// casting
				var parType = RTMLparameter.get(key,\valueType);
				[v,key,parType].postln;

				switch (parType)
				{Integer} {v = v.asInt}
				{Float} {v = v.asFloat}
				{Array} {

					var list = RTMLparameter.get(key,\valueList).postln;
					// check type of list elements
					var vType = list[0].class;
					switch(vType)
					{Integer} {v = v.asInt}
					{Symbol} {v = v.asSymbol};
				};

				// set into instance
				instance.set(key,v);


			};

			e = e.getNextSibling;
		} );

	}

}
