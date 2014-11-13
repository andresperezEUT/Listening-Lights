RTML {

	classvar server;
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

	classvar <destAddr; // osc destination NetAddr

	//classvar defaultDestIP = "127.0.0.1"; // this
	//classvar defaultDestIP = "172.31.13.255"; // fabraicoats broadcast
	classvar defaultDestIP = "255.255.255.255";

	/*classvar defaultDestPort = 7770; // 7770 qlcplus*/
	classvar defaultDestPort = 12000; // 12000 processing

	classvar eqSetting;

	classvar view;

	*init { |myServer,myDestAddr|

		server = myServer;
		server.reboot;
		// TODO: desconectar system ins...

		elements = Dictionary.new; // (elementName -> element)
		nodeIDs = Dictionary.new; // (elementName -> element.synth.nodeID)

		NetAddr.broadcastFlag_(true);
		destAddr = myDestAddr ? NetAddr(defaultDestIP,defaultDestPort);

		// default equalizer settings
		eqSetting = EQSetting( EQdef( 'peak', BPeakEQ, 'gain', Gain),[[1000,0.5,12],[-6]]);

		// create view
		//view = RTMLview.new;

		// osc receivers from server

		oscReceiver = OSCFunc({ arg msg, time;
			var nodeID = msg[1];
			var element = this.getElementFromNodeID(nodeID);

			if (element.isNil.not) { //avoid error when reset BeatTracker

				switch (element.class)
				{OnsetDetector} {
					if (element.testSound) {Synth(\click)};

					if (element.send) {element.sendMsg};
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
				}

				{PitchFollower} {
					var freq = msg[3];
					switch (element.mode)
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
					};

					if (element.testSound) {
						element.toneSynth.set(\freq,freq)
					};
				}
				{PeakTracker} {
					var peak = msg[3];
					peak = (peak*255).round;
					if (element.send) {element.sendMsg(peak)};
				}
				;



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


	*addElement { |element|
		var name;

		switch(element.class)
		{OnsetDetector} {
			name = "onset_" ++ numOnsetDetector;
			numOnsetDetector = numOnsetDetector + 1;
		}
		{PitchFollower} {
			name = "pitch_" ++ numPitchFollower;
			numPitchFollower = numPitchFollower + 1;
		}
		{BeatTracker} {
			name = "beat_" ++ numBeatTracker;
			numBeatTracker = numBeatTracker + 1;
		}
		{PeakTracker} {
			name = "peak_" ++ numPeakTracker;
			numPeakTracker = numPeakTracker + 1;
		};


		elements.add(name.asSymbol -> element);
		nodeIDs.add(name.asSymbol -> element.nodeID);
		numElements = numElements+1;


		// PUT OTHER CLASSES

		^name;

	}

}
