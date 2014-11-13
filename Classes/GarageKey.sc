// Controller Class for
// Garagekey groove-Garagekey groove MIDI 1

GarageKey {
	var <pads,<knobs,<faders;
	var <padActions,<knobActions,<faderActions,<keyOnAction,<keyOffAction;

	*new {
		^super.new.init;
	}

	init {

		//////////////// CONNECT ///////////////////
		var sources;
		var uid;

		MIDIClient.init;
		sources=MIDIClient.sources;

		if (sources.select{|d|d.device=="Garagekey groove-Garagekey groove MIDI 1"}.size!=0) {
			"Device connected".postln;
			sources.do{|d|if (d.device=="Garagekey groove-Garagekey groove MIDI 1") {uid=d.uid}};
			MIDIIn.connect(0,uid);
			pads=Dictionary.newFrom([48,1,45,2,49,3,51,4,36,5,38,6,42,7,46,8]);
			knobs=Dictionary.newFrom([9,1,10,2,11,3,12,4]);
			faders=Dictionary.newFrom([7,1,43,2,37,3,72,4]);
		} {
			"Device not connected".error
		};

		////////////// SET UP RESPONDERS ///////////////

		padActions=Array.newClear(8);
		knobActions=Array.newClear(4);
		faderActions=Array.newClear(4);

		MIDIFunc.noteOn({ |val,num,chan,src|
			//pads
			//[val,num,chan].postln;
			if (chan==9) {
				if (pads.at(num).isNil.not) {
					padActions[pads.at(num)-1].value(val);
					// ["pad"++pads.at(num),val].postln;
				};
			};
			//notes
			if (chan==0) {
				keyOnAction.value(num,val);
				/*				["note"++num,val].postln;*/
			}
		});

		MIDIFunc.noteOff({|val,num,chan,src|
			//notes
			if (chan==0) {
				keyOffAction.value(num,val);
				/*				["note"++num,val].postln;*/
			};
		});

		MIDIFunc.cc({|val,num|
			//knobs
			if (knobs.at(num).isNil.not) {
				knobActions[knobs.at(num)-1].value(val);
				// ["knob"++knobs.at(num),val].postln;
			};
			//faders
			if (faders.at(num).isNil.not) {
				faderActions[faders.at(num)-1].value(val);
				// ["fader"++faders.at(num),val].postln;
			};


		});

	}

	addAction { |type,num,action|
		switch(type)
		{\keyOn}   {keyOnAction=action}
		{\keyOff}   {keyOffAction=action}
		{\fader} {faderActions.put(num-1,action)}
		{\knob}  {knobActions.put(num-1,action)}
		{\pad}   {padActions.put(num-1,action)};
	}

	// cc { |argFunc|
	// 	MIDIFunc.cc
	// }




}
